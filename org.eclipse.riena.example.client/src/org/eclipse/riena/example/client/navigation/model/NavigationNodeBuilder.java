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
package org.eclipse.riena.example.client.navigation.model;

import org.eclipse.riena.internal.example.client.Activator;
import org.eclipse.riena.navigation.INavigationAssembler;
import org.eclipse.riena.navigation.INavigationAssemblyExtension;
import org.osgi.framework.Bundle;

public abstract class NavigationNodeBuilder implements INavigationAssembler {

	private INavigationAssemblyExtension assembly;

	/**
	 * @see org.eclipse.riena.navigation.INavigationAssembler#getAssembly()
	 */
	public INavigationAssemblyExtension getAssembly() {
		return assembly;
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationAssembler#setAssembly(org.eclipse.riena.navigation.INavigationAssemblyExtension)
	 */
	public void setAssembly(INavigationAssemblyExtension nodeDefinition) {
		assembly = nodeDefinition;
	}

	protected String createIconPath(String subPath) {
		Bundle bundle = Activator.getDefault().getBundle();

		if (bundle == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder(bundle.getSymbolicName());
		builder.append(":"); //$NON-NLS-1$
		builder.append(subPath);
		return builder.toString();
	}

}
