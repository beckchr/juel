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
