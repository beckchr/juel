package javax.el;

import junit.framework.TestCase;

public class ValueReferenceTest extends TestCase {

	public void testGetBase() {
		assertEquals("foo", new ValueReference("foo", "bar").getBase());
	}

	public void testGetProperty() {
		assertEquals("bar", new ValueReference("foo", "bar").getProperty());
	}

}
