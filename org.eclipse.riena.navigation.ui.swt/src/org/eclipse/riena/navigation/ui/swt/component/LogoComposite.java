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
package org.eclipse.riena.navigation.ui.swt.component;

import org.osgi.service.log.LogService;

import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.log.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.internal.navigation.ui.swt.Activator;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.ShellLogoRenderer;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.utils.ImageStore;

/**
 * Composite to display a logo in the title of the Riena application.
 */
public class LogoComposite extends Composite {

	private static final Logger LOGGER = Log4r.getLogger(Activator.getDefault(), LogoComposite.class);

	/**
	 * Creates a new instance of {@code LogoComposite} and initializes it.
	 * 
	 * @param parent
	 *            a composite which will be the parent of the new instance
	 *            (cannot be null)
	 * @param style
	 *            the style of widget to construct
	 */
	public LogoComposite(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		init(parent);
	}

	/**
	 * Layouts the logo and adds a PaintListener for the logo.
	 * 
	 * @param parent
	 *            parent composite
	 */
	private void init(Composite parent) {

		Assert.isTrue(parent.getLayout() instanceof FormLayout);

		FormData logoData = new FormData();
		int topInset = 0;
		int leftRightInset = 0;
		logoData.top = new FormAttachment(0, topInset);
		int height = getSwitchterTopMargin() + getSwitchterHeight() - 1;
		logoData.bottom = new FormAttachment(0, height + 2);
		Image logoImage = getLogoImage();
		if (logoImage == null) {
			return;
		}
		Rectangle imageBounds = logoImage.getBounds();
		if (imageBounds == null) {
			return;
		}
		logoData.width = imageBounds.width + ShellLogoRenderer.getHorizontalLogoMargin() * 2;
		Integer hPos = getHorizontalLogoPosition();
		switch (hPos) {
		case SWT.CENTER:
			logoData.left = new FormAttachment(50, -logoData.width / 2);
			break;
		case SWT.RIGHT:
			logoData.right = new FormAttachment(100, -leftRightInset);
			break;
		default:
			logoData.left = new FormAttachment(0, leftRightInset);
			break;
		}
		setLayoutData(logoData);

		addPaintListener(new LogoPaintListener());

	}

	/**
	 * Returns the image of the logo.
	 * 
	 * @return logo image or the default missing image, if the logo image of the
	 *         L&F wasn't found.
	 */
	private Image getLogoImage() {
		Image logoImage = LnfManager.getLnf().getImage(LnfKeyConstants.TITLELESS_SHELL_LOGO);
		if (logoImage == null) {
			String message = "The image of the logo wasn't found! A dummy image is used."; //$NON-NLS-1$
			LOGGER.log(LogService.LOG_WARNING, message);
			logoImage = ImageStore.getInstance().getMissingImage();
		}
		return logoImage;
	}

	/**
	 * Returns the horizontal position of the logo inside the shell.
	 * 
	 * @return horizontal position (SWT.LEFT, SWT.CENTER, SWT.RIGHT)
	 */
	private int getHorizontalLogoPosition() {

		Integer hPos = LnfManager.getLnf().getIntegerSetting(LnfKeyConstants.TITLELESS_SHELL_HORIZONTAL_LOGO_POSITION);
		if (hPos == null) {
			hPos = SWT.LEFT;
		}
		return hPos;

	}

	/**
	 * Returns the margin between the top of the shell and the widget with the
	 * sub-application switchers.
	 * 
	 * @return margin
	 */
	private int getSwitchterTopMargin() {

		int margin = LnfManager.getLnf().getIntegerSetting(LnfKeyConstants.SUB_APPLICATION_SWITCHER_TOP_MARGIN);
		return margin;

	}

	/**
	 * Returns the of the sub-application switcher.
	 * 
	 * @return height
	 */
	private int getSwitchterHeight() {

		int margin = LnfManager.getLnf().getIntegerSetting(LnfKeyConstants.SUB_APPLICATION_SWITCHER_HEIGHT);
		return margin;

	}

	/**
	 * This listener paints the logo.
	 */
	private static class LogoPaintListener implements PaintListener {

		/**
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		public void paintControl(PaintEvent e) {
			onPaint(e);
		}

		/**
		 * Paints the image of the logo.
		 * 
		 * @param e
		 *            an event containing information about the paint
		 */
		private void onPaint(PaintEvent e) {

			Composite logoComposite = (Composite) e.getSource();
			Rectangle compositeBounds = logoComposite.getBounds();
			ShellLogoRenderer renderer = (ShellLogoRenderer) LnfManager.getLnf().getRenderer(
					LnfKeyConstants.TITLELESS_SHELL_LOGO_RENDERER);
			renderer.setBounds(compositeBounds);
			renderer.paint(e.gc, null);

		}

	}

}
