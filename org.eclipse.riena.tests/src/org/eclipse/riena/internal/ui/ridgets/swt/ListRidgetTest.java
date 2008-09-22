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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.tests.FTActionListener;
import org.eclipse.riena.tests.UITestHelper;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.ISelectableRidget;
import org.eclipse.riena.ui.ridgets.ISortableByColumn;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.ISelectableRidget.SelectionType;
import org.eclipse.riena.ui.ridgets.swt.uibinding.DefaultSwtControlRidgetMapper;
import org.eclipse.riena.ui.ridgets.util.beans.Person;
import org.eclipse.riena.ui.ridgets.util.beans.PersonManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;

/**
 * Tests of the class {@link ListRidget}.
 */
public class ListRidgetTest extends AbstractTableRidgetTest {

	@Override
	protected Control createUIControl(Composite parent) {
		return new List(parent, SWT.MULTI);
	}

	@Override
	protected IRidget createRidget() {
		return new ListRidget();
	}

	@Override
	protected List getUIControl() {
		return (List) super.getUIControl();
	}

	@Override
	protected ListRidget getRidget() {
		return (ListRidget) super.getRidget();
	}

	@Override
	protected void bindRidgetToModel() {
		getRidget().bindToModel(manager, "persons", Person.class, new String[] { "firstname" }, new String[] { "" });
	}

	// test methods
	// /////////////

	public void testRidgetMapping() {
		DefaultSwtControlRidgetMapper mapper = new DefaultSwtControlRidgetMapper();
		assertSame(ListRidget.class, mapper.getRidgetClass(getUIControl()));
	}

	public void testUpdateFromModel() {
		List control = getUIControl();
		ITableRidget ridget = getRidget();

		int oldCount = manager.getPersons().size();

		assertEquals(oldCount, ridget.getObservableList().size());
		assertEquals(oldCount, control.getItemCount());

		manager.getPersons().remove(person1);

		int newCount = oldCount - 1;

		assertEquals(newCount, manager.getPersons().size());
		assertEquals(oldCount, ridget.getObservableList().size());
		assertEquals(oldCount, control.getItemCount());

		ridget.updateFromModel();

		assertEquals(newCount, manager.getPersons().size());
		assertEquals(newCount, ridget.getObservableList().size());
		assertEquals(newCount, control.getItemCount());
	}

	public void testUpdateFromModelPreservesSelection() {
		ITableRidget ridget = getRidget();

		ridget.setSelection(person2);

		assertSame(person2, ridget.getSelection().get(0));

		manager.getPersons().remove(person1);
		ridget.updateFromModel();

		assertSame(person2, ridget.getSelection().get(0));
	}

	public void testContainsOption() {
		ITableRidget ridget = getRidget();

		assertTrue(ridget.containsOption(person1));
		assertTrue(ridget.containsOption(person2));
		assertTrue(ridget.containsOption(person3));

		assertFalse(ridget.containsOption(null));
		assertFalse(ridget.containsOption(new Person("", "")));

		java.util.List<Person> persons = Arrays.asList(new Person[] { person3 });
		PersonManager manager = new PersonManager(persons);
		getRidget().bindToModel(manager, "persons", Person.class, new String[] { "firstname" }, new String[] { "" });

		assertFalse(ridget.containsOption(person1));
		assertTrue(ridget.containsOption(person3));
	}

	public void testSetSelectionType() {
		ITableRidget ridget = getRidget();
		List control = getUIControl();

		assertEquals(SelectionType.SINGLE, ridget.getSelectionType());
		assertTrue((control.getStyle() & SWT.MULTI) != 0);

		ridget.setSelection(new int[] { 0, 1 });

		// single selection is enforced
		assertEquals(1, ridget.getSelectionIndices().length);
		assertEquals(1, control.getSelectionCount());

		// multiple selection is now allowed
		ridget.setSelectionType(SelectionType.MULTI);
		ridget.setSelection(new int[] { 0, 1 });

		assertEquals(2, ridget.getSelectionIndices().length);
		assertEquals(2, control.getSelectionCount());
	}

