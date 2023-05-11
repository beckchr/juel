package javax.el;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueReferenceTest {

	@Test
	public void testGetBase() {
		assertEquals("foo", new ValueReference("foo", "bar").getBase());
	}

	@Test
	public void testGetProperty() {
		assertEquals("bar", new ValueReference("foo", "bar").getProperty());
	}

}
