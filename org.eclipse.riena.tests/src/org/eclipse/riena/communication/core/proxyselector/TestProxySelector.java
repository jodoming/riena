/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.communication.core.proxyselector;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestProxySelector extends ProxySelector {

	private Proxy proxy;
	private URI uri;

	public TestProxySelector(Proxy proxy) {
		this.proxy = proxy;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		this.uri = uri;
	}

	@Override
	public List<Proxy> select(URI uri) {
		List<Proxy> result = new ArrayList<Proxy>();
		result.add(proxy);
		return result;
	}

	URI getFailedURI() {
		return uri;
	}
}
