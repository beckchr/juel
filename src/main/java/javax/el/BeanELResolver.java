/*
 * Copyright 2006-2009 Odysseus Software GmbH
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
package javax.el;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines property resolution behavior on objects using the JavaBeans component architecture. This
 * resolver handles base objects of any type, as long as the base is not null. It accepts any object
 * as a property, and coerces it to a string. That string is then used to find a JavaBeans compliant
 * property on the base object. The value is accessed using JavaBeans getters and setters. This
 * resolver can be constructed in read-only mode, which means that isReadOnly will always return
 * true and {@link #setValue(ELContext, Object, Object, Object)} will always throw
 * PropertyNotWritableException. ELResolvers are combined together using {@link CompositeELResolver}
 * s, to define rich semantics for evaluating an expression. See the javadocs for {@link ELResolver}
 * for details. Because this resolver handles base objects of any type, it should be placed near the
 * end of a composite resolver. Otherwise, it will claim to have resolved a property before any
 * resolvers that come after it get a chance to test if they can do so as well.
 * 
 * @see CompositeELResolver
 * @see ELResolver
 */
public class BeanELResolver extends ELResolver {
	protected static final class BeanProperties {
		private final Map<String, BeanProperty> map = new HashMap<String, BeanProperty>();

		public BeanProperties(Class<?> baseClass) {
			PropertyDescriptor[] descriptors;
			try {
				descriptors = Introspector.getBeanInfo(baseClass).getPropertyDescriptors();
			} catch (IntrospectionException e) {
				throw new ELException(e);
			}
			for (PropertyDescriptor descriptor : descriptors) {
				map.put(descriptor.getName(), new BeanProperty(baseClass, descriptor));
			}
		}

		public BeanProperty getBeanProperty(String property) {
			return map.get(property);
		}
	}

	protected static final class BeanProperty {
		private final Class<?> baseClass;
		private final PropertyDescriptor descriptor;

		public BeanProperty(Class<?> baseClass, PropertyDescriptor descriptor) {
			this.baseClass = baseClass;
			this.descriptor = descriptor;
		}

		public Class<?> getPropertyType() {
			return descriptor.getPropertyType();
		}

		public Method getReadMethod() {
			return findAccessibleMethod(baseClass, descriptor.getReadMethod());
		}

		public Method getWriteMethod() {
			return findAccessibleMethod(baseClass, descriptor.getWriteMethod());
		}

		public boolean isReadOnly() {
			return findAccessibleMethod(baseClass, descriptor.getWriteMethod()) == null;
		}

		private Method findAccessibleMethod(Class<?> clazz, Method method) {
			if (method == null || Modifier.isPublic(clazz.getModifiers())) {
				return method;
			}
			for (Class<?> cls : clazz.getInterfaces()) {
				Method mth = null;
				try {
					mth = cls.getMethod(method.getName(), method.getParameterTypes());
					mth = findAccessibleMethod(mth.getDeclaringClass(), mth);
					if (mth != null) {
						return mth;
					}
				} catch (Exception e) {
				}
			}
			Class<?> cls = clazz.getSuperclass();
			if (cls != null) {
				Method mth = null;
				try {
					mth = cls.getMethod(method.getName(), method.getParameterTypes());
					mth = findAccessibleMethod(mth.getDeclaringClass(), mth);
					if (mth != null) {
						return mth;
					}
				} catch (Exception e) {
				}
			}
			return null;
		}
	}

	private final boolean readOnly;
	private final ConcurrentHashMap<Class<?>, BeanProperties> cache;

	/**
	 * Creates a new read/write BeanELResolver.
	 */
	public BeanELResolver() {
		this(false);
	}

	/**
	 * Creates a new BeanELResolver whose read-only status is determined by the given parameter.
	 */
	public BeanELResolver(boolean readOnly) {
		this.readOnly = readOnly;
		this.cache = new ConcurrentHashMap<Class<?>, BeanProperties>();
	}

