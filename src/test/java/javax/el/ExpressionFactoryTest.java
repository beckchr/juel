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

import java.util.Properties;

import junit.framework.TestCase;

public class ExpressionFactoryTest extends TestCase {
	public static class TestExpressionFactory extends ExpressionFactory {
		final Properties properties;
		public TestExpressionFactory() {
			this.properties = null;
		}
		public TestExpressionFactory(Properties properties) {
			this.properties = properties;
		}
		@Override
		public Object coerceToType(Object obj, Class<?> targetType) {
			return null;
		}
		@Override
		public MethodExpression createMethodExpression(ELContext context, String expression,
				Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
			return null;
		};
		@Override
		public ValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
			return null;
		}
		@Override
		public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
			return null;
		}
	}

	public void testNewInstance() {
		ExpressionFactory factory = ExpressionFactory.newInstance();
		assertNotNull(factory);
//		assertSame(factory.getClass(), TestExpressionFactory.class);
//		assertNull(((TestExpressionFactory)factory).properties);
	}

	public void testNewInstanceProperties() {
		ExpressionFactory factory = ExpressionFactory.newInstance(null);
		assertNotNull(factory);
//		assertSame(factory.getClass(), TestExpressionFactory.class);
//		assertNull(((TestExpressionFactory)factory).properties);

		factory = ExpressionFactory.newInstance(System.getProperties());
		assertNotNull(factory);
//		assertSame(factory.getClass(), TestExpressionFactory.class);
//		assertNotNull(((TestExpressionFactory)factory).properties);
	}
}
