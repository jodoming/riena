/*******************************************************************************
 * Copyright (c) 2007, 2012 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.swt.utils;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.riena.core.cache.LRUHashMap;
import org.eclipse.riena.ui.swt.facades.GCFacade;

/**
 * A collection of utility methods for SWT.
 */
public final class SwtUtilities {

	private static final String THREE_DOTS = "..."; //$NON-NLS-1$
	private static final Map<GCString, Integer> TEXT_WIDTH_CACHE = LRUHashMap.createLRUHashMap(2048);
	private static final Map<GCChar, Integer> CHAR_WIDTH_CACHE = LRUHashMap.createLRUHashMap(1024);

	/**
	 * This class contains only static methods. So it is not necessary to create an instance.
	 */
	private SwtUtilities() {
		throw new Error("SwtUtilities is just a container for static methods"); //$NON-NLS-1$
	}

	/**
	 * The text will be clipped, if the width of the given text is greater than the maximum width.<br>
	 * The clipped text always ends with three dots ("...").
	 * 
	 * @param gc
	 *            graphics context
	 * @param text
	 *            text
	 * @param maxWidth
	 *            maximum of the text
	 * @return truncated text
	 */
	public static String clipText(final GC gc, final String text, final int maxWidth) {
		int textwidth = calcTextWidth(gc, text);
		if (textwidth <= maxWidth) {
			return text;
		}
		final int threeDotsWidth = calcTextWidth(gc, THREE_DOTS);
		final StringBuilder shortenedText = new StringBuilder(text);
		while (textwidth + threeDotsWidth > maxWidth && shortenedText.length() != 0) {
			shortenedText.setLength(shortenedText.length() - 1);
			textwidth = calcTextWidth(gc, shortenedText);
		}
		shortenedText.append(THREE_DOTS);
		return shortenedText.toString();
	}

	/**
	 * Calculates the width of the given text based on the current settings of the given graphics context.
	 * 
	 * @param gc
	 *            graphics context
	 * @param text
	 *            text
	 * @return width of text
	 */
	public static int calcTextWidth(final GC gc, final CharSequence text) {
		if (text == null) {
			return 0;
		}
		final GCString lookupKey = new GCString(gc, text);
		Integer width = TEXT_WIDTH_CACHE.get(lookupKey);
		if (width == null) {
			int w = 0;
			for (int i = 0; i < text.length(); i++) {
				w += calcCharWidth(gc, text.charAt(i));
			}
			width = w;
			TEXT_WIDTH_CACHE.put(lookupKey, width);
		}
		return width;
	}

	/**
	 * Calculates the width of the given char based on the current settings of the given graphics context.
	 * 
	 * @param gc
	 *            graphics context
	 * @param ch
	 *            character
	 * @return width of character
	 */
	public static int calcCharWidth(final GC gc, final char ch) {
		final GCChar lookupKey = new GCChar(gc, ch);
		Integer width = CHAR_WIDTH_CACHE.get(lookupKey);
		if (width == null) {
			width = GCFacade.getDefault().getAdvanceWidth(gc, ch);
			CHAR_WIDTH_CACHE.put(lookupKey, width);
		}
		return width;
	}

	/**
	 * Disposes the given resource, if the resource is not null and isn't already disposed.
	 * 
	 * @param resource
	 *            resource to dispose
	 * 
	 * @since 3.0
	 */
	public static void dispose(final Resource resource) {
		if (!isDisposed(resource)) {
			resource.dispose();
		}
	}

	/**
	 * Disposes the given widget, if the widget is not {@code null} and isn't already disposed.
	 * 
	 * @param widget
	 *            widget to dispose
	 * 
	 * @since 3.0
	 */
	public static void dispose(final Widget widget) {
		if (!isDisposed(widget)) {
			widget.dispose();
		}
	}

	/**
	 * Returns the 0 based index of the column at {@code pt}. The code can handle re-ordered columns. The index refers to the original ordering (as used by SWT
	 * API).
	 * <p>
	 * Will return -1 if no column could be computed -- this is the case when all columns are resized to have width 0.
	 * 
	 * @since 5.0
	 */
	public static int findColumn(final Tree tree, final Point pt) {
		int width = 0;
		final int[] colOrder = tree.getColumnOrder();
		// compute the current column ordering
		final TreeColumn[] columns = new TreeColumn[colOrder.length];
		for (int i = 0; i < colOrder.length; i++) {
			final int idx = colOrder[i];
			columns[i] = tree.getColumn(idx);
		}
		// find the column under Point pt
		for (final TreeColumn col : columns) {
			final int colWidth = col.getWidth();
			if (width < pt.x && pt.x < width + colWidth) {
				return tree.indexOf(col);
			}
			width += colWidth;
		}
		return -1;
	}

