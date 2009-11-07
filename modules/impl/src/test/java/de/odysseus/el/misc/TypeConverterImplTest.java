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
package de.odysseus.el.misc;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.el.ELException;

import junit.framework.TestCase;

/**
 * JUnit test case for {@link de.odysseus.el.misc.TypeConverterImpl}.
 *
 * @author Christoph Beck
 */
public class TypeConverterImplTest extends TestCase {
	/**
	 * Test property editor for date objects.
	 * Accepts integer strings as text input and uses them as time value in milliseconds.
	 */
	public static class DateEditor implements PropertyEditor {
		private Date value;
		public void addPropertyChangeListener(PropertyChangeListener listener) {}
		public String getAsText() { return value == null ? null : "" + value.getTime(); }
		public Component getCustomEditor() { return null; }
		public String getJavaInitializationString() { return null; }
		public String[] getTags() { return null; }
		public Object getValue() { return value; }
		public boolean isPaintable() { return false; }
		public void paintValue(Graphics gfx, Rectangle box) {}
		public void removePropertyChangeListener(PropertyChangeListener listener) {}
		public void setAsText(String text) throws IllegalArgumentException { value = new Date(Long.parseLong(text)); }
		public void setValue(Object value) { this.value = (Date)value; }
		public boolean supportsCustomEditor() { return false; }
	}

	static {
		PropertyEditorManager.registerEditor(Date.class, DateEditor.class);
	}
	
	/**
	 * Test enum type
	 */
	static enum Foo { BAR };
	
	TypeConverterImpl converter = new TypeConverterImpl();

	public void testToBoolean() {
		assertFalse(converter.coerceToBoolean(null));
		assertFalse(converter.coerceToBoolean(""));
		assertTrue(converter.coerceToBoolean(Boolean.TRUE));
		assertFalse(converter.coerceToBoolean(Boolean.FALSE));
		assertTrue(converter.coerceToBoolean("true"));
		assertFalse(converter.coerceToBoolean("false"));
		assertFalse(converter.coerceToBoolean("yes")); // Boolean.valueOf(String) never throws an exception...
	}

	public void testToCharacter() {
		assertEquals(Character.valueOf((char)0), converter.coerceToCharacter(null));
		assertEquals(Character.valueOf((char)0), converter.coerceToCharacter(""));
		Character c = Character.valueOf((char)99);
		assertSame(c, converter.coerceToCharacter(c));
		try {
			converter.coerceToCharacter(Boolean.TRUE);
			fail();
		} catch (ELException e) {}
		try {
			converter.coerceToCharacter(Boolean.FALSE);
			fail();
		} catch (ELException e) {}
		assertEquals(c, converter.coerceToCharacter(new Byte((byte)99)));
		assertEquals(c, converter.coerceToCharacter(new Short((short)99)));
		assertEquals(c, converter.coerceToCharacter(new Integer(99)));
		assertEquals(c, converter.coerceToCharacter(new Long(99)));
		assertEquals(c, converter.coerceToCharacter(new Float((float)99.5)));
		assertEquals(c, converter.coerceToCharacter(new Double(99.5)));
		assertEquals(c, converter.coerceToCharacter(new BigDecimal("99.5")));
		assertEquals(c, converter.coerceToCharacter(new BigInteger("99")));
		assertEquals(c, converter.coerceToCharacter("c#"));
		try {
			converter.coerceToCharacter(this);
			fail();
		} catch (ELException e) {}
	}

	private <T extends Number> void testToNumber(Class<T> type, T zero, T ninetynine) {
		assertEquals(zero, converter.coerceToNumber(null, type));
		assertEquals(zero, converter.coerceToNumber("", type));
		assertEquals(ninetynine, converter.coerceToNumber(Character.valueOf('c'), type));
		assertEquals(ninetynine, converter.coerceToNumber(new Byte((byte)99), type));
		assertEquals(ninetynine, converter.coerceToNumber(new Short((short)99), type));
		assertEquals(ninetynine, converter.coerceToNumber(new Integer(99), type));
		assertEquals(ninetynine, converter.coerceToNumber(new Long(99), type));
		assertEquals(ninetynine, converter.coerceToNumber(new Float(99), type));
		assertEquals(ninetynine, converter.coerceToNumber(new Double(99), type));
		assertEquals(ninetynine, converter.coerceToNumber(new BigDecimal(99), type));
		assertEquals(ninetynine, converter.coerceToNumber(new BigInteger("99"), type));
		assertEquals(ninetynine, converter.coerceToNumber(ninetynine.toString(), type));
	}

	public void testToNumber() {
		testToNumber(Byte.class, new Byte((byte)0), new Byte((byte)99));
		testToNumber(Short.class, new Short((short)0), new Short((short)99));
		testToNumber(Integer.class, new Integer(0), new Integer(99));
		testToNumber(Long.class, new Long(0), new Long(99));
		testToNumber(Float.class, new Float(0), new Float(99));
		testToNumber(Double.class, new Double(0), new Double(99));
		testToNumber(BigDecimal.class, new BigDecimal(0), new BigDecimal(99));
		testToNumber(BigInteger.class, new BigInteger("0"), new BigInteger("99"));
		try {
			converter.coerceToNumber("foo", Long.class);
			fail();
		} catch (ELException e) {
		} catch (NumberFormatException e) {
			// allow?
		}
	}

	public void testToString() {
		assertSame("foo", converter.coerceToString("foo"));
		assertEquals("", converter.coerceToString(null));
		assertEquals(Foo.BAR.name(), converter.coerceToString(Foo.BAR));
		Object value = new BigDecimal("99.345");
		assertEquals(value.toString(), converter.coerceToString(value));
	}

	public void testToEnum() {
		assertNull(converter.coerceToEnum(null, Foo.class));
		assertSame(Foo.BAR, converter.coerceToEnum(Foo.BAR, Foo.class));
		assertNull(converter.coerceToEnum("", Foo.class));
		assertSame(Foo.BAR, converter.coerceToEnum("BAR", Foo.class));
	}

	public void testToType() {
		assertEquals("foo", converter.coerceToType("foo", String.class));
		assertEquals(new Long(0), converter.coerceToType("0", Long.class));
		assertEquals(new Character('c'), converter.coerceToType("c", Character.class));
		assertEquals(Boolean.TRUE, converter.coerceToType("true", Boolean.class));
		assertEquals(Foo.BAR, converter.coerceToType("BAR", Foo.class));
		// other types
		assertNull(converter.coerceToType(null, Object.class));
		Object value = new Date(0);
		assertSame(value, converter.coerceToType(value, Object.class));
		assertEquals(new Date(0), converter.coerceToType("0", Date.class));
		assertNull(converter.coerceToType("", Date.class));
		try {
			converter.coerceToType("foo", Date.class);
			fail();
		} catch (Exception e) {}
		assertNull(converter.coerceToType("", getClass()));
		try {
			converter.coerceToType("bar", getClass());
			fail();
		} catch (Exception e) {}
		assertEquals(false, converter.coerceToType("false", boolean.class));
		assertEquals((byte)0, converter.coerceToType("0", byte.class));
		assertEquals((short)0, converter.coerceToType("0", short.class));
		assertEquals(0, converter.coerceToType("0", int.class));
		assertEquals((long)0, converter.coerceToType("0", long.class));
		assertEquals((float)0, converter.coerceToType("0", float.class));
		assertEquals((double)0, converter.coerceToType("0", double.class));
		assertEquals('0', converter.coerceToType("0", char.class));
	}
}
