/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.security.common.authentication.credentials;

import java.util.Locale;

/**
 * 
 */
public class LanguageCredential extends AbstractCredential {

	private Locale locale;

	/**
	 * @param prompt
	 */
	public LanguageCredential(Locale locale) {
		super(null);
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

}
