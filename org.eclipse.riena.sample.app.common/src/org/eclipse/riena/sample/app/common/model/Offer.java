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
package org.eclipse.riena.sample.app.common.model;

public class Offer {
	private Integer customerNumber;
	private Integer productId;

	public Offer(Integer customerNumber, Integer productId) {

		super();

		this.customerNumber = customerNumber;
		this.productId = productId;
	}

	public Integer getCustomerNumber() {
		return customerNumber;
	}

	public Integer getProductId() {
		return productId;
	}
}
