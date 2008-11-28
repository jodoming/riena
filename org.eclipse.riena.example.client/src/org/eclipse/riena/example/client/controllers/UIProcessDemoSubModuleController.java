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
package org.eclipse.riena.example.client.controllers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.riena.example.client.communication.IInfoService;
import org.eclipse.riena.example.client.communication.InfoServiceFake;
import org.eclipse.riena.example.client.communication.RemoteCallProcess;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.core.uiprocess.UIProcess;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;

public class UIProcessDemoSubModuleController extends SubModuleController {

	private boolean registered;

	public UIProcessDemoSubModuleController() {
		this(null);
	}

	public UIProcessDemoSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
	}

	public IActionRidget getActionRidget() {
		return (IActionRidget) getRidget("actionRidget"); //$NON-NLS-1$
	}

	public IActionRidget getActionRidgetJob() {
		return (IActionRidget) getRidget("actionRidgetJob"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.riena.ui.ridgets.IRidgetContainer#configureRidgets()
	 */
	@Override
	public void configureRidgets() {
		if (getActionRidget() != null && !registered) {
			initUIProcessAction();
			initJobAction();
			registered = true;
		}
	}

	private void initJobAction() {
		getActionRidgetJob().setText("start job"); //$NON-NLS-1$
		getActionRidgetJob().addListener(new JobProcessAction());

	}

	private void initUIProcessAction() {
		getActionRidget().setText("start UIProcess"); //$NON-NLS-1$
		getActionRidget().addListener(new UIProcessAction());
	}

	void runUIProcess() {

		final IInfoService service = new InfoServiceFake();

		RemoteCallProcess<IInfoService> process = new RemoteCallProcess<IInfoService>("remote", true,
				getNavigationNode()) {

			@Override
			public boolean runJob(IProgressMonitor monitor) {
				getService().getInfo("foo");
				return true;
			}

			@Override
			protected IInfoService getService() {
				return service;
			}

		};

		process.start();

	}

	void runJob() {
		Job job = new Job("eclipse job") { //$NON-NLS-1$
			@Override
			public IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("eclipse job", 10); //$NON-NLS-1$
					for (int i = 0; i <= 10; i++) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						monitor.worked(i);
						if (monitor.isCanceled())
							return Status.CANCEL_STATUS;
					}
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setProperty(UIProcess.PROPERTY_CONTEXT, getNavigationNode());
		job.setUser(true);// to be visualized the job has to be user
		job.schedule();
	}

	private class UIProcessAction implements IActionListener {

		public void callback() {
			runUIProcess();
		}

	}

	private class JobProcessAction implements IActionListener {

		public void callback() {
			runJob();
		}

	}

}