	/**
	 * If the base object is not null, returns the most general type that this resolver accepts for
	 * the property argument. Otherwise, returns null. Assuming the base is not null, this method
	 * will always return Object.class. This is because any object is accepted as a key and is
	 * coerced into a string.
	 * 
	 * @param context
	 *            The context of this evaluation.
	 * @param base
	 *            The bean to analyze.
	 * @return null if base is null; otherwise Object.class.
	 */
	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return isResolvable(base) ? Object.class : null;
	}

	/**
	 * If the base object is not null, returns an Iterator containing the set of JavaBeans
	 * properties available on the given object. Otherwise, returns null. The Iterator returned must
	 * contain zero or more instances of java.beans.FeatureDescriptor. Each info object contains
	 * information about a property in the bean, as obtained by calling the
	 * BeanInfo.getPropertyDescriptors method. The FeatureDescriptor is initialized using the same
	 * fields as are present in the PropertyDescriptor, with the additional required named
	 * attributes "type" and "resolvableAtDesignTime" set as follows:
	 * <ul>
	 * <li>{@link ELResolver#TYPE} - The runtime type of the property, from
	 * PropertyDescriptor.getPropertyType().</li>
	 * <li>{@link ELResolver#RESOLVABLE_AT_DESIGN_TIME} - true.</li>
	 * </ul>
	 * 
	 * @param context
	 *            The context of this evaluation.
	 * @param base
	 *            The bean to analyze.
	 * @return An Iterator containing zero or more FeatureDescriptor objects, each representing a
	 *         property on this bean, or null if the base object is null.
	 */
	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		if (isResolvable(base)) {
			final PropertyDescriptor[] properties;
			try {
				properties = Introspector.getBeanInfo(base.getClass()).getPropertyDescriptors();
			} catch (IntrospectionException e) {
				return Collections.<FeatureDescriptor>emptyList().iterator();
			}
			return new Iterator<FeatureDescriptor>() {
				int next = 0;
				public boolean hasNext() {					
					return properties != null && next < properties.length;
				}
				public FeatureDescriptor next() {
					PropertyDescriptor property = properties[next++];
					FeatureDescriptor feature = new FeatureDescriptor();
					feature.setDisplayName(property.getDisplayName());
					feature.setName(property.getName());
					feature.setShortDescription(property.getShortDescription());
					feature.setExpert(property.isExpert());
					feature.setHidden(property.isHidden());
					feature.setPreferred(property.isPreferred());
					feature.setValue(TYPE, property.getPropertyType());
					feature.setValue(RESOLVABLE_AT_DESIGN_TIME, true);
					return feature;
				}
				public void remove() {
					throw new UnsupportedOperationException("cannot remove");
				}
			};
		}
		return null;
	}

	/**
	 * If the base object is not null, returns the most general acceptable type that can be set on
	 * this bean property. If the base is not null, the propertyResolved property of the ELContext
	 * object must be set to true by this resolver, before returning. If this property is not true
	 * after this method is called, the caller should ignore the return value. The provided property
	 * will first be coerced to a String. If there is a BeanInfoProperty for this property and there
	 * were no errors retrieving it, the propertyType of the propertyDescriptor is returned.
	 * Otherwise, a PropertyNotFoundException is thrown.
	 * 
	 * @param context
	 *            The context of this evaluation.
	 * @param base
	 *            The bean to analyze.
	 * @param property
	 *            The name of the property to analyze. Will be coerced to a String.
	 * @return If the propertyResolved property of ELContext was set to true, then the most general
	 *         acceptable type; otherwise undefined.
	 * @throws NullPointerException
	 *             if context is null
	 * @throws PropertyNotFoundException
	 *             if base is not null and the specified property does not exist or is not readable.
	 * @throws ELException
	 *             if an exception was thrown while performing the property or variable resolution.
	 *             The thrown exception must be included as the cause property of this exception, if
	 *             available.
	 */
	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException();
		}
		Class<?> result = null;
		if (isResolvable(base)) {
			result = toBeanProperty(base, property).getPropertyType();
			context.setPropertyResolved(true);
		}
		return result;
	}

	/**
	 * If the base object is not null, returns the current value of the given property on this bean.
	 * If the base is not null, the propertyResolved property of the ELContext object must be set to
	 * true by this resolver, before returning. If this property is not true after this method is
	 * called, the caller should ignore the return value. The provided property name will first be
	 * coerced to a String. If the property is a readable property of the base object, as per the
	 * JavaBeans specification, then return the result of the getter call. If the getter throws an
	 * exception, it is propagated to the caller. If the property is not found or is not readable, a
	 * PropertyNotFoundException is thrown.
	 * 
	 * @param context
	 *            The context of this evaluation.
	 * @param base
	 *            The bean to analyze.
	 * @param property
	 *            The name of the property to analyze. Will be coerced to a String.
	 * @return If the propertyResolved property of ELContext was set to true, then the value of the
	 *         given property. Otherwise, undefined.
	 * @throws NullPointerException
	 *             if context is null
	 * @throws PropertyNotFoundException
	 *             if base is not null and the specified property does not exist or is not readable.
	 * @throws ELException
	 *             if an exception was thrown while performing the property or variable resolution.
	 *             The thrown exception must be included as the cause property of this exception, if
	 *             available.
	 */
	@Override
	public Object getValue(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException();
		}
		Object result = null;
		if (isResolvable(base)) {
			Method method = toBeanProperty(base, property).getReadMethod();
			if (method == null) {
				throw new PropertyNotFoundException("Cannot read property " + property);
			}
			try {
				result = method.invoke(base);
			} catch (InvocationTargetException e) {
				throw new ELException(e.getCause());
			} catch (Exception e) {
				throw new ELException(e);
			}
			context.setPropertyResolved(true);
		}
		return result;
	}

	/**
	 * If the base object is not null, returns whether a call to
	 * {@link #setValue(ELContext, Object, Object, Object)} will always fail. If the base is not
	 * null, the propertyResolved property of the ELContext object must be set to true by this
	 * resolver, before returning. If this property is not true after this method is called, the
	 * caller can safely assume no value was set.
	 * 
	 * @param context
	 *            The context of this evaluation.
	 * @param base
	 *            The bean to analyze.
	 * @param property
	 *            The name of the property to analyze. Will be coerced to a String.
	 * @return If the propertyResolved property of ELContext was set to true, then true if calling
	 *         the setValue method will always fail or false if it is possible that such a call may
	 *         succeed; otherwise undefined.
	 * @throws NullPointerException
	 *             if context is null
	 * @throws PropertyNotFoundException
	 *             if base is not null and the specified property does not exist or is not readable.
	 * @throws ELException
	 *             if an exception was thrown while performing the property or variable resolution.
	 *             The thrown exception must be included as the cause property of this exception, if
	 *             available.
	 */
	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (context == null) {
			throw new NullPointerException();
		}
		boolean result = readOnly;
		if (isResolvable(base)) {
			result |= toBeanProperty(base, property).isReadOnly();
			context.setPropertyResolved(true);
		}
		return result;
	}

	/**
	 * If the base object is not null, attempts to set the value of the given property on this bean.
	 * If the base is not null, the propertyResolved property of the ELContext object must be set to
	 * true by this resolver, before returning. If this property is not true after this method is
	 * called, the caller can safely assume no value was set. If this resolver was constructed in
	 * read-only mode, this method will always throw PropertyNotWritableException. The provided
	 * property name will first be coerced to a String. If property is a writable property of base
	 * (as per the JavaBeans Specification), the setter method is called (passing value). If the
	 * property exists but does not have a setter, then a PropertyNotFoundException is thrown. If
	 * the property does not exist, a PropertyNotFoundException is thrown.
	 * 
	 * @param context
	 *            The context of this evaluation.
	 * @param base
	 *            The bean to analyze.
	 * @param property
	 *            The name of the property to analyze. Will be coerced to a String.
	 * @param value
	 *            The value to be associated with the specified key.
	 * @throws NullPointerException
	 *             if context is null
	 * @throws PropertyNotFoundException
	 *             if base is not null and the specified property does not exist or is not readable.
	 * @throws PropertyNotWritableException
	 *             if this resolver was constructed in read-only mode, or if there is no setter for
	 *             the property
	 * @throws ELException
	 *             if an exception was thrown while performing the property or variable resolution.
	 *             The thrown exception must be included as the cause property of this exception, if
	 *             available.
	 */
	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (context == null) {
			throw new NullPointerException();
		}
		if (isResolvable(base)) {
			if (readOnly) {
				throw new PropertyNotWritableException("resolver is read-only");
			}
			Method method = toBeanProperty(base, property).getWriteMethod();
			if (method == null) {
				throw new PropertyNotWritableException("Cannot write property: " + property);
			}
			try {
				method.invoke(base, value);
			} catch (InvocationTargetException e) {
				throw new ELException("Cannot write property: " + property, e.getCause());
			} catch (IllegalAccessException e) {
				throw new PropertyNotWritableException("Cannot write property: " + property, e);
			}
			context.setPropertyResolved(true);
		}
	}

	/**
	 * Test whether the given base should be resolved by this ELResolver.
	 * 
	 * @param base
	 *            The bean to analyze.
	 * @param property
	 *            The name of the property to analyze. Will be coerced to a String.
	 * @return base != null
	 */
	private final boolean isResolvable(Object base) {
		return base != null;
	}

	/**
	 * Lookup BeanProperty for the given (base, property) pair.
	 * 
	 * @param base
	 *            The bean to analyze.
	 * @param property
	 *            The name of the property to analyze. Will be coerced to a String.
	 * @return The BeanProperty representing (base, property).
	 * @throws PropertyNotFoundException
	 *             if no BeanProperty can be found.
	 */
	private final BeanProperty toBeanProperty(Object base, Object property) {
		BeanProperties beanProperties = cache.get(base.getClass());
		if (beanProperties == null) {
			cache.putIfAbsent(base.getClass(), beanProperties = new BeanProperties(base.getClass()));
		}
		BeanProperty beanProperty = property == null ? null : beanProperties.getBeanProperty(property.toString());
		if (beanProperty == null) {
			throw new PropertyNotFoundException("Could not find property " + property + " in " + base.getClass());
		}
		return beanProperty;
	}

	/**
	 * This method is not part of the API, though it can be used (reflectively) by clients of this
	 * class to remove entries from the cache when the beans are being unloaded.
	 * 
	 * Note: this method is present in the reference implementation, so we're adding it here to ease
	 * migration.
	 * 
	 * @param classloader
	 *            The classLoader used to load the beans.
	 */
	@SuppressWarnings("unused")
	private final void purgeBeanClasses(ClassLoader loader) {
		Iterator<Class<?>> classes = cache.keySet().iterator();
		while (classes.hasNext()) {
			if (loader == classes.next().getClassLoader()) {
				classes.remove();
			}
		}
	}
}
