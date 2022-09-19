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
package de.odysseus.el;

import javax.el.ELException;

import de.odysseus.el.misc.TypeConverter;
import org.junit.jupiter.api.Test;

import static de.odysseus.el.TestCase.deserialize;
import static de.odysseus.el.TestCase.serialize;
import static org.junit.jupiter.api.Assertions.*;

public class ObjectValueExpressionTest {
	private TypeConverter converter = TypeConverter.DEFAULT;

	@Test
	public void testHashCode() {
		assertEquals("foo".hashCode(), new ObjectValueExpression(converter, "foo", Object.class).hashCode());
	}

	@Test
	public void testEqualsObject() {
		assertTrue(new ObjectValueExpression(converter, "foo", Object.class).equals(new ObjectValueExpression(converter, "foo", Object.class)));
		assertTrue(new ObjectValueExpression(converter, new String("foo"), Object.class).equals(new ObjectValueExpression(converter, "foo", Object.class)));
		assertFalse(new ObjectValueExpression(converter, "foo", Object.class).equals(new ObjectValueExpression(converter, "bar", Object.class)));
	}

	@Test
	public void testGetValue() {
		assertEquals("foo", new ObjectValueExpression(converter, "foo", Object.class).getValue(null));
	}

	@Test
	public void testGetExpressionString() {
		assertNull(new ObjectValueExpression(converter, "foo", Object.class).getExpressionString());
	}

	@Test
	public void testIsLiteralText() {
		assertFalse(new ObjectValueExpression(converter, "foo", Object.class).isLiteralText());
	}

	@Test
	public void testGetType() {
		assertNull(new ObjectValueExpression(converter, "foo", Object.class).getType(null));
	}

	@Test
	public void testIsReadOnly() {
		assertTrue(new ObjectValueExpression(converter, "foo", Object.class).isReadOnly(null));
	}

	@Test
	public void testSetValue() {
		try {
			new ObjectValueExpression(converter, "foo", Object.class).setValue(null, "bar");
			fail();
		} catch (ELException e) {}
	}

	@Test
	public void testSerialize() throws Exception {
		ObjectValueExpression expression = new ObjectValueExpression(converter, "foo", Object.class);
		assertEquals(expression, deserialize(serialize(expression)));
	}
}
