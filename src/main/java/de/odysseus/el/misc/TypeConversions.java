/*
 * Copyright 2006, 2007 Odysseus Software GmbH
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

/**
 * Type Conversions as described in EL 2.1 specification (section 1.17).
 */
@Deprecated
public class TypeConversions {
	private static TypeConverterImpl DELEGATE;

	public static final Boolean coerceToBoolean(Object value) {
		return DELEGATE.coerceToBoolean(value);
	}

	public static final Character coerceToCharacter(Object value) {
		return DELEGATE.coerceToCharacter(value);
	}

	public static final <T extends Number> T coerceToNumber(Object value, Class<T> type) {
		return DELEGATE.coerceToNumber(value, type);
	}

	public static final String coerceToString(Object value) {
		return DELEGATE.coerceToString(value);
	}

	public static final <T extends Enum<T>> T coerceToEnum(Object value, Class<T> type) {
		return DELEGATE.coerceToEnum(value, type);
	}

	public static final Object coerceToType(Object value, Class<?> type) {
		return DELEGATE.coerceToType(value, type);
	}
}
