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
package org.eclipse.riena.core.wire;

import java.lang.reflect.Method;

import org.eclipse.riena.core.injector.service.FilterInjector;
import org.eclipse.riena.core.injector.service.RankingInjector;
import org.eclipse.riena.core.injector.service.ServiceInjector;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.core.util.WeakRef;
import org.eclipse.riena.internal.core.test.RienaTestCase;
import org.eclipse.riena.internal.core.test.collect.NonUITestCase;
import org.eclipse.riena.internal.core.wire.ServiceInjectorBuilder;

/**
 * Test the {@code ServiceInjectorBuilder}.
 */
@NonUITestCase
public class ServiceInjectorBuilderTest extends RienaTestCase {

	public void testBuildForBind1() throws NoSuchMethodException {
		Method bindMethod = ServiceInjectorBuilderTest.class.getMethod("bind1", new Class[] { Schtonk.class });
		ServiceInjectorBuilder builder = new ServiceInjectorBuilder(this, bindMethod);
		ServiceInjector injector = builder.build().andStart(getContext());
		assertNotNull(injector);
		assertTrue(injector instanceof FilterInjector);
		assertFalse(useRanking(injector));
		assertNull(getFilter(injector));
		assertEquals(Schtonk.class, getServiceClass(injector));
		assertSame(this, getBean(injector));
		assertEquals(bindMethod, getBindMethod(injector));
		assertEquals("unbind1", getUnbindMethod(injector).getName());
		assertFalse(getOnceOnly(injector));
	}

	@InjectService(service = Schtonk.class)
	public void bind1(Schtonk schtonk) {
	}

	public void unbind1(Schtonk schtonk) {
	}

	public void testBuildForBind2() throws NoSuchMethodException {
		Method bindMethod = ServiceInjectorBuilderTest.class.getMethod("bind2", new Class[] { Schtonk.class });
		ServiceInjectorBuilder builder = new ServiceInjectorBuilder(this, bindMethod);
		ServiceInjector injector = builder.build().andStart(getContext());
		assertNotNull(injector);
		assertTrue(injector instanceof RankingInjector);
		assertTrue(useRanking(injector));
		assertNull(getFilter(injector));
		assertEquals(Schtonk.class.getName(), getServiceClassName(injector));
		assertSame(this, getBean(injector));
		assertEquals("bind2", getBindMethod(injector).getName());
		assertEquals("entbinde", getUnbindMethod(injector).getName());
		assertFalse(getOnceOnly(injector));
	}

	@InjectService(serviceName = "org.eclipse.riena.core.wire.Schtonk", useRanking = true, unbind = "entbinde")
	public void bind2(Schtonk schtonk) {
	}

	public void entbinde(Schtonk schtonk) {
	}

	public void testBuildForBind3() throws NoSuchMethodException {
		Method bindMethod = ServiceInjectorBuilderTest.class.getMethod("bind3", new Class[] { Schtonk.class });
		ServiceInjectorBuilder builder = new ServiceInjectorBuilder(this, bindMethod);
		ServiceInjector injector = builder.build().andStart(getContext());
		assertNotNull(injector);
		assertTrue(injector instanceof FilterInjector);
		assertFalse(useRanking(injector));
		assertEquals("(mellita = gut)", getFilter(injector));
		assertEquals(Schtonk.class.getName(), getServiceClassName(injector));
		assertSame(this, getBean(injector));
		assertEquals("bind3", getBindMethod(injector).getName());
		assertEquals("entbinde3", getUnbindMethod(injector).getName());
		assertFalse(getOnceOnly(injector));
	}

	@InjectService(serviceName = "org.eclipse.riena.core.wire.Schtonk", useFilter = "(mellita = gut)", unbind = "entbinde3")
	public void bind3(Schtonk schtonk) {
	}

	public void entbinde3(Schtonk schtonk) {
	}

