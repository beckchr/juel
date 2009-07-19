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

import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

/**
 * Sample: enable resolving <code>null</code> properties.
 */
public class NullProperties {
	public static void main(String... args) throws NoSuchMethodException {
		// create our factory which uses our customized builder
		System.setProperty("javax.el.nullProperties", "true");		
		ExpressionFactory f = new ExpressionFactoryImpl(System.getProperties());

		// create our context
		ELContext context = new SimpleContext();

		// create our expression we want to evaluate
		ValueExpression e = f.createValueExpression(context, "${map[null]}", String.class);

		// create a map containing a value for key <code>null</code> and make it available
		Map<String, String> map = new HashMap<String, String>();
		map.put(null, "foo");
		context.getELResolver().setValue(context, null, "map", map);

		// let's go...
		System.out.println(e.getValue(context)); // --> "foo"
	}
}
