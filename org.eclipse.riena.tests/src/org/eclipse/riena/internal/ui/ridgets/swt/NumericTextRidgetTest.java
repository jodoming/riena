/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.util.Collection;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.riena.core.marker.IMarker;
import org.eclipse.riena.tests.TestUtils;
import org.eclipse.riena.tests.UITestHelper;
import org.eclipse.riena.ui.core.marker.ErrorMarker;
import org.eclipse.riena.ui.core.marker.IMessageMarker;
import org.eclipse.riena.ui.core.marker.ValidationTime;
import org.eclipse.riena.ui.ridgets.INumericValueTextFieldRidget;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.ITextFieldRidget;
import org.eclipse.riena.ui.ridgets.swt.uibinding.DefaultSwtControlRidgetMapper;
import org.eclipse.riena.ui.ridgets.util.beans.IntegerBean;
import org.eclipse.riena.ui.ridgets.validation.MaxLength;
import org.eclipse.riena.ui.ridgets.validation.MinLength;
import org.eclipse.riena.ui.ridgets.validation.ValidationFailure;
import org.eclipse.riena.ui.ridgets.validation.ValidationRuleStatus;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

/**
 * Tests for the class {@link NumericTextRidget}.
 */
public class NumericTextRidgetTest extends TextRidgetTest {

	private static final Integer INTEGER_ONE = Integer.valueOf(471);
	private static final Integer INTEGER_TWO = Integer.valueOf(23);

	@Override
	protected IRidget createRidget() {
		return new NumericTextRidget();
	}

	@Override
	protected INumericValueTextFieldRidget getRidget() {
		return (INumericValueTextFieldRidget) super.getRidget();
	}

	@Override
	protected Control createUIControl(Composite parent) {
		Control result = new Text(getShell(), SWT.RIGHT | SWT.BORDER | SWT.SINGLE);
		result.setData(UIControlsFactory.KEY_TYPE, UIControlsFactory.TYPE_NUMERIC);
		result.setLayoutData(new RowData(100, SWT.DEFAULT));
		return result;
	}

	// test methods
	///////////////

	@Override
	public void testRidgetMapping() {
		DefaultSwtControlRidgetMapper mapper = new DefaultSwtControlRidgetMapper();
		assertSame(NumericTextRidget.class, mapper.getRidgetClass(getUIControl()));
	}

	@Override
	public void testCreate() throws Exception {
		assertFalse(getRidget().isDirectWriting());
		assertEquals("0", getRidget().getText());
	}

	@Override
	public void testSetText() throws Exception {
		INumericValueTextFieldRidget ridget = getRidget();
		try {
			ridget.setText("0");
			fail();
		} catch (UnsupportedOperationException exc) {
			// expected
		}
	}

	@Override
	public void testGetText() throws Exception {
		ITextFieldRidget ridget = getRidget();

		assertEquals("0", ridget.getText());
	}

	@Override
	public void testBindToModelPropertyName() {
		ITextFieldRidget ridget = getRidget();
		IntegerBean model = new IntegerBean(1337);
		ridget.bindToModel(model, IntegerBean.PROP_VALUE);

		assertEquals("0", ridget.getText());

		ridget.updateFromModel();

		assertEquals(TestUtils.getLocalizedNumber("1.337"), ridget.getText());
	}

	@Override
	public void testUpdateFromModel() {
		ITextFieldRidget ridget = getRidget();
		IntegerBean model = new IntegerBean(1337);
		ridget.bindToModel(model, IntegerBean.PROP_VALUE);

		model.setValue(-7);
		ridget.updateFromModel();

		assertEquals(TestUtils.getLocalizedNumber("-7"), ridget.getText());
	}

	@Override
	public void testBindToModelIObservableValue() throws Exception {
		ITextFieldRidget ridget = getRidget();

		IntegerBean model = new IntegerBean(4711);
		IObservableValue modelOV = BeansObservables.observeValue(model, IntegerBean.PROP_VALUE);
		ridget.bindToModel(modelOV);

		assertEquals("0", ridget.getText());

		ridget.updateFromModel();

		assertEquals(TestUtils.getLocalizedNumber("4.711"), ridget.getText());
	}

