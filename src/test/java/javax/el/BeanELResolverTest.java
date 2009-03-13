package javax.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import de.odysseus.el.util.SimpleContext;

public class BeanELResolverTest extends TestCase {
	public static class TestBean {
		int readOnly = 123;
		int readWrite = 456;
		int writeOnly = 789;
		public int getReadOnly() {
			return readOnly;
		}
		void setReadOnly(int readOnly) {
			this.readOnly = readOnly;
		}
		public int getReadWrite() {
			return readWrite;
		}
		public void setReadWrite(int readWrite) {
			this.readWrite = readWrite;
		}
		int getWriteOnly() {
			return writeOnly;
		}
		public void setWriteOnly(int writeOnly) {
			this.writeOnly = writeOnly;
		}
	}

	ELContext context = new SimpleContext();

	public void testGetCommonPropertyType() {
		BeanELResolver resolver = new BeanELResolver();

		// base is bean --> Object.class
		assertSame(Object.class, resolver.getCommonPropertyType(context, new TestBean()));

		// base == null --> null
		assertNull(resolver.getCommonPropertyType(context, null));
	}

	public void testGetFeatureDescriptors() {
		BeanELResolver resolver = new BeanELResolver();

		// base == null --> null
		assertNull(resolver.getCommonPropertyType(context, null));

		// base is bean --> features...
		Iterator<FeatureDescriptor> iterator = resolver.getFeatureDescriptors(context, new TestBean());
		List<String> names = new ArrayList<String>();
		while (iterator.hasNext()) {
			FeatureDescriptor feature = iterator.next();
			names.add(feature.getName());
			Class<?> type = "class".equals(feature.getName()) ? Class.class : int.class;
			assertSame(type, feature.getValue(ELResolver.TYPE));
			assertSame(Boolean.TRUE, feature.getValue(ELResolver.RESOLVABLE_AT_DESIGN_TIME));
		}
		assertTrue(names.contains("class"));
		assertTrue(names.contains("readOnly"));
		assertTrue(names.contains("readWrite"));
		assertTrue(names.contains("writeOnly"));
		assertEquals(4, names.size());		
	}

	public void testGetType() {
		BeanELResolver resolver = new BeanELResolver();

		// base == null --> null
		context.setPropertyResolved(false);
		assertNull(resolver.getType(context, null, "foo"));
		assertFalse(context.isPropertyResolved());

		// base is bean, property == "readWrite" --> int.class
		context.setPropertyResolved(false);
		assertSame(int.class, resolver.getType(context, new TestBean(), "readWrite"));
		assertTrue(context.isPropertyResolved());

		// base is bean, property == null --> exception
		try {
			resolver.getType(context, new TestBean(), null);
			fail();
		} catch (PropertyNotFoundException e) {
			// fine
		}

		// base is bean, property != null, but doesn't exist --> exception
		try {
			resolver.getType(context, new TestBean(), "doesntExist");
			fail();
		} catch (PropertyNotFoundException e) {
			// fine
		}
	}

	public void testGetValue() {
		BeanELResolver resolver = new BeanELResolver();

		// base == null --> null
		context.setPropertyResolved(false);
		assertNull(resolver.getValue(context, null, "foo"));
		assertFalse(context.isPropertyResolved());

		// base is bean, property == "readWrite" --> 123
		context.setPropertyResolved(false);
		assertEquals(456, resolver.getValue(context, new TestBean(), "readWrite"));
		assertTrue(context.isPropertyResolved());

		// base is bean, property == "writeOnly" --> exception
		try {
			resolver.getValue(context, new TestBean(), "writeOnly");
			fail();
		} catch (PropertyNotFoundException e) {
			// fine
		}

		// base is bean, property != null, but doesn't exist --> exception
		try {
			resolver.getValue(context, new TestBean(), "doesntExist");
			fail();
		} catch (PropertyNotFoundException e) {
			// fine
		}
	}

	public void testIsReadOnly() {
		BeanELResolver resolver = new BeanELResolver();
		BeanELResolver resolverReadOnly = new BeanELResolver(true);

		// base == null --> false
		context.setPropertyResolved(false);
		assertFalse(resolver.isReadOnly(context, null, "foo"));
		assertFalse(context.isPropertyResolved());

		// base is bean, property == "readOnly" --> true
		context.setPropertyResolved(false);
		assertTrue(resolver.isReadOnly(context, new TestBean(), "readOnly"));
		assertTrue(context.isPropertyResolved());

		// base is bean, property == "readWrite" --> false
		context.setPropertyResolved(false);
		assertFalse(resolver.isReadOnly(context, new TestBean(), "readWrite"));
		assertTrue(context.isPropertyResolved());

		// base is bean, property == "writeOnly" --> false
		context.setPropertyResolved(false);
		assertFalse(resolver.isReadOnly(context, new TestBean(), "writeOnly"));
		assertTrue(context.isPropertyResolved());

		// base is bean, property == 1 --> true (use read-only resolver)
		context.setPropertyResolved(false);
		assertTrue(resolverReadOnly.isReadOnly(context, new TestBean(), "readWrite"));
		assertTrue(context.isPropertyResolved());

		// is bean, property != null, but doesn't exist --> exception
		try {
			resolver.isReadOnly(context, new TestBean(), "doesntExist");
			fail();
		} catch (PropertyNotFoundException e) {
			// fine
		}
	}

	public void testSetValue() {
		BeanELResolver resolver = new BeanELResolver();
		BeanELResolver resolverReadOnly = new BeanELResolver(true);

		// base == null --> unresolved
		context.setPropertyResolved(false);
		resolver.setValue(context, null, "foo", -1);
		assertFalse(context.isPropertyResolved());

		// base is bean, property == "readWrite" --> ok
		context.setPropertyResolved(false);
		TestBean bean = new TestBean();
		resolver.setValue(context, bean, "readWrite", 999);
		assertEquals(999, bean.getReadWrite());
		assertTrue(context.isPropertyResolved());

		// base is bean, property == "readOnly" --> exception
		try {
			resolver.setValue(context, new TestBean(), "readOnly", 1);
			fail();
		} catch (PropertyNotWritableException e) {
			// fine
		}

		// base is bean, property != null, but doesn't exist --> exception
		try {
			resolver.setValue(context, new TestBean(), "doesntExist", 1);
			fail();
		} catch (PropertyNotFoundException e) {
			// fine
		}

		// base is bean, property == "readWrite", invalid value --> exception
		try {
			resolver.setValue(context, new TestBean(), "readWrite", "invalid");
			fail();
		} catch (ELException e) {
			// fine, according to the spec...
		} catch (IllegalArgumentException e) {
			// violates the spec, but we'll accept this...
		}

		// read-only resolver
		try {
			resolverReadOnly.setValue(context, bean, "readWrite", 999);
			fail();
		} catch (PropertyNotWritableException e) {
			// fine
		}
	}
}
