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

import java.lang.reflect.Method;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

/**
 * Sample: enable function calls with varargs.
 */
public class VarArgs {
	public static void main(String... args) throws NoSuchMethodException {
		// create our factory
//		System.setProperty("javax.el.varArgs", "true");		
//		ExpressionFactory f = new ExpressionFactoryImpl(System.getProperties());
		ExpressionFactory f = new ExpressionFactoryImpl(); // varargs are enabled by default

		// create our context with function "vararg:format"
		Method method = String.class.getMethod("format", new Class[]{String.class, Object[].class});
		SimpleContext context = new SimpleContext();
		context.setFunction("varargs", "format", method);

		// our expression we want to evaluate
		String expression = "${varargs:format('Hey %s','Joe')}";

		// let's go...
		ValueExpression e = f.createValueExpression(context, expression, String.class);
		System.out.println(e.getValue(context)); // --> Hey Joe
	}
}
