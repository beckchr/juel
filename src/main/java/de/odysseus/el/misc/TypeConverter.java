package de.odysseus.el.misc;

import java.io.Serializable;

import javax.el.ELException;

public interface TypeConverter extends Serializable {
	/**
	 * Default conversions as from JSR245.
	 */
	public static final TypeConverter DEFAULT = new TypeConverterImpl();
	
	/**
	 * Convert the given input value to the specified target type.
	 * @param value input value
	 * @param type target type
	 * @return conversion result
	 */
	public <T> T convert(Object value, Class<T> type) throws ELException;
}
