package javax.el;

import java.util.Locale;

import junit.framework.TestCase;

import de.odysseus.el.util.SimpleContext;

public class ELContextTest extends TestCase {

	public void testContext() {
		ELContext context = new SimpleContext();
		assertNull(context.getContext(Integer.class));
		context.putContext(Integer.class, "foo");
		assertEquals("foo", context.getContext(Integer.class));
	}

	public void testLocale() {
		ELContext context = new SimpleContext();
		assertNull(context.getLocale());
		context.setLocale(Locale.ENGLISH);
		assertEquals(Locale.ENGLISH, context.getLocale());
	}

	public void testPropertyResolved() {
		ELContext context = new SimpleContext();
		assertFalse(context.isPropertyResolved());
		context.setPropertyResolved(true);
		assertTrue(context.isPropertyResolved());
	}
}
