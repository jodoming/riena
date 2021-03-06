/*******************************************************************************
 * Copyright (c) 2007, 2013 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.security.common;

public class NotAuthorizedFailure extends SecurityFailure {

	private static final long serialVersionUID = 4187279179455530447L;

	/**
	 * @param msg
	 */
	public NotAuthorizedFailure(final String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public NotAuthorizedFailure(final String msg, final Throwable cause) {
		super(msg, cause);
	}

}
