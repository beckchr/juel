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
package javax.el;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

import javax.el.TestContext;

public class ELContextTest {

	@Test
	public void testContext() {
		ELContext context = new TestContext();
		assertNull(context.getContext(Integer.class));
		context.putContext(Integer.class, "foo");
		assertEquals("foo", context.getContext(Integer.class));
	}

	@Test
	public void testLocale() {
		ELContext context = new TestContext();
		assertNull(context.getLocale());
		context.setLocale(Locale.ENGLISH);
		assertEquals(Locale.ENGLISH, context.getLocale());
	}

	@Test
	public void testPropertyResolved() {
		ELContext context = new TestContext();
		assertFalse(context.isPropertyResolved());
		context.setPropertyResolved(true);
		assertTrue(context.isPropertyResolved());
	}
}
