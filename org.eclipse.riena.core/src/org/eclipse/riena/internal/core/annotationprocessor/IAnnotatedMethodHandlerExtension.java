/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.core.annotationprocessor;

import java.lang.annotation.Annotation;

import org.eclipse.riena.core.annotationprocessor.IAnnotatedMethodHandler;
import org.eclipse.riena.core.injector.extension.ExtensionInterface;
import org.eclipse.riena.core.injector.extension.MapName;

/**
 *
 */
@ExtensionInterface(id = "annotatedMethodHandlers")
public interface IAnnotatedMethodHandlerExtension {

	/**
	 * @return
	 */
	Class<? extends Annotation> getAnnotation();

	/**
	 * @return
	 */
	@MapName("class")
	IAnnotatedMethodHandler createHandler();

}