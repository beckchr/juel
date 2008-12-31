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
package de.odysseus.el.samples.extensions;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.misc.MethodInvocation;
import de.odysseus.el.util.SimpleContext;

/**
 * Sample: enable method invocations.
 */
public class MethodInvocations {
	/**
	 * Public method resolver.
	 * 
	 * For a method invocation of method <code>name</code> on object <code>base</code> with
	 * <code>n</code> arguments, this resolver will match a public method <code>m</code>
	 * declared in <code>base</code>' class or any of it's superclasses such that
	 * <ul>
	 * <li><code>m</code> has name <code>name</code></li>
	 * <li><code>m</code>'s return type is not <code>void</code></li>
	 * <li><code>m</code> takes <code>n</code> parameters or <code>m</code> is a vararg-method
	 * and varargs are enabled and <code>m</code> takes at least <code>n+1</code> parameters</li>.
	 * </ul>
	 * This implementation does no caching. A real-world resolver may want do this for better
	 * performance. Feel free to implement your own, optimized method resolver.
	 */
	public static class PublicMethodResolver extends ELResolver {
		private boolean match(MethodInvocation call, Method method) {
			if (method.getName().equals(call.getName()) && method.getReturnType() != void.class) {
				if (call.getParamCount() == method.getParameterTypes().length) {
					return true;
				}
				if (method.isVarArgs() && call.isVarArgs()) {
					if (call.getParamCount() >= method.getParameterTypes().length - 1) {
						return true;
					}
				}
			}
			return false;
		}
		
		@Override
		public Method getValue(ELContext context, Object base, Object prop) {
			if (base != null && prop instanceof MethodInvocation) {
				MethodInvocation call = (MethodInvocation)prop;
				for (Method method : base.getClass().getMethods()) {
					if (match(call, method)) {
						context.setPropertyResolved(true);
						return method;
					}
				}
			}
			return null;
		}

		@Override
		public void setValue(ELContext context, Object base, Object property, Object value) {
			throw new PropertyNotWritableException();
		}

		@Override
		public Class<?> getCommonPropertyType(ELContext context, Object base) {
			return MethodInvocation.class;
		}

		@Override
		public boolean isReadOnly(ELContext context, Object base, Object property) {
			return true;
		}

		@Override
		public Class<?> getType(ELContext context, Object base, Object property) {
			return null;
		}

		@Override
		public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
			return null;
		}
	}

	public static void main(String... args) throws NoSuchMethodException {
		// create our factory which uses our customized builder
		System.setProperty("javax.el.methodInvocations", "true");		
		ExpressionFactory f = new ExpressionFactoryImpl(System.getProperties());

		// create our resolver
		CompositeELResolver resolver = new CompositeELResolver();
		resolver.add(new PublicMethodResolver());
		resolver.add(new BeanELResolver());

		// create our context
		ELContext context = new SimpleContext(resolver);

		// let's go...
		ValueExpression e = null;
		
		e = f.createValueExpression(context, "${'foo'.matches('foo|bar')}", boolean.class);
		System.out.println(e.getValue(context)); // --> true
		
		e = f.createValueExpression(context, "${'bar'.toUpperCase()}", String.class);
		System.out.println(e.getValue(context)); // --> BAR
		
		e = f.createValueExpression(context, "${'foobar '.trim().length()}", int.class);
		System.out.println(e.getValue(context)); // --> 6
	}
}
