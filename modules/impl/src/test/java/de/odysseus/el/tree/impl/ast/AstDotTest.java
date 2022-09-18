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
package de.odysseus.el.tree.impl.ast;

import java.util.Arrays;

import jakarta.el.BeanELResolver;
import jakarta.el.ELException;
import jakarta.el.MethodInfo;
import jakarta.el.PropertyNotFoundException;
import jakarta.el.ValueExpression;

import de.odysseus.el.TestCase;
import de.odysseus.el.test.TestClass;
import de.odysseus.el.tree.Bindings;
import de.odysseus.el.util.SimpleContext;
import de.odysseus.el.util.SimpleResolver;

public class AstDotTest extends TestCase {
	AstDot parseNode(String expression) {
		return (AstDot)parse(expression).getRoot().getChild(0);
	}

	SimpleContext context;
	Bindings bindings;
	
	long foo = 1l;
	
	public long getFoo() {
		return foo;
	}

	public void setFoo(long value) {
		foo = value;
	}

	public long bar() {
		return 1l;
	}

	public long bar(long value) {
		return value;
	}

	public TestClass getTestClass() {
		return new TestClass();
	}
	
	public Object getNullObject() {
		return null;
	}
	
	@Override
	protected void setUp() throws Exception {
		context = new SimpleContext(new SimpleResolver(new BeanELResolver()));
		context.getELResolver().setValue(context, null, "base", this);
		
		bindings = new Bindings(null, new ValueExpression[1]);
	}

	public void testEval() {
		try { parseNode("${base.bad}").eval(bindings, context); fail(); } catch (ELException e) {}
		assertEquals(1l, parseNode("${base.foo}").eval(bindings, context));
	}

	public void testAppendStructure() {
		StringBuilder s = new StringBuilder();
		parseNode("${foo.bar}").appendStructure(s, new Bindings(null, null, null));
		assertEquals("foo.bar", s.toString());
	}

	public void testIsLiteralText() {
		assertFalse(parseNode("${foo.bar}").isLiteralText());
	}

	public void testIsLeftValue() {
		assertFalse(parseNode("${'foo'.bar}").isLeftValue());
		assertTrue(parseNode("${foo.bar}").isLeftValue());
	}

	public void testGetType() {
		try { parseNode("${base.bad}").getType(bindings, context); fail(); } catch (ELException e) {}
		assertEquals(long.class, parseNode("${base.foo}").getType(bindings, context));
		assertNull(parseNode("${'base'.foo}").getType(bindings, context));
	}

	public void testIsReadOnly() {
		assertFalse(parseNode("${base.foo}").isReadOnly(bindings, context));
		assertTrue(parseNode("${'base'.foo}").isReadOnly(bindings, context));
	}

	public void testSetValue() {
		try { parseNode("${base.bad}").setValue(bindings, context, "good"); fail(); } catch (ELException e) {}
		parseNode("${base.foo}").setValue(bindings, context, 2l);
		assertEquals(2l, getFoo());
		parseNode("${base.foo}").setValue(bindings, context, "3");
		assertEquals(3l, getFoo());
	}

	public void testGetValue() {
		assertEquals(1l, parseNode("${base.foo}").getValue(bindings, context, null));
		assertEquals("1", parseNode("${base.foo}").getValue(bindings, context, String.class));
		assertNull(parseNode("${base.nullObject.class}").getValue(bindings, context, Object.class));
	}

	public void testGetValueReference() {
		assertEquals(this, parseNode("${base.foo}").getValueReference(bindings, context).getBase());
		assertEquals("foo", parseNode("${base.foo}").getValueReference(bindings, context).getProperty());
	}

	public void testInvoke() {
		assertEquals(1l, parseNode("${base.bar}").invoke(bindings, context, long.class, new Class[0], null));
		assertEquals(2l, parseNode("${base.bar}").invoke(bindings, context, null, new Class[]{long.class}, new Object[]{2l}));

		assertEquals(42, parseNode("${base.testClass.anonymousTestInterface.fourtyTwo}").invoke(bindings, context, null, new Class[0], null));
		assertEquals(42, parseNode("${base.testClass.nestedTestInterface.fourtyTwo}").invoke(bindings, context, null, new Class[0], null));

		try {
			parseNode("${base.nullObject.class}").invoke(bindings, context, null, null, new Object[0]);
			fail();
		} catch (PropertyNotFoundException e) {
			// ok
		}
	}

	public void testGetMethodInfo() {
		MethodInfo info = null;
		
		// long bar()
		info = parseNode("${base.bar}").getMethodInfo(bindings, context, long.class, new Class[0]);
		assertEquals("bar", info.getName());
		assertTrue(Arrays.equals(new Class[0], info.getParamTypes()));
		assertEquals(long.class, info.getReturnType());
		
		// long bar(long)
		info = parseNode("${base.bar}").getMethodInfo(bindings, context, null, new Class[]{long.class});
		assertEquals("bar", info.getName());
		assertTrue(Arrays.equals(new Class[]{long.class}, info.getParamTypes()));
		assertEquals(long.class, info.getReturnType());

		// bad arg type
		try { info = parseNode("${base.bar}").getMethodInfo(bindings, context, null, new Class[]{String.class}); fail(); } catch(ELException e) {}
		// bad return type
		try { info = parseNode("${base.bar}").getMethodInfo(bindings, context, String.class, new Class[0]); fail(); } catch(ELException e) {}
	}
}