	public void testAddDoubleClickListener() {
		ListRidget ridget = getRidget();
		List control = getUIControl();

		try {
			ridget.addDoubleClickListener(null);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		FTActionListener listener1 = new FTActionListener();
		ridget.addDoubleClickListener(listener1);

		FTActionListener listener2 = new FTActionListener();
		ridget.addDoubleClickListener(listener2);
		ridget.addDoubleClickListener(listener2);

		Event doubleClick = new Event();
		doubleClick.widget = control;
		doubleClick.type = SWT.MouseDoubleClick;
		control.notifyListeners(SWT.MouseDoubleClick, doubleClick);

		assertEquals(1, listener1.getCount());
		assertEquals(1, listener2.getCount());

		ridget.removeDoubleClickListener(listener1);

		control.notifyListeners(SWT.MouseDoubleClick, doubleClick);

		assertEquals(1, listener1.getCount());
	}

	public void testSetComparator() {
		ListRidget ridget = getRidget();
		List control = getUIControl();

		// sorts from a to z
		Comparator<Object> comparator = new StringComparator();

		try {
			ridget.setComparator(-1, comparator);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		try {
			ridget.setComparator(1, comparator);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		int lastItemIndex = control.getItemCount() - 1;

		assertEquals("John", control.getItem(0));
		assertEquals("Frank", control.getItem(lastItemIndex));

		ridget.setComparator(0, comparator);
		ridget.setSortedColumn(0);

		assertEquals("Frank", control.getItem(0));
		assertEquals("John", control.getItem(lastItemIndex));

		ridget.setComparator(0, null);

		assertEquals("John", control.getItem(0));
		assertEquals("Frank", control.getItem(lastItemIndex));
	}

	public void testGetSortedColumn() {
		ListRidget ridget = getRidget();

		try {
			ridget.setSortedColumn(1);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		try {
			ridget.setSortedColumn(-2);
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		assertEquals(-1, ridget.getSortedColumn());

		ridget.setComparator(0, new StringComparator());

		assertEquals(-1, ridget.getSortedColumn());

		ridget.setSortedColumn(0);

		assertEquals(0, ridget.getSortedColumn());

		ridget.setComparator(0, null);

		assertEquals(-1, ridget.getSortedColumn());

		ridget.setSortedColumn(-1);

		assertEquals(-1, ridget.getSortedColumn());

		// no comparator in column 0
		ridget.setSortedColumn(0);

		assertEquals(-1, ridget.getSortedColumn());
	}

	public void testIsColumnSortable() {
		ListRidget ridget = getRidget();

		try {
			assertFalse(ridget.isColumnSortable(-1));
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		try {
			assertFalse(ridget.isColumnSortable(1));
			fail();
		} catch (RuntimeException rex) {
			// expected
		}

		assertFalse(ridget.isColumnSortable(0));

		ridget.setComparator(0, new StringComparator());

		assertTrue(ridget.isColumnSortable(0));

		ridget.setComparator(0, null);

		assertFalse(ridget.isColumnSortable(0));
	}

	public void testSetColumnSortable() {
		ListRidget ridget = getRidget();

		try {
			ridget.setColumnSortable(0, true);
			fail();
		} catch (UnsupportedOperationException uoe) {
			// expected
		}
	}

	public void testSetSortedAscending() {
		List control = getUIControl();
		ListRidget ridget = getRidget();

		ridget.bindToModel(manager, "persons", Person.class, new String[] { "lastname" }, new String[] { "" });
		int lastItemIndex = control.getItemCount() - 1;

		assertEquals(-1, ridget.getSortedColumn());
		assertTrue(ridget.isSortedAscending());

		ridget.setSortedAscending(false);

		assertFalse(ridget.isSortedAscending());

		ridget.setComparator(0, new StringComparator());
		ridget.setSortedColumn(0);

		assertFalse(ridget.isSortedAscending());

		assertEquals("Zappa", control.getItem(0));
		assertEquals("Doe", control.getItem(lastItemIndex));

		ridget.setSortedAscending(true);

		assertTrue(ridget.isSortedAscending());
		assertEquals("Doe", control.getItem(0));
		assertEquals("Zappa", control.getItem(lastItemIndex));

		ridget.setComparator(0, null);

		assertEquals(-1, ridget.getSortedColumn());
		assertTrue(ridget.isSortedAscending());
	}

	public void testSetSortedAscendingFiresEvents() {
		ListRidget ridget = getRidget();

		assertTrue(ridget.isSortedAscending());

		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORT_ASCENDING,
				Boolean.TRUE, Boolean.FALSE));

		ridget.setSortedAscending(false);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setSortedAscending(false);

		verifyPropertyChangeEvents();
		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORT_ASCENDING,
				Boolean.FALSE, Boolean.TRUE));

		ridget.setSortedAscending(true);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setSortedAscending(true);

		verifyPropertyChangeEvents();
	}

	public void testSetSortedColumnFiresEvents() {
		ListRidget ridget = getRidget();

		assertEquals(-1, ridget.getSortedColumn());

		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORTED_COLUMN, Integer
				.valueOf(-1), Integer.valueOf(0)));

		ridget.setSortedColumn(0);

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setSortedColumn(0);

		verifyPropertyChangeEvents();
		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, ISortableByColumn.PROPERTY_SORTED_COLUMN, Integer
				.valueOf(0), Integer.valueOf(-1)));

		ridget.setSortedColumn(-1);

		verifyPropertyChangeEvents();
	}

	public void testHasMoveableColumns() {
		ListRidget ridget = getRidget();

		assertFalse(ridget.hasMoveableColumns());

		try {
			ridget.setMoveableColumns(true);
			fail();
		} catch (UnsupportedOperationException ex) {
			// expected
		}
	}

	/**
	 * Tests that for single selection, the ridget selection state and the ui
	 * selection state cannot be changed by the user when ridget is set to
	 * "output only".
	 */
	public void testOutputSingleSelectionCannotBeChangedFromUI() {
		ListRidget ridget = getRidget();
		List control = getUIControl();

		ridget.setSelectionType(SelectionType.SINGLE);

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(true);
		control.setFocus();
		// press space to select row 0
		UITestHelper.sendString(control.getDisplay(), " ");

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(false);
		control.setFocus();
		// press space to select row 0
		UITestHelper.sendString(control.getDisplay(), " ");

		assertEquals(1, ridget.getSelection().size());
		assertEquals(1, control.getSelectionCount());
	}

	/**
	 * Tests that for multiple selection, the ridget selection state and the ui
	 * selection state cannot be changed by the user when ridget is set to
	 * "output only".
	 */
	public void testOutputMultipleSelectionCannotBeChangedFromUI() {
		ListRidget ridget = getRidget();
		List control = getUIControl();

		ridget.setSelectionType(SelectionType.MULTI);

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(true);
		control.setFocus();
		// press space to select row 0
		UITestHelper.sendString(control.getDisplay(), " ");

		assertEquals(0, ridget.getSelection().size());
		assertEquals(0, control.getSelectionCount());

		ridget.setOutputOnly(false);
		control.setFocus();
		// press space to select row 0
		UITestHelper.sendString(control.getDisplay(), " ");

		assertEquals(1, ridget.getSelection().size());
		assertEquals(1, control.getSelectionCount());
	}

	/**
	 * Tests that toggling output state on/off does not change the selection.
	 */
	public void testTogglingOutputDoesNotChangeSelection() {
		ListRidget ridget = getRidget();

		ridget.setSelection(0);

		assertEquals(0, ridget.getSelectionIndex());

		ridget.setOutputOnly(true);

		assertEquals(0, ridget.getSelectionIndex());

		ridget.setSelection((Object) null);

		assertEquals(-1, ridget.getSelectionIndex());

		ridget.setOutputOnly(false);

		assertEquals(-1, ridget.getSelectionIndex());
	}

	/**
	 * Tests that changing the selection in ridget works as expected, even when
	 * the ridget is disabled.
	 */
	public void testDisabledListIsEmptyFromRidget() {
		if (!MarkerSupport.HIDE_DISABLED_RIDGET_CONTENT) {
			System.out.println("Skipping ListRidgetTest.testDisabledListIsEmptyFromRidget()");
			return;
		}

		ListRidget ridget = getRidget();
		List control = getUIControl();
		// the single selection is bound to another object in the parent class
		getRidget().bindSingleSelectionToModel(manager, "selectedPerson");

		ridget.setSelection(person1);

		assertEquals(person1.getFirstname(), control.getItem(control.getSelectionIndex()));
		assertEquals(person1, ridget.getSelection().get(0));
		assertEquals(person1, manager.getSelectedPerson());

		ridget.setEnabled(false);

		assertEquals(-1, control.getSelectionIndex());
		assertEquals(person1, ridget.getSelection().get(0));
		assertEquals(person1, manager.getSelectedPerson());

		ridget.setSelection(person2);

		assertEquals(-1, control.getSelectionIndex());
		assertEquals(person2, ridget.getSelection().get(0));
		assertEquals(person2, manager.getSelectedPerson());

		ridget.setEnabled(true);

		assertEquals(person2.getFirstname(), control.getItem(control.getSelectionIndex()));
		assertEquals(person2, ridget.getSelection().get(0));
		assertEquals(person2, manager.getSelectedPerson());
	}

	/**
	 * Tests that changing the selection in a bound model works as expected,
	 * even when the ridget is disabled.
	 */
	public void testDisabledListIsEmptyFromModel() {
		if (!MarkerSupport.HIDE_DISABLED_RIDGET_CONTENT) {
			System.out.println("Skipping ListRidgetTest.testDisabledListIsEmptyFromModel()");
			return;
		}

		ListRidget ridget = getRidget();
		List control = getUIControl();
		// the single selection is bound to another object in the parent class
		getRidget().bindSingleSelectionToModel(manager, "selectedPerson");

		manager.setSelectedPerson(person1);
		ridget.updateSingleSelectionFromModel();

		assertEquals(person1.getFirstname(), control.getItem(control.getSelectionIndex()));
		assertEquals(person1, ridget.getSelection().get(0));
		assertEquals(person1, manager.getSelectedPerson());

		ridget.setEnabled(false);

		assertEquals(-1, control.getSelectionIndex());
		assertEquals(person1, ridget.getSelection().get(0));
		assertEquals(person1, manager.getSelectedPerson());

		manager.setSelectedPerson(person2);
		ridget.updateSingleSelectionFromModel();

		assertEquals(-1, control.getSelectionIndex());
		assertEquals(person2, ridget.getSelection().get(0));
		assertEquals(person2, manager.getSelectedPerson());

		ridget.setEnabled(true);

		assertEquals(person2.getFirstname(), control.getItem(control.getSelectionIndex()));
		assertEquals(person2, ridget.getSelection().get(0));
		assertEquals(person2, manager.getSelectedPerson());
	}

	/**
	 * Tests that disabling / enabling the ridget does not fire selection events
	 * (because the list is modified internally).
	 */
	public void testDisabledDoesNotFireSelection() {
		ListRidget ridget = getRidget();
		ridget.setSelectionType(ISelectableRidget.SelectionType.MULTI);
		FTPropertyChangeListener listener = new FTPropertyChangeListener();
		ridget.addPropertyChangeListener(ISelectableRidget.PROPERTY_SELECTION, listener);

		ridget.setSelection(person1);
		int count = listener.getCount();

		ridget.setEnabled(false);

		assertEquals(count, listener.getCount());

		ridget.setSelection(person2);
		count = listener.getCount();

		ridget.setEnabled(true);

		assertEquals(count, listener.getCount());
	}

	/**
	 * Tests that disabling / enabling works as intended, even when no model is
	 * bound to the ridget.
	 */
	public void testDisableWithoutBoundModel() {
		ListRidget ridget = (ListRidget) createRidget();
		List control = getUIControl();
		ridget.setUIControl(control);

		assertNull(ridget.getObservableList());

		ridget.setEnabled(false);

		assertFalse(ridget.isEnabled());
		assertFalse(control.isEnabled());

		ridget.setEnabled(true);

		assertTrue(ridget.isEnabled());
		assertTrue(control.isEnabled());
	}

	/**
	 * Check that disabling / enabling works when we don't have a bound control.
	 */
	public void testDisableWithoutUIControl() {
		ListRidget ridget = getRidget();
		ridget.setUIControl(null);

		ridget.setEnabled(false);

		assertFalse(ridget.isEnabled());

		ridget.setEnabled(true);

		assertTrue(ridget.isEnabled());
	}

	/**
	 * Tests that the disabled state is applied to a new control when set into
	 * the ridget.
	 */
	public void testDisableAndClearOnBind() {
		if (!MarkerSupport.HIDE_DISABLED_RIDGET_CONTENT) {
			System.out.println("Skipping ListRidgetTest.testDisableAndClearOnBind()");
			return;
		}

		ListRidget ridget = getRidget();
		List control = getUIControl();
		// the single selection is bound to another object in the parent class
		getRidget().bindSingleSelectionToModel(manager, "selectedPerson");

		ridget.setUIControl(null);
		ridget.setEnabled(false);
		manager.setSelectedPerson(person1);
		ridget.updateSingleSelectionFromModel();
		ridget.setUIControl(control);

		assertFalse(control.isEnabled());
		assertEquals("", control.getItem(0)); // items 'hidden' == ""
		assertEquals(-1, control.getSelectionIndex()); // no selection
		assertEquals(person1, ridget.getSelection().get(0));

		ridget.setEnabled(true);

		assertTrue(control.isEnabled());
		assertTrue(control.getItem(0).length() > 0); // ! ""
		assertTrue(control.getSelectionIndex() > -1); // selection was restored
		assertEquals(manager.getPersons().size(), control.getItemCount());
		assertEquals(person1, ridget.getSelection().get(0));
	}

	// helping methods
	// ////////////////

	@Override
	protected void clearUIControlRowSelection() {
		getUIControl().deselectAll();
		fireSelectionEvent();
	}

	@Override
	protected int getUIControlSelectedRowCount() {
		return getUIControl().getSelectionCount();
	}

	@Override
	protected int getUIControlSelectedRow() {
		return getUIControl().getSelectionIndex();
	}

	@Override
	protected Object getRowValue(int i) {
		// return getRidget().getRowObservables().get(i);
		IObservableList rowObservables = ReflectionUtils.invokeHidden(getRidget(), "getRowObservables");
		return rowObservables.get(i);
	}

	@Override
	protected int[] getSelectedRows() {
		// IObservableList rowObservables = getRidget().getRowObservables();
		IObservableList rowObservables = ReflectionUtils.invokeHidden(getRidget(), "getRowObservables");
		Object[] elements = getRidget().getMultiSelectionObservable().toArray();
		int[] result = new int[elements.length];
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			result[i] = rowObservables.indexOf(element);
		}
		return result;
	}

	@Override
	protected int[] getUIControlSelectedRows() {
		return getUIControl().getSelectionIndices();
	}

	@Override
	protected void setUIControlRowSelection(int[] indices) {
		getUIControl().setSelection(indices);
		fireSelectionEvent();
	}

	@Override
	protected void setUIControlRowSelectionInterval(int start, int end) {
		getUIControl().setSelection(start, end);
		fireSelectionEvent();
	}

	// helping classes
	// ////////////////

	/**
	 * Compares two strings.
	 */
	private static final class StringComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.compareTo(s2);
		}
	}

	/**
	 * PropertyChangeListener that counts how often it is invoked.
	 */
	private static final class FTPropertyChangeListener implements PropertyChangeListener {

		private int count;

		public int getCount() {
			return count;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			count++;
			// System.out.println(count + "\t" + evt.getOldValue() + " -> " + evt.getNewValue());
		}
	}

}