	/**
	 * Returns the 0 based index of the column at {@code pt}. The code can handle re-ordered columns. The index refers to the original ordering (as used by SWT
	 * API).
	 * <p>
	 * Will return -1 if no column could be computed -- this is the case when all columns are resized to have width 0.
	 * 
	 * @since 3.0
	 */
	public static int findColumn(final Table table, final Point pt) {
		int width = 0;
		final int[] colOrder = table.getColumnOrder();
		// compute the current column ordering
		final TableColumn[] columns = new TableColumn[colOrder.length];
		for (int i = 0; i < colOrder.length; i++) {
			final int idx = colOrder[i];
			columns[i] = table.getColumn(idx);
		}
		// find the column under Point pt
		for (final TableColumn col : columns) {
			final int colWidth = col.getWidth();
			if (width < pt.x && pt.x < width + colWidth) {
				return table.indexOf(col);
			}
			width += colWidth;
		}
		return -1;
	}

	/**
	 * Returns true if the given {@code styleBit} is turned on, on the given {@code widget} instance. Returns false otherwise.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * SwtUtilities.hasStyle(button, SWT.CHECK);
	 * </pre>
	 * 
	 * @param widget
	 *            a Widget instance; may be null.
	 * @param styleBit
	 *            an SWT style bit value
	 * @since 3.0
	 */
	public static boolean hasStyle(final Widget widget, final int styleBit) {
		return widget == null ? false : (widget.getStyle() & styleBit) == styleBit;
	}

	/**
	 * Returns {@code true}, if the given widget is disposed or {@code null}.
	 * 
	 * @param widget
	 *            widget to check
	 * @return {@code true}, if the widget is disposed or {@code null}; otherwise {@code false}.
	 */
	public static boolean isDisposed(final Widget widget) {
		return widget == null || widget.isDisposed();
	}

	/**
	 * Returns {@code true}, if the given resource is disposed or {@code null}.
	 * 
	 * @param resource
	 *            resource to check
	 * @return {@code true}, if the resource is disposed or {@code null}; otherwise {@code false}.
	 */
	public static boolean isDisposed(final Resource resource) {
		return !((resource != null) && (!resource.isDisposed()));
	}

	/**
	 * Creates a new instance of <code>Color</code> that is a brighter version of the given color.
	 * 
	 * @param color
	 *            the color to make brighter; never null
	 * @param f
	 *            the factor.
	 * @return a new <code>Color</code> object that is a brighter version of this given color.
	 */
	public static Color makeBrighter(final Color color, final float f) {

		Assert.isNotNull(color);
		Assert.isTrue(f >= 0.0);

		final float[] hsb = color.getRGB().getHSB();
		final float h = hsb[0];
		final float s = hsb[1];
		float b = hsb[2];

		b = b * f;
		if (b > 1.0f) {
			b = 1.0f;
		}

		final RGB rgb = new RGB(h, s, b);

		return new Color(color.getDevice(), rgb);
	}

	private final static class GCChar {
		private final char ch;
		private final Font font;

		private GCChar(final GC gc, final char ch) {
			this.font = gc.getFont();
			this.ch = ch;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ch;
			result = prime * result + ((font == null) ? 0 : font.hashCode());
			return result;
		}

		// Note: There is no type check here because we do not mix types in the map!! (performance) 
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			final GCChar other = (GCChar) obj;
			if (ch != other.ch) {
				return false;
			}
			if (font == null) {
				if (other.font != null) {
					return false;
				}
			} else if (!font.equals(other.font)) {
				return false;
			}
			return true;
		}

	}

	private final static class GCString {
		private final String text;
		private final Font font;

		private GCString(final GC gc, final CharSequence seq) {
			this.font = gc.getFont();
			this.text = seq.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((font == null) ? 0 : font.hashCode());
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			return result;
		}

		// Note: There is no type check here because we do not mix types in the map!! (performance) 
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			final GCString other = (GCString) obj;
			if (font == null) {
				if (other.font != null) {
					return false;
				}
			} else if (!font.equals(other.font)) {
				return false;
			}
			if (text == null) {
				if (other.text != null) {
					return false;
				}
			} else if (!text.equals(other.text)) {
				return false;
			}
			return true;
		}

	}

}
