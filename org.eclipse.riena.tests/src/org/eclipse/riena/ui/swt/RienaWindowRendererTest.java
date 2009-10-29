/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.swt;

import junit.framework.TestCase;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.riena.internal.core.test.collect.UITestCase;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf;
import org.eclipse.riena.ui.swt.utils.SwtUtilities;

/**
 * Tests of the class {@link RienaWindowRenderer}.
 */
@UITestCase
public class RienaWindowRendererTest extends TestCase {

	private RienaWindowRenderer renderer;
	private Shell shell;
	private MyDialog dialog;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		shell = new Shell();
		dialog = new MyDialog(shell);
		renderer = new RienaWindowRenderer(dialog);
		LnfManager.setLnf(new MyLnf());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		renderer = null;
		SwtUtilities.disposeWidget(shell);
		dialog = null;
	}

	/**
	 * Tests (parts) of the method {@code computeShellStyle()}.
	 */
	public void testComputeShellStyle() {

		dialog.setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.DIALOG_TRIM);
		int ret = renderer.computeShellStyle();
		assertEquals(SWT.CLOSE, ret & SWT.CLOSE);
		assertEquals(SWT.MIN, ret & SWT.MIN);
		assertEquals(SWT.MAX, ret & SWT.MAX);

	}

	private class MyDialog extends Dialog {

		public MyDialog(Shell shell) {
			super(shell);
		}

		@Override
		public void setShellStyle(int newShellStyle) {
			super.setShellStyle(newShellStyle);
		}

	}

	private class MyLnf extends RienaDefaultLnf {

		@Override
		protected void initSettingsDefaults() {
			super.initSettingsDefaults();
			getSettingTable().put(LnfKeyConstants.DIALOG_HIDE_OS_BORDER, Boolean.TRUE);
		}

	}

}