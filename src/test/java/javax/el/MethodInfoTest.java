package javax.el;

import junit.framework.TestCase;

public class MethodInfoTest extends TestCase {

	public void testGetName() {
		assertEquals("foo", new MethodInfo("foo", Integer.class, new Class<?>[]{String.class}).getName());
	}

	public void testGetParamTypes() {
		assertEquals(String.class, new MethodInfo("foo", Integer.class, new Class<?>[]{String.class}).getParamTypes()[0]);
	}

	public void testGetReturnType() {
		assertEquals(Integer.class, new MethodInfo("foo", Integer.class, new Class<?>[]{String.class}).getReturnType());
	}
}
