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

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider that shows checked / unchecked icons, if the attribute in a
 * column is of Boolean type.
 * 
 * @see TableRidget
 */
public class TableRidgetLabelProvider extends ObservableMapLabelProvider {

	private final IObservableMap[] attributeMap;

	/**
	 * Create a new instance
	 * 
	 * @param viewer
	 *            a non-null {@link TreeViewer} instance
	 * @param attributeMap
	 *            a non-null {@link IObservableMap} instance
	 */
	public TableRidgetLabelProvider(IObservableMap[] attributeMap) {
		super(attributeMap);
		this.attributeMap = new IObservableMap[attributeMap.length];
		System.arraycopy(attributeMap, 0, this.attributeMap, 0, this.attributeMap.length);
	}

	@Override
	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex < attributeMap.length) {
			Object result = attributeMap[columnIndex].get(element);
			if (result instanceof Boolean) {
				String key = ((Boolean) result).booleanValue() ? SharedImages.IMG_CHECKED : SharedImages.IMG_UNCHECKED;
				return Activator.getSharedImage(key);
			}
		}
		return super.getColumnImage(element, columnIndex);
	}

	/**
	 * Returns the value of the given element at the specified column index.
	 * 
	 * @param element
	 * @param columnIndex
	 *            - column index
	 * @return value or {@code null} if column index is not correct
	 */
	public Object getColumnValue(Object element, int columnIndex) {
		if (columnIndex < attributeMap.length) {
			return attributeMap[columnIndex].get(element);
		}
		return null;
	}

}