	public void testBuildForBind4() throws NoSuchMethodException {
		Method bindMethod = ServiceInjectorBuilderTest.class.getMethod("bind4", new Class[] { Schtonk.class });
		ServiceInjectorBuilder builder = new ServiceInjectorBuilder(this, bindMethod);
		ServiceInjector injector = builder.build().andStart(getContext());
		assertNotNull(injector);
		assertTrue(injector instanceof FilterInjector);
		assertFalse(useRanking(injector));
		assertNull(getFilter(injector));
		assertEquals(Schtonk.class, getServiceClass(injector));
		assertSame(this, getBean(injector));
		assertEquals("bind4", getBindMethod(injector).getName());
		assertEquals("unbind4", getUnbindMethod(injector).getName());
		assertFalse(getOnceOnly(injector));
	}

	@InjectService
	public void bind4(Schtonk schtonk) {
	}

	public void unbind4(Schtonk schtonk) {
	}

	public void testBuildForBind5OnceOnlyViaStatic() throws NoSuchMethodException {
		Method bindMethod = ServiceInjectorBuilderTest.class.getMethod("bind5", new Class[] { Schtonk.class });
		ServiceInjectorBuilder builder = new ServiceInjectorBuilder(this, bindMethod);
		ServiceInjector injector = builder.build().andStart(getContext());
		assertNotNull(injector);
		assertTrue(injector instanceof FilterInjector);
		assertFalse(useRanking(injector));
		assertNull(getFilter(injector));
		assertEquals(Schtonk.class, getServiceClass(injector));
		assertSame(this, getBean(injector));
		assertEquals("bind5", getBindMethod(injector).getName());
		assertEquals("unbind5", getUnbindMethod(injector).getName());
		assertTrue(getOnceOnly(injector));
	}

	@InjectService
	public static void bind5(Schtonk schtonk) {
	}

	public static void unbind5(Schtonk schtonk) {
	}

	public void testBuildForBind6OnceOnlyViaAnnotation() throws NoSuchMethodException {
		Method bindMethod = ServiceInjectorBuilderTest.class.getMethod("bind6", new Class[] { Schtonk.class });
		ServiceInjectorBuilder builder = new ServiceInjectorBuilder(this, bindMethod);
		ServiceInjector injector = builder.build().andStart(getContext());
		assertNotNull(injector);
		assertTrue(injector instanceof FilterInjector);
		assertFalse(useRanking(injector));
		assertNull(getFilter(injector));
		assertEquals(Schtonk.class, getServiceClass(injector));
		assertSame(this, getBean(injector));
		assertEquals("bind6", getBindMethod(injector).getName());
		assertEquals("unbind6", getUnbindMethod(injector).getName());
		assertTrue(getOnceOnly(injector));
	}

	@InjectService(onceOnly = true)
	public void bind6(Schtonk schtonk) {
	}

	public void unbind6(Schtonk schtonk) {
	}

	private boolean useRanking(ServiceInjector injector) {
		Object serviceDescriptor = ReflectionUtils.getHidden(injector, "serviceDesc");
		return ReflectionUtils.getHidden(serviceDescriptor, "ranking");
	}

	private String getFilter(ServiceInjector injector) {
		Object serviceDescriptor = ReflectionUtils.getHidden(injector, "serviceDesc");
		return ReflectionUtils.getHidden(serviceDescriptor, "filter");
	}

	private Object getServiceClassName(ServiceInjector injector) {
		Object serviceDescriptor = ReflectionUtils.getHidden(injector, "serviceDesc");
		return ReflectionUtils.getHidden(serviceDescriptor, "className");
	}

	private Object getServiceClass(ServiceInjector injector) {
		Object serviceDescriptor = ReflectionUtils.getHidden(injector, "serviceDesc");
		return ReflectionUtils.getHidden(serviceDescriptor, "clazz");
	}

	private Object getBean(ServiceInjector injector) {
		WeakRef<?> ref = ReflectionUtils.getHidden(injector, "targetRef");
		return ref.get();
	}

	private Method getBindMethod(ServiceInjector injector) {
		return ReflectionUtils.getHidden(injector, "bindMethod");
	}

	private Method getUnbindMethod(ServiceInjector injector) {
		return ReflectionUtils.getHidden(injector, "unbindMethod");
	}

	private boolean getOnceOnly(ServiceInjector injector) {
		return ReflectionUtils.getHidden(injector, "onceOnly");
	}
}
