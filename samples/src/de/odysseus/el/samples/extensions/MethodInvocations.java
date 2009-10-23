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
package de.odysseus.el.samples.extensions;

import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

/**
 * Sample: enable method invocations.
 */
public class MethodInvocations {
	public static void main(String... args) throws NoSuchMethodException {
		// create our factory
//		System.setProperty("javax.el.methodInvocations", "true");		
//		ExpressionFactory f = new ExpressionFactoryImpl(System.getProperties());
		ExpressionFactory f = new ExpressionFactoryImpl(); // method invocations are enabled by default

		// create our resolver
		CompositeELResolver resolver = new CompositeELResolver();
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
