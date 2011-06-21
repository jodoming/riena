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
package org.eclipse.riena.internal.ui.ridgets.annotation.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.IRidgetContainer;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.annotation.OnClick;
import org.eclipse.riena.ui.ridgets.annotation.handler.AbstractRidgetContainerAnnotationHandler;
import org.eclipse.riena.ui.ridgets.listener.IClickListener;

/**
 * Annotation handler for {@code @OnClick}
 * 
 * @since 4.0
 */
public class ClickAnnotationHandler extends AbstractRidgetContainerAnnotationHandler {

	public void handleAnnotation(final Annotation annotation, final IRidgetContainer ridgetContainer,
			final Object target, final Method targetMethod) {

		if (annotation instanceof OnClick) {
			final IRidget ridget = getRidget(annotation, targetMethod, ridgetContainer,
					((OnClick) annotation).ridgetId());
			final IClickListener clickListener = createListener(IClickListener.class, "callback", target, //$NON-NLS-1$
					targetMethod);
			if (ridget instanceof ITableRidget) {
				((ITableRidget) ridget).addClickListener(clickListener);
			} else {
				errorUnsupportedRidgetType(annotation, ridget);
			}
		}
	}
}