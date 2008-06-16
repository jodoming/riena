package org.eclipse.riena.example.client.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.riena.navigation.ui.swt.lnf.LnfManager;
import org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultLnf;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

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

/**
 * 
 */
public class RienaDefaultLnfHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		LnfManager.setLnf(getNewLnf());

		Shell shell = HandlerUtil.getActiveShell(event);
		shell.setRedraw(false);
		shell.setRedraw(true);
		return null;

	}

	protected RienaDefaultLnf getNewLnf() {
		return new RienaDefaultLnf();
	}

}
