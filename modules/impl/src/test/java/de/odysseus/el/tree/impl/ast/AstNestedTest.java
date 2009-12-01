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

import de.odysseus.el.TestCase;

public class AstNestedTest extends TestCase {
	AstNested parseNode(String expression) {
		return (AstNested)parse(expression).getRoot().getChild(0);
	}

	public void testIsLeftValue() {
		assertFalse(parseNode("${(a)}").isLeftValue());
	}

	public void testEval() {
		assertEquals(1l, parseNode("${(1)}").eval(null, null));
	}

	public void testAppendStructure() {
		StringBuilder s = new StringBuilder();
		parseNode("${(1)}").appendStructure(s, null);
		assertEquals("(1)", s.toString());
	}

	public void testGetValueReference() {
		assertNull(parseNode("${(1)}").getValueReference(null, null));
	}
}
