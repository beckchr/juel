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

import jakarta.el.ELException;

import de.odysseus.el.TestCase;
import de.odysseus.el.tree.Bindings;

public class AstNullTest extends TestCase {
	private Bindings bindings = new Bindings(null, null, null);
	
	AstNull parseNode(String expression) {
		return (AstNull)parse(expression).getRoot().getChild(0);
	}

	public void testEval() {
		assertNull(parseNode("${null}").eval(bindings, null));
	}

	public void testAppendStructure() {
		StringBuilder s = new StringBuilder();
		parseNode("${null}").appendStructure(s, bindings);
		assertEquals("null", s.toString());
	}

	public void testIsLiteralText() {
		assertFalse(parseNode("${null}").isLiteralText());
	}

	public void testIsLeftValue() {
		assertFalse(parseNode("${null}").isLeftValue());
	}

	public void testGetType() {
		assertNull(parseNode("${null}").getType(bindings, null));
	}

	public void testIsReadOnly() {
		assertTrue(parseNode("${null}").isReadOnly(bindings, null));
	}

	public void testSetValue() {
		try { parseNode("${null}").setValue(bindings, null, null); fail(); } catch (ELException e) {}
	}

	public void testGetValue() {
		assertNull(parseNode("${null}").getValue(bindings, null, null));
		assertEquals("", parseNode("${null}").getValue(bindings, null, String.class));
	}

	public void testGetValueReference() {
		assertNull(parseNode("${null}").getValueReference(null, null));
	}
}
