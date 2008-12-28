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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELException;

/**
 * Type Conversions as described in EL 2.1 specification (section 1.17).
 */
public class TypeConverterImpl implements TypeConverter {
	private static final long serialVersionUID = 1L;

	protected static final Long LONG_ZERO = Long.valueOf(0L);
	protected static final Map<Class<?>,Class<?>> WRAPPER_TYPES;
	
	static {
		HashMap<Class<?>,Class<?>> wrapperTypes = new HashMap<Class<?>, Class<?>>();
		wrapperTypes = new HashMap<Class<?>,Class<?>>();
		wrapperTypes.put(Boolean.TYPE, Boolean.class);
		wrapperTypes.put(Character.TYPE, Character.class);
		wrapperTypes.put(Byte.TYPE, Byte.class);
		wrapperTypes.put(Short.TYPE, Short.class);
		wrapperTypes.put(Integer.TYPE, Integer.class);
		wrapperTypes.put(Long.TYPE, Long.class);
		wrapperTypes.put(Float.TYPE, Float.class);
		wrapperTypes.put(Double.TYPE, Double.class);
		WRAPPER_TYPES = Collections.unmodifiableMap(wrapperTypes);
	}

	protected Boolean coerceToBoolean(Object value) {
		if (value == null || "".equals(value)) {
			return Boolean.FALSE;
		}
		if (value instanceof Boolean) {
			return (Boolean)value;
		}
		if (value instanceof String) {
			return Boolean.valueOf((String)value);
		}
		throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), Boolean.class));
	}

	protected Character coerceToCharacter(Object value) {
		if (value == null || "".equals(value)) {
			return Character.valueOf((char)0);
		}
		if (value instanceof Character) {
			return (Character)value;
		}
		if (value instanceof Number) {
			return new Character((char)((Number)value).shortValue());
		}
		if (value instanceof String) {
			return new Character(((String)value).charAt(0));
		}
		throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), Character.class));
	}

	protected Number coerceStringToNumber(String value, Class<? extends Number> type) {
		try {
			if (type == BigDecimal.class) {
				return new BigDecimal(value);
			}
			if (type == BigInteger.class) {
				return new BigInteger(value);
			}
			if (type == Byte.class) {
				return Byte.valueOf(value);
			}
			if (type == Short.class) {
				return Short.valueOf(value);
			}
			if (type == Integer.class) {
				return Integer.valueOf(value);
			}
			if (type == Long.class) {
				return Long.valueOf(value);
			}
			if (type == Float.class) {
				return Float.valueOf(value);
			}
			if (type == Double.class) {
				return Double.valueOf(value);
			}
		} catch (NumberFormatException e) {
			throw new ELException(LocalMessages.get("error.coerce.value", value, type));
		}
		throw new ELException(LocalMessages.get("error.coerce.type", String.class, type));
	}

	protected Number coerceNumberToNumber(Number value, Class<? extends Number> type) {
		if (type.isInstance(value)) {
			return value;
		}
		if (type == BigInteger.class) {
			if (value instanceof BigDecimal) {
				return ((BigDecimal)value).toBigInteger();
			}
			return BigInteger.valueOf((value).longValue());
		}
		if (type == BigDecimal.class) {
			if (value instanceof BigInteger) {
				return new BigDecimal((BigInteger)value);
			}
			return new BigDecimal(value.doubleValue());
		}
		if (type == Byte.class) {
			return Byte.valueOf(value.byteValue());
		}
		if (type == Short.class) {
			return Short.valueOf(value.shortValue());
		}
		if (type == Integer.class) {
			return Integer.valueOf(value.intValue());
		}
		if (type == Long.class) {
			return Long.valueOf(value.longValue());
		}
		if (type == Float.class) {
			return Float.valueOf(value.floatValue());
		}
		if (type == Double.class) {
			return Double.valueOf(value.doubleValue());
		}
		throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), type));
	}

	@SuppressWarnings("unchecked")
	protected <T extends Number> T coerceToNumber(Object value, Class<T> type) {
		if (value == null || "".equals(value)) {
			return (T)coerceNumberToNumber(LONG_ZERO, type);
		}
		if (value instanceof Character) {
			return (T)coerceNumberToNumber(Short.valueOf((short)((Character)value).charValue()), type);
		}
		if (value instanceof Number) {
			return (T)coerceNumberToNumber((Number)value, type);
		}
		if (value instanceof String) {
			return (T)coerceStringToNumber((String)value, type);
		}
		throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), type));
	}

	protected String coerceToString(Object value) {
		if (value == null) {
			return "";
		}
		if (value instanceof String) {
			return (String)value;
		}
		if (value instanceof Enum) {
			return ((Enum<?>)value).name();
		}
		return value.toString();
	}

	@SuppressWarnings("unchecked")
	protected <T extends Enum<T>> T coerceToEnum(Object value, Class<T> type) {
		if (value == null || "".equals(value)) {
			return null;
		}
		if (type.isInstance(value)) {
			return (T)value;
		}
		if (value instanceof String) {
			try {
				return Enum.valueOf(type, (String)value);
			} catch (IllegalArgumentException e) {
				throw new ELException(LocalMessages.get("error.coerce.value", value, type));
			}
		}
		throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), type));
	}

	protected Object coerceStringToType(String value, Class<?> type) {
		PropertyEditor editor = PropertyEditorManager.findEditor(type);
		if (editor == null) {
			if ("".equals(value)) {
				return null;
			}
			throw new ELException(LocalMessages.get("error.coerce.type", String.class, type));
		} else {
			if ("".equals(value)) {
				try {
					editor.setAsText(value);
				} catch (IllegalArgumentException e) {
					return null;
				}
			} else {
				try {
					editor.setAsText(value);
				} catch (IllegalArgumentException e) {
					throw new ELException(LocalMessages.get("error.coerce.value", value, type));
				}
			}
			return editor.getValue();
		}
	}

	@SuppressWarnings("unchecked")
	protected Object coerceToType(Object value, Class<?> type) {
		if (type == String.class) {
			return coerceToString(value);
		}
		if (type.isPrimitive()) {
			type = WRAPPER_TYPES.get(type);
		}
		if (Number.class.isAssignableFrom(type)) {
			return coerceToNumber(value, (Class<? extends Number>)type);
		}
		if (type == Character.class) {
			return coerceToCharacter(value);
		}
		if (type == Boolean.class) {
			return coerceToBoolean(value);
		}
		if (Enum.class.isAssignableFrom(type)) {
			return coerceToEnum(value, (Class<? extends Enum>)type);
		}
		if (value == null) {
			return null;
		}
		if (type.isInstance(value)) {
			return value;
		}
		if (value instanceof String) {
			return coerceStringToType((String)value, type);
		}
		throw new ELException(LocalMessages.get("error.coerce.type", value.getClass(), type));
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(getClass());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@SuppressWarnings("unchecked")
	public <T> T convert(Object value, Class<T> type) throws ELException {
		return (T)coerceToType(value, type);
	}
}
