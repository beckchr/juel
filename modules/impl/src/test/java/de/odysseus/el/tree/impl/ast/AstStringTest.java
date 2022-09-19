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

import javax.el.ELException;

import de.odysseus.el.TestCase;
import de.odysseus.el.tree.Bindings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AstStringTest extends TestCase {
	private Bindings bindings = new Bindings(null, null, null);
	
	AstString parseNode(String expression) {
		return (AstString)parse(expression).getRoot().getChild(0);
	}

	@Test
	public void testEval() {
		assertEquals("foo", parseNode("${'foo'}").eval(bindings, null));
	}

	@Test
	public void testAppendStructure() {
		StringBuilder s = new StringBuilder();
		parseNode("${'foo'}").appendStructure(s, bindings);
		assertEquals("'foo'", s.toString());
	}

	@Test
	public void testIsLiteralText() {
		assertFalse(parseNode("${'foo'}").isLiteralText());
	}

	@Test
	public void testIsLeftValue() {
		assertFalse(parseNode("${'foo'}").isLeftValue());
	}

	@Test
	public void testGetType() {
		assertNull(parseNode("${'foo'}").getType(bindings, null));
	}

	@Test
	public void testIsReadOnly() {
		assertTrue(parseNode("${'foo'}").isReadOnly(bindings, null));
	}

	@Test
	public void testSetValue() {
		try { parseNode("${'foo'}").setValue(bindings, null, null); fail(); } catch (ELException e) {}
	}

	@Test
	public void testGetValue() {
		assertEquals("1", parseNode("${'1'}").getValue(bindings, null, null));
		assertEquals(1, parseNode("${'1'}").getValue(bindings, null, Integer.class));
	}

	@Test
	public void testGetValueReference() {
		assertNull(parseNode("${'foo'}").getValueReference(null, null));
	}
}