	@Override
	public void testFocusGainedDoesSelectOnSingleText() {
		Text control = getUIControl();

		assertEquals("0", control.getSelectionText());
		control.setSelection(0, 0);

		Event e = new Event();
		e.type = SWT.FocusIn;
		e.widget = control;
		e.widget.notifyListeners(e.type, e);

		assertEquals(0, control.getStyle() & SWT.MULTI);
		assertEquals("0", control.getSelectionText());
	}

	@Override
	public void testFocusGainedDoesNotSelectOnMultiLineText() {
		// override test in superclass; multiline is not supported
		assertTrue(true);
	}

	public void testCheckWidget() {
		ITextFieldRidget ridget = getRidget();
		Text control = new Text(getShell(), SWT.MULTI);

		try {
			ridget.setUIControl(control);
			fail();
		} catch (RuntimeException exc) {
			// expected;
		}

		try {
			ridget.setUIControl(new Button(getShell(), SWT.PUSH));
			fail();
		} catch (RuntimeException exc) {
			// expected;
		}
	}

	public void testSetSignedTrue() {
		INumericValueTextFieldRidget ridget = getRidget();
		Text control = getUIControl();
		IntegerBean model = new IntegerBean(1337);
		ridget.bindToModel(model, IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertTrue(ridget.isSigned());

		expectNoPropertyChangeEvent();
		ridget.setSigned(true);

		verifyPropertyChangeEvents();
		assertTrue(ridget.isSigned());

		int caretPos = control.getText().length() - 1;
		focusIn(control);
		control.setSelection(caretPos, caretPos);

		assertEquals(TestUtils.getLocalizedNumber("1.337"), control.getText());
		assertEquals(caretPos, control.getCaretPosition());

		UITestHelper.sendString(control.getDisplay(), "-");

		assertEquals(TestUtils.getLocalizedNumber("-1.337"), control.getText());
		assertEquals(caretPos + 1, control.getCaretPosition());

		control.setSelection(1, 1);
		UITestHelper.sendString(control.getDisplay(), "\b");

		assertEquals(TestUtils.getLocalizedNumber("1.337"), control.getText());
		assertEquals(0, control.getCaretPosition());
	}

	public void testSetSignedFalse() {
		INumericValueTextFieldRidget ridget = getRidget();
		Text control = getUIControl();
		IntegerBean model = new IntegerBean(1337);
		ridget.bindToModel(model, IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertTrue(ridget.isSigned());

		expectPropertyChangeEvent(INumericValueTextFieldRidget.PROPERTY_SIGNED, Boolean.TRUE, Boolean.FALSE);
		ridget.setSigned(false);

		verifyPropertyChangeEvents();
		assertFalse(ridget.isSigned());

		int caretPos = control.getText().length() - 1;
		focusIn(control);
		control.setSelection(caretPos, caretPos);

		assertEquals(TestUtils.getLocalizedNumber("1.337"), control.getText());
		assertEquals(caretPos, control.getCaretPosition());

		UITestHelper.sendString(control.getDisplay(), "-");

		assertEquals(TestUtils.getLocalizedNumber("1.337"), control.getText());
		assertEquals(caretPos, control.getCaretPosition());
	}

	public void testSetGrouping() {
		INumericValueTextFieldRidget ridget = getRidget();
		IntegerBean model = new IntegerBean(1337);
		ridget.bindToModel(model, IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertTrue(ridget.isGrouping());
		assertEquals(TestUtils.getLocalizedNumber("1.337"), ridget.getText());

		ridget.setGrouping(false);

		assertFalse(ridget.isGrouping());
		assertEquals("1337", ridget.getText());

		ridget.setGrouping(true);

		assertTrue(ridget.isGrouping());
		assertEquals(TestUtils.getLocalizedNumber("1.337"), ridget.getText());
	}

	public void testUpdateFromControlUserInput() throws Exception {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();
		Display display = control.getDisplay();
		IntegerBean bean = new IntegerBean();
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);

		assertFalse(ridget.isDirectWriting());

		UITestHelper.sendString(display, "47");

		assertEquals("47", control.getText());
		assertEquals("0", ridget.getText());
		assertEquals(Integer.valueOf(0), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, "0", "47");

		UITestHelper.sendString(display, "\r");
		UITestHelper.readAndDispatch(control);

		verifyPropertyChangeEvents();
		assertEquals("47", control.getText());
		assertEquals("47", ridget.getText());
		assertEquals(Integer.valueOf(47), bean.getValue());

		expectNoPropertyChangeEvent();

		UITestHelper.sendString(display, "1");

		verifyPropertyChangeEvents();
		assertEquals("471", control.getText());
		assertEquals("47", ridget.getText());
		assertEquals(Integer.valueOf(47), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, "47", "471");

		UITestHelper.sendString(display, "\t");

		verifyPropertyChangeEvents();
		assertEquals("471", control.getText());
		assertEquals("471", ridget.getText());
		assertEquals(Integer.valueOf(471), bean.getValue());
	}

	public void testUpdateFromControlUserInputDirectWriting() {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		IntegerBean bean = new IntegerBean();
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);
		ridget.setDirectWriting(true);

		Display display = control.getDisplay();
		UITestHelper.sendString(display, "4");

		assertEquals("4", control.getText());
		assertEquals("4", ridget.getText());
		assertEquals(Integer.valueOf(4), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, "4", "47");

		UITestHelper.sendString(display, "7");

		verifyPropertyChangeEvents();
		assertEquals("47", control.getText());
		assertEquals("47", ridget.getText());
		assertEquals(Integer.valueOf(47), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, "47", "471");

		UITestHelper.sendString(display, "1");

		verifyPropertyChangeEvents();
		assertEquals("471", control.getText());
		assertEquals("471", ridget.getText());
		assertEquals(Integer.valueOf(471), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, "471", TestUtils.getLocalizedNumber("4.711"));

		UITestHelper.sendString(display, "1");

		verifyPropertyChangeEvents();
		assertEquals(TestUtils.getLocalizedNumber("4.711"), control.getText());
		assertEquals(TestUtils.getLocalizedNumber("4.711"), ridget.getText());
		assertEquals(Integer.valueOf(4711), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, TestUtils.getLocalizedNumber("4.711"), "471");

		UITestHelper.sendKeyAction(display, SWT.ARROW_LEFT);
		UITestHelper.sendString(display, "\b");

		verifyPropertyChangeEvents();
		assertEquals("471", control.getText());
		assertEquals("471", ridget.getText());
		assertEquals(Integer.valueOf("471"), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, "471", "47");

		UITestHelper.sendString(display, String.valueOf(SWT.DEL));

		verifyPropertyChangeEvents();
		assertEquals("47", control.getText());
		assertEquals("47", ridget.getText());
		assertEquals(Integer.valueOf(47), bean.getValue());

		expectNoPropertyChangeEvent();

		bean.setValue(Integer.valueOf(4711));

		verifyPropertyChangeEvents();
		assertEquals("47", control.getText());
		assertEquals("47", ridget.getText());
		assertEquals(Integer.valueOf(4711), bean.getValue());

		expectPropertyChangeEvent(ITextFieldRidget.PROPERTY_TEXT, "47", "4");

		UITestHelper.sendString(display, "\b");

		verifyPropertyChangeEvents();
		assertEquals("4", control.getText());
		assertEquals("4", ridget.getText());
		assertEquals(Integer.valueOf("4"), bean.getValue());
	}

	public void testUpdateFromRidgetOnRebind() throws Exception {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		IntegerBean bean = new IntegerBean();
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);
		bean.setValue(INTEGER_ONE);
		ridget.updateFromModel();

		assertEquals(INTEGER_ONE.toString(), control.getText());
		assertEquals(INTEGER_ONE.toString(), ridget.getText());
		assertEquals(INTEGER_ONE, bean.getValue());

		// unbind, e.g. when the view is used by another controller
		ridget.setUIControl(null);

		control.selectAll();
		UITestHelper.sendString(control.getDisplay(), "99");

		assertEquals("99", control.getText());
		assertEquals(INTEGER_ONE.toString(), ridget.getText());
		assertEquals(INTEGER_ONE, bean.getValue());

		// rebind
		ridget.setUIControl(control);

		assertEquals(INTEGER_ONE.toString(), control.getText());
		assertEquals(INTEGER_ONE.toString(), ridget.getText());
		assertEquals(INTEGER_ONE, bean.getValue());

		// unbind again
		ridget.setUIControl(null);

		bean.setValue(INTEGER_TWO);
		ridget.updateFromModel();

		assertEquals(INTEGER_ONE.toString(), control.getText());
		assertEquals(INTEGER_TWO.toString(), ridget.getText());
		assertEquals(INTEGER_TWO, bean.getValue());

		// rebind
		ridget.setUIControl(control);

		assertEquals(INTEGER_TWO.toString(), control.getText());
		assertEquals(INTEGER_TWO.toString(), ridget.getText());
		assertEquals(INTEGER_TWO, bean.getValue());
	}

