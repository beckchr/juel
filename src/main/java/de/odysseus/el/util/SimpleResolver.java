/*
 * Copyright 2006, 2007 Odysseus Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package de.odysseus.el.util;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ResourceBundleELResolver;

/**
 * Simple resolver implementation.
 *
 * @author Christoph Beck
 */
public class SimpleResolver extends ELResolver {
	private static final ELResolver DEFAULT_RESOLVER_READ_ONLY = new CompositeELResolver() {
		{
			add(new ArrayELResolver(true));
			add(new ListELResolver(true));
			add(new MapELResolver(true));
			add(new ResourceBundleELResolver());
			add(new BeanELResolver(true));
		}
	};
	private static final ELResolver DEFAULT_RESOLVER_READ_WRITE = new CompositeELResolver() {
		{
			add(new ArrayELResolver(false));
			add(new ListELResolver(false));
			add(new MapELResolver(false));
			add(new ResourceBundleELResolver());
			add(new BeanELResolver(false));
		}
	};
	private final Map<Object,Object> map = Collections.synchronizedMap(new HashMap<Object,Object>());
	private final ELResolver delegate;
	private final boolean readOnly;

	/**
	 * Create a resolver capable of resolving top-level identifiers.
	 * Everything else is passed to the supplied delegate.
	 */
	public SimpleResolver(ELResolver delegate, boolean readOnly) {
		this.delegate = delegate;
		this.readOnly = readOnly;
	}

	/**
	 * Create a read/write resolver capable of resolving top-level identifiers.
	 * Everything else is passed to the supplied delegate.
	 */
	public SimpleResolver(ELResolver delegate) {
		this(delegate, false);
	}

	/**
	 * Create a resolver capable of resolving top-level identifiers,
	 * bean properties, array values, list values, map values and resource values.
	 */
	public SimpleResolver(boolean readOnly) {
		this(readOnly ? DEFAULT_RESOLVER_READ_ONLY : DEFAULT_RESOLVER_READ_WRITE, readOnly);
	}

	/**
	 * Create a read/write resolver capable of resolving top-level identifiers,
	 * bean properties, array values, list values, map values and resource values.
	 */
	public SimpleResolver() {
		this(DEFAULT_RESOLVER_READ_WRITE, false);
	}

	private boolean resolve(ELContext context, Object base) {
		context.setPropertyResolved(base == null);
		return context.isPropertyResolved();
	}

	private Object get(Object property) throws PropertyNotFoundException {
		if (map.containsKey(property)) {
			return map.get(property);
		}
		throw new PropertyNotFoundException("Cannot find property " + property);
	}
	
	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return resolve(context, base) ? Object.class : delegate.getCommonPropertyType(context, base);
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return resolve(context, base) ? null : delegate.getFeatureDescriptors(context, base);
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
		return resolve(context, base) ? Object.class : delegate.getType(context, base, property);
	}

	@Override
	public Object getValue(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
		return resolve(context, base) ? get(property) : delegate.getValue(context, base, property);
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) throws NullPointerException, PropertyNotFoundException, ELException {
		return resolve(context, base) ? readOnly : delegate.isReadOnly(context, base, property);
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
		if (resolve(context, base)) {
			if (readOnly) {
				throw new PropertyNotWritableException("resolver is read only!");
			}
			map.put(property, value);
		} else {
			delegate.setValue(context, base, property, value);
		}
	}		
}
