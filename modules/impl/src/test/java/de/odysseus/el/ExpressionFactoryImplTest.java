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
package de.odysseus.el;

import de.odysseus.el.util.SimpleContext;
import de.odysseus.el.util.SimpleResolver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionFactoryImplTest {

	public static long bar() {
		return 1;
	}
	
	private ExpressionFactoryImpl factory = new ExpressionFactoryImpl();

	@Test
	public void testCoerceToType() {
		assertEquals("1", factory.coerceToType(1l, String.class));
	}

	@Test
	public void testCreateTreeValueExpression() {
		SimpleContext context = new SimpleContext(new SimpleResolver());
		assertEquals(1l, factory.createValueExpression(context, "${1}", Object.class).getValue(context));
	}

	@Test
	public void testCreateObjectValueExpression() {
		SimpleContext context = new SimpleContext(new SimpleResolver());
		assertEquals("1", factory.createValueExpression("1", Object.class).getValue(context));
	}

	@Test
	public void testCreateMethodExpression() throws NoSuchMethodException {
		SimpleContext context = new SimpleContext(new SimpleResolver());
		context.getELResolver().setValue(context, null, "foo", this);
		assertEquals(bar(), factory.createMethodExpression(context, "${foo.bar}", null, new Class[0]).invoke(context, null));
	}
}
