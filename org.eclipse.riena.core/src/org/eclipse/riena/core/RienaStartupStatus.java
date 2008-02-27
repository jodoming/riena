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
package org.eclipse.riena.core;

import org.eclipse.riena.internal.core.RienaStartupStatusSetter;

public abstract class RienaStartupStatus {

	private static RienaStartupStatus myself = new RienaStartupStatusSetter();

	/**
	 * Get the riena startup status.<br>
	 * <code>true</code> indicates that the
	 * <code>org.eclipse.riena.core</code> plugin has been started.
	 * 
	 * @return startup status
	 */
	public static RienaStartupStatus getInstance() {
		return myself;
	}

	public abstract boolean isStarted();

}
