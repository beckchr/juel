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

public class AstCompositeTest extends TestCase {
	private Bindings bindings = new Bindings(null, null, null);
	
	AstComposite parseNode(String expression) {
		return (AstComposite)parse(expression).getRoot();
	}

	public void testEval() {
		assertEquals("101", parseNode("${1}0${1}").eval(bindings, null));
	}

	public void testAppendStructure() {
		StringBuilder s = new StringBuilder();
		parseNode("${1}0${1}").appendStructure(s, bindings);
		assertEquals("${1}0${1}", s.toString());
	}

	public void testIsLiteralText() {
		assertFalse(parseNode("${1}0${1}").isLiteralText());
	}

	public void testIsLeftValue() {
		assertFalse(parseNode("${1}0${1}").isLeftValue());
	}

	public void testGetType() {
		assertNull(parseNode("${1}0${1}").getType(bindings, null));
	}

	public void testIsReadOnly() {
		assertTrue(parseNode("${1}0${1}").isReadOnly(bindings, null));
	}

	public void testSetValue() {
		try { parseNode("${1}0${1}").setValue(bindings, null, null); fail(); } catch (ELException e) {}
	}

	public void testGetValue() {
		assertEquals("101", parseNode("${1}0${1}").getValue(bindings, null, null));
		assertEquals(101l, parseNode("${1}0${1}").getValue(bindings, null, Long.class));
	}

	public void testGetValueReference() {
		assertNull(parseNode("${1}0${1}").getValueReference(bindings, null));
	}
}