	public void testValidationOnUpdateToModel() throws Exception {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		IntegerBean bean = new IntegerBean();
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);

		ridget.addValidationRule(new MinLength(3), ValidationTime.ON_UPDATE_TO_MODEL);

		bean.setValue(INTEGER_ONE);
		ridget.updateFromModel();

		assertTrue(ridget.getMarkersOfType(ErrorMarker.class).isEmpty());
		assertEquals(INTEGER_ONE.toString(), ridget.getText());

		control.selectAll();
		UITestHelper.sendString(control.getDisplay(), "99\t");

		assertFalse(ridget.getMarkersOfType(ErrorMarker.class).isEmpty());
		assertEquals("99", ridget.getText());

		focusIn(control);
		UITestHelper.sendKeyAction(control.getDisplay(), SWT.END);
		UITestHelper.sendString(control.getDisplay(), "9");

		assertFalse(ridget.getMarkersOfType(ErrorMarker.class).isEmpty());

		UITestHelper.sendString(control.getDisplay(), "\r");

		assertTrue(ridget.getMarkersOfType(ErrorMarker.class).isEmpty());
		assertEquals("999", ridget.getText());
	}

	public void testCharactersAreBlockedInControl() throws Exception {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		IntegerBean bean = new IntegerBean();
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);
		ridget.setDirectWriting(true);

		UITestHelper.sendString(control.getDisplay(), "12");

		assertEquals("12", control.getText());
		assertEquals("12", ridget.getText());
		assertEquals(Integer.valueOf(12), bean.getValue());

		UITestHelper.sendString(control.getDisplay(), "foo");

		assertEquals("12", control.getText());
		assertEquals("12", ridget.getText());
		assertEquals(Integer.valueOf(12), bean.getValue());
	}

	public void testValidationOnUpdateFromModelWithOnEditRule() {
		ITextFieldRidget ridget = getRidget();
		IntegerBean bean = new IntegerBean();
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);

		assertFalse(ridget.isErrorMarked());

		ridget.addValidationRule(new MaxLength(5), ValidationTime.ON_UI_CONTROL_EDIT);
		bean.setValue(Integer.valueOf(123456));
		ridget.updateFromModel();

		assertTrue(ridget.isErrorMarked());
		assertEquals(TestUtils.getLocalizedNumber("123.456"), ridget.getText());
		assertEquals(TestUtils.getLocalizedNumber("123.456"), getUIControl().getText());
		assertEquals(Integer.valueOf(123456), bean.getValue());

		bean.setValue(Integer.valueOf(1234));
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());
		assertEquals(TestUtils.getLocalizedNumber("1.234"), ridget.getText());
		assertEquals(TestUtils.getLocalizedNumber("1.234"), getUIControl().getText());
		assertEquals(Integer.valueOf(1234), bean.getValue());
	}

	public void testValidationOnUpdateFromModelWithOnUpdateRule() {
		ITextFieldRidget ridget = getRidget();
		IntegerBean bean = new IntegerBean(Integer.valueOf(123456));
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());

		ridget.addValidationRule(new MinLength(5), ValidationTime.ON_UPDATE_TO_MODEL);
		bean.setValue(Integer.valueOf(123));
		ridget.updateFromModel();

		assertTrue(ridget.isErrorMarked());
		assertEquals("123", ridget.getText());
		assertEquals("123", getUIControl().getText());
		assertEquals(Integer.valueOf(123), bean.getValue());

		bean.setValue(Integer.valueOf(1234));
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());
		assertEquals(TestUtils.getLocalizedNumber("1.234"), ridget.getText());
		assertEquals(TestUtils.getLocalizedNumber("1.234"), getUIControl().getText());
		assertEquals(Integer.valueOf(1234), bean.getValue());
	}

	public void testUpdateFromRidgetWithValidationOnEditRule() {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		IntegerBean bean = new IntegerBean(Integer.valueOf(1234));
		ridget.addValidationRule(new MinLength(5), ValidationTime.ON_UI_CONTROL_EDIT);
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);

		assertFalse(ridget.isErrorMarked());
		assertFalse(ridget.isDirectWriting());

		UITestHelper.sendString(control.getDisplay(), "98765\t");

		assertFalse(ridget.isErrorMarked());
		assertEquals(TestUtils.getLocalizedNumber("98.765"), ridget.getText());
		assertEquals(Integer.valueOf(98765), bean.getValue());

		focusIn(control);
		control.selectAll();
		// \t triggers update
		UITestHelper.sendString(control.getDisplay(), "12\t");

		assertTrue(ridget.isErrorMarked());
		// MinLength is non-blocking, so we expected '12' in ridget
		assertEquals("12", ridget.getText());
		assertEquals(Integer.valueOf(98765), bean.getValue());

		focusIn(control);
		control.selectAll();
		UITestHelper.sendString(control.getDisplay(), "43210\t");

		assertFalse(ridget.isErrorMarked());
		assertEquals(TestUtils.getLocalizedNumber("43.210"), ridget.getText());
		assertEquals(Integer.valueOf(43210), bean.getValue());
	}

	public void testUpdateFromRidgetWithValidationOnUpdateRule() {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		IntegerBean bean = new IntegerBean();
		ridget.addValidationRule(new EndsWithFive(), ValidationTime.ON_UPDATE_TO_MODEL);
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);

		assertFalse(ridget.isErrorMarked());
		assertFalse(ridget.isDirectWriting());

		UITestHelper.sendString(control.getDisplay(), "98765\t");

		assertFalse(ridget.isErrorMarked());
		assertEquals(TestUtils.getLocalizedNumber("98.765"), ridget.getText());
		assertEquals(Integer.valueOf(98765), bean.getValue());

		focusIn(control);
		control.selectAll();
		// \t triggers update
		UITestHelper.sendString(control.getDisplay(), "98\t");

		assertTrue(ridget.isErrorMarked());
		assertEquals("98", ridget.getText());
		assertEquals(Integer.valueOf(98765), bean.getValue());

		focusIn(control);
		control.setSelection(2, 2);
		UITestHelper.sendString(control.getDisplay(), "555\t");

		assertFalse(ridget.isErrorMarked());
		assertEquals(TestUtils.getLocalizedNumber("98.555"), ridget.getText());
		assertEquals(Integer.valueOf(98555), bean.getValue());
	}

	public void testValidationMessageWithOnEditRule() throws Exception {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		ridget.addValidationRule(new EvenNumberOfCharacters(), ValidationTime.ON_UI_CONTROL_EDIT);
		ridget.setDirectWriting(true);

		ridget.addValidationMessage("ValidationErrorMessage");

		assertEquals(0, ridget.getMarkers().size());

		UITestHelper.sendString(control.getDisplay(), "1");

		assertEquals(2, ridget.getMarkers().size());
		assertEquals("ValidationErrorMessage", getMessageMarker(ridget.getMarkers()).getMessage());

		UITestHelper.sendString(control.getDisplay(), "2");

		assertEquals(0, ridget.getMarkers().size());
	}

	public void testValidationMessageWithOnUpdateRule() throws Exception {
		Text control = getUIControl();
		ITextFieldRidget ridget = getRidget();

		ridget.addValidationRule(new EvenNumberOfCharacters(), ValidationTime.ON_UPDATE_TO_MODEL);
		ridget.setDirectWriting(true);

		ridget.addValidationMessage("ValidationErrorMessage");

		assertEquals(0, ridget.getMarkers().size());

		// \r triggers update
		UITestHelper.sendString(control.getDisplay(), "1\r");

		assertEquals(2, ridget.getMarkers().size());
		assertEquals("ValidationErrorMessage", getMessageMarker(ridget.getMarkers()).getMessage());

		// \r triggers update
		UITestHelper.sendString(control.getDisplay(), "2\r");

		assertEquals(0, ridget.getMarkers().size());
	}

	public void testRevalidateOnEditRule() {
		ITextFieldRidget ridget = getRidget();

		ridget.bindToModel(new IntegerBean(123), IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());

		IValidator rule = new EvenNumberOfCharacters();
		ridget.addValidationRule(rule, ValidationTime.ON_UI_CONTROL_EDIT);

		assertFalse(ridget.isErrorMarked());

		boolean isOk1 = ridget.revalidate();

		assertFalse(isOk1);
		assertTrue(ridget.isErrorMarked());

		ridget.removeValidationRule(rule);

		assertTrue(ridget.isErrorMarked());

		boolean isOk2 = ridget.revalidate();

		assertTrue(isOk2);
		assertFalse(ridget.isErrorMarked());
	}

	public void testRevalidateOnUpdateRule() {
		ITextFieldRidget ridget = getRidget();

		ridget.bindToModel(new IntegerBean(123), IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());

		IValidator rule = new EvenNumberOfCharacters();
		ridget.addValidationRule(rule, ValidationTime.ON_UPDATE_TO_MODEL);

		assertFalse(ridget.isErrorMarked());

		boolean isOk1 = ridget.revalidate();

		assertFalse(isOk1);
		assertTrue(ridget.isErrorMarked());

		ridget.removeValidationRule(rule);

		assertTrue(ridget.isErrorMarked());

		boolean isOk2 = ridget.revalidate();

		assertTrue(isOk2);
		assertFalse(ridget.isErrorMarked());
	}

	public void testRevalidateDoesUpdate() {
		ITextFieldRidget ridget = getRidget();
		Text control = getUIControl();
		EvenNumberOfCharacters evenChars = new EvenNumberOfCharacters();
		ridget.addValidationRule(evenChars, ValidationTime.ON_UI_CONTROL_EDIT);

		IntegerBean bean = new IntegerBean(12);
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());

		focusIn(control);
		control.selectAll();
		UITestHelper.sendString(control.getDisplay(), "345\t");
		assertEquals("345", control.getText());
		// non-blocking rule, expect 'abc'
		assertEquals("345", ridget.getText());
		assertEquals(Integer.valueOf(12), bean.getValue());

		assertTrue(ridget.isErrorMarked());

		ridget.removeValidationRule(evenChars);
		ridget.revalidate();

		assertFalse(ridget.isErrorMarked());
		assertEquals("345", control.getText());
		assertEquals("345", ridget.getText());
		assertEquals(Integer.valueOf(345), bean.getValue());
	}

	public void testReValidationOnUpdateFromModel() {
		ITextFieldRidget ridget = getRidget();

		IntegerBean bean = new IntegerBean(12);
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());
		assertEquals("12", ridget.getText());

		IValidator rule = new EvenNumberOfCharacters();
		ridget.addValidationRule(rule, ValidationTime.ON_UI_CONTROL_EDIT);
		bean.setValue(Integer.valueOf(321));
		ridget.updateFromModel();

		assertTrue(ridget.isErrorMarked());
		assertEquals(Integer.valueOf(321), bean.getValue());
		assertEquals("321", ridget.getText());

		ridget.removeValidationRule(rule);
		ridget.updateFromModel();

		assertFalse(ridget.isErrorMarked());
		assertEquals(Integer.valueOf(321), bean.getValue());
		assertEquals("321", ridget.getText());
	}

	public void testControlNotEditableWithOutputMarker() {
		ITextFieldRidget ridget = getRidget();
		Text control = getUIControl();

		assertTrue(control.getEditable());

		ridget.setOutputOnly(true);

		assertFalse(control.getEditable());

		ridget.setOutputOnly(false);

		assertTrue(control.getEditable());
	}

	public void testOutputMultipleSelectionCannotBeChangedFromUI() {
		ITextFieldRidget ridget = getRidget();
		Text control = getUIControl();

		assertEquals("0", control.getText());
		assertEquals("0", ridget.getText());

		ridget.setOutputOnly(true);
		control.selectAll();
		focusIn(control);
		UITestHelper.sendString(control.getDisplay(), "123\t");

		assertEquals("0", control.getText());
		assertEquals("0", ridget.getText());

		ridget.setOutputOnly(false);
		control.selectAll();
		focusIn(control);
		UITestHelper.sendString(control.getDisplay(), "123\t");

		assertEquals("123", control.getText());
		assertEquals("123", ridget.getText());
	}

	/**
	 * Test that null is not allowed in {@code setText(string)}.
	 */
	public void testSetTextNull() {
		ITextFieldRidget ridget = getRidget();
		try {
			ridget.setText(null);
			fail();
		} catch (RuntimeException rex) {
			// ok
		}
	}

	public void testDisabledHasNoTextFromModel() {
		if (!MarkerSupport.HIDE_DISABLED_RIDGET_CONTENT) {
			System.out.println("Skipping TextRidgetTest2.testDisabledHasNoTextFromModel()");
			return;
		}

		ITextFieldRidget ridget = getRidget();
		Text control = getUIControl();
		IntegerBean bean = new IntegerBean(INTEGER_TWO);
		ridget.bindToModel(bean, IntegerBean.PROP_VALUE);
		ridget.updateFromModel();

		assertTrue(ridget.isEnabled());
		assertEquals(INTEGER_TWO.toString(), control.getText());
		assertEquals(INTEGER_TWO.toString(), ridget.getText());
		assertEquals(INTEGER_TWO, bean.getValue());

		ridget.setEnabled(false);

		assertEquals("", control.getText());
		assertEquals(INTEGER_TWO.toString(), ridget.getText());
		assertEquals(INTEGER_TWO, bean.getValue());

		bean.setValue(INTEGER_ONE);
		ridget.updateFromModel();

		assertEquals("", control.getText());
		assertEquals(INTEGER_ONE.toString(), ridget.getText());
		assertEquals(INTEGER_ONE, bean.getValue());

		ridget.setEnabled(true);

		assertEquals(INTEGER_ONE.toString(), control.getText());
		assertEquals(INTEGER_ONE.toString(), ridget.getText());
		assertEquals(INTEGER_ONE, bean.getValue());
	}

	// TODO [ev] fixme
	//	public void testMaxLength() throws Exception {
	//		ITextFieldRidget ridget = getRidget();
	//		Text control = getUIControl();
	//
	//		ridget.addValidationRule(new MaxNumberLength(5), ValidationTime.ON_UI_CONTROL_EDIT);
	//
	//		focusIn(control);
	//		UITestHelper.sendString(control.getDisplay(), "1234");
	//
	//		assertEquals(TestUtils.getLocalizedNumber("1.234"), control.getText());
	//
	//		focusOut(control);
	//
	//		assertEquals(TestUtils.getLocalizedNumber("1.234"), ridget.getText());
	//
	//		focusIn(control);
	//		control.setSelection(control.getText().length()); // move cursor to end
	//		UITestHelper.sendString(control.getDisplay(), "5");
	//
	//		assertEquals(TestUtils.getLocalizedNumber("12.345"), control.getText());
	//
	//		focusOut(control);
	//
	//		assertEquals(TestUtils.getLocalizedNumber("12.345"), control.getText());
	//
	//		focusIn(control);
	//		control.setSelection(control.getText().length()); // move cursor to end
	//		UITestHelper.sendString(control.getDisplay(), "6");
	//
	//		assertEquals(TestUtils.getLocalizedNumber("12.345"), control.getText());
	//
	//		focusOut(control);
	//
	//		assertEquals(TestUtils.getLocalizedNumber("12.345"), control.getText());
	//	}

	// helping methods
	//////////////////

	private void focusIn(Text control) {
		control.setFocus();
		assertTrue(control.isFocusControl());
	}

	private void focusOut(Text control) {
		// clear focus
		UITestHelper.sendString(control.getDisplay(), "\t");
		assertFalse(control.isFocusControl());
	}

	private IMessageMarker getMessageMarker(Collection<? extends IMarker> markers) {
		for (IMarker marker : markers) {
			if (marker instanceof IMessageMarker) {
				return (IMessageMarker) marker;
			}
		}
		return null;
	}

	// helping classes
	//////////////////

	private static final class EvenNumberOfCharacters implements IValidator {

		public IStatus validate(final Object value) {
			if (value == null) {
				return ValidationRuleStatus.ok();
			}
			if (value instanceof String) {
				final String string = (String) value;
				if (string.length() % 2 == 0) {
					return ValidationRuleStatus.ok();
				}
				return ValidationRuleStatus.error(false, "Odd number of characters.", this);
			}
			throw new ValidationFailure(getClass().getName() + " can only validate objects of type "
					+ String.class.getName());
		}

	}

	private static final class EndsWithFive implements IValidator {

		public IStatus validate(Object value) {
			boolean isOk = false;
			String s = null;
			if (value instanceof Number) {
				s = ((Number) value).toString();
			} else if (value instanceof String) {
				s = (String) value;
			}
			if (s != null) {
				char lastChar = s.charAt(s.length() - 1);
				isOk = lastChar == '5';
			}
			return isOk ? Status.OK_STATUS : Status.CANCEL_STATUS;
		}
	}
}
