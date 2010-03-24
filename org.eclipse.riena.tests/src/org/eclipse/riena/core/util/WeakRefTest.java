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
package org.eclipse.riena.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.riena.internal.core.test.RienaTestCase;
import org.eclipse.riena.internal.core.test.collect.NonUITestCase;

/**
 * Test the {@code WeakRef}.
 */
@NonUITestCase
public class WeakRefTest extends RienaTestCase {

	private static boolean gotNotified;

	public void testRemoveOfGarbageCollectedInstance() throws IOException {
		gotNotified = false;

		WeakRef<StringBuffer> ref = new WeakRef<StringBuffer>(new StringBuffer("go away"), new Runnable() {
			public void run() {
				gotNotified = true;
			}
		});

		runOutOfMemory();

		assertNull(ref.get());
		assertTrue(gotNotified);
	}

	private void runOutOfMemory() throws IOException {
		try {
			OutputStream os = new ByteArrayOutputStream();
			while (true) {
				os.write(new byte[1024 * 1024]);
			}
		} catch (OutOfMemoryError e) {
			System.gc();
		}
	}

}