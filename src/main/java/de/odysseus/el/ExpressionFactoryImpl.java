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
package de.odysseus.el;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.Properties;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;

import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.tree.TreeBuilder;
import de.odysseus.el.tree.TreeStore;
import de.odysseus.el.tree.impl.Builder;
import de.odysseus.el.tree.impl.Cache;
import de.odysseus.el.tree.impl.Builder.Feature;

/**
 * Expression factory implementation.
 *
 * This class is also used as an EL "service provider".
 * The <em>JUEL</em> jar file specifies this class as el expression factory
 * implementation in <code>META-INF/services/javax.el.ExpressionFactory</code>.
 * Calling {@link ExpressionFactory#newInstance()} will then return
 * an instance of this class, configured as described below.
 * 
 * The default constructor loads properties from resources
 * <ol>
 * <li>
 * 	<code>JAVA_HOME/lib/el.properties</code> -
 * 	If this file exists and	if it contains property <code>javax.el.ExpressionFactory</code>
 * 	whose value is the name of this class, these properties are taken as default properties.
 * </li>
 * <li>
 * 	<code>el.properties</code> on your classpath. These properties override the
 * 	properties from <code>JAVA_HOME/lib/el.properties</code>.
 * </li>
 * </ol>
 * There's also a constructor to explicitly pass in an instance of {@link Properties}.
 * 
 * Having this, the following properties are read:
 * <ul>
 * <li>
 * 	<code>javax.el.cacheSize</code> - cache size (int, default is 1000)
 * </li>
 * <li>
 * 	<code>javax.el.methodInvocations</code> - allow method invocations
 * 	as in <code>${foo.bar(baz)}</code> (boolean, default is <code>false</code>).
 * </li>
 * <li>
 * 	<code>javax.el.nullProperties</code> - resolve <code>null</code> properties
 * 	as in <code>${foo[null]}</code> (boolean, default is <code>false</code>).
 * </li>
 * <li>
 * 	<code>javax.el.varArgs</code> - support function/method calls using varargs
 *  (boolean, default is <code>false</code>).
 * </li>
 * </ul>
 * 
 * @author Christoph Beck
 */
public class ExpressionFactoryImpl extends javax.el.ExpressionFactory {
	private final TreeStore store;
	private final TypeConverter converter;
	
	/**
	 * Create a new expression factory using the default parser and tree cache implementations.
	 * The builder and cache are configured from <code>el.properties</code> (see above).
	 * The maximum cache size will be 1000 unless overridden in <code>el.properties</code>.
	 */
	public ExpressionFactoryImpl() {
		Properties properties = loadProperties("el.properties");
		store = createTreeStore(1000, properties);
		converter = createTypeConverter(properties);
	}

	/**
	 * Create a new expression factory using the default builder and cache implementations.
	 * The builder and cache are configured using the specified properties.
	 * The maximum cache size will be 1000 unless overridden by property <code>javax.el.cacheSize</code>.
	 * @param properties used to initialize this factory (may be <code>null</code>)
	 */
	public ExpressionFactoryImpl(Properties properties) {
		store = createTreeStore(1000, properties);
		converter = createTypeConverter(properties);
	}

	/**
	 * Create a new expression factory.
	 * @param store the tree store used to parse and cache parse trees.
	 */
	public ExpressionFactoryImpl(TreeStore store) {
		this(store, TypeConverter.DEFAULT);
	}

	/**
	 * Create a new expression factory.
	 * @param store the tree store used to parse and cache parse trees.
	 * @param converter custom type converter
	 */
	public ExpressionFactoryImpl(TreeStore store, TypeConverter converter) {
		this.store = store;
		this.converter = converter;
	}

	private Properties loadDefaultProperties() {
		String home = System.getProperty("java.home");
		String path = home + File.separator + "lib" + File.separator + "el.properties";
		File file = new File(path);
		if (file.exists()) {
			Properties properties = new Properties();
			InputStream input = null;
			try {
				properties.load(input = new FileInputStream(file));
			} catch (IOException e) {
				throw new ELException("Cannot read default EL properties", e);
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					// ignore...
				}
			}
			String clazz = properties.getProperty("javax.el.ExpressionFactory");
			if (getClass().getName().equals(clazz)) {
				return properties;
			}
		}
		return null;
	}

	private Properties loadProperties(String path) {
		Properties properties = new Properties(loadDefaultProperties());

		// try to find and load properties
		InputStream input = null;
		try {
			input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		} catch (SecurityException e) {
			input = ClassLoader.getSystemResourceAsStream(path);
		}
		if (input != null) {
			try {
				properties.load(input);
			} catch (IOException e) {
				throw new ELException("Cannot read EL properties", e);
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					// ignore...
				}
			}
		}

		return properties;
	}

	/**
	 * Create the factory's tree store.
	 * This implementation creates a new tree store using the default builder and cache
	 * implementations. The builder and cache are configured using the specified properties.
	 * The maximum cache size will be as specified unless overridden by property
	 * <code>javax.el.cacheSize</code>.
	 */
	protected TreeStore createTreeStore(int defaultCacheSize, Properties properties) {
		// create builder
		EnumSet<Builder.Feature> features = EnumSet.noneOf(Builder.Feature.class);
		if (properties != null) {
			if (Boolean.valueOf(properties.getProperty("javax.el.methodInvocations", "false"))) {
				features.add(Builder.Feature.METHOD_INVOCATIONS);
			}
			if (Boolean.valueOf(properties.getProperty("javax.el.nullProperties", "false"))) {
				features.add(Builder.Feature.NULL_PROPERTIES);
			}
			if (Boolean.valueOf(properties.getProperty("javax.el.varArgs", "false"))) {
				features.add(Builder.Feature.VARARGS);
			}
		}
		
		TreeBuilder builder = createTreeBuilder(properties, features.toArray(new Builder.Feature[0]));

		// create cache
		int cacheSize = defaultCacheSize;
		if (properties != null && properties.containsKey("javax.el.cacheSize")) {
			try {
				cacheSize = Integer.parseInt(properties.getProperty("javax.el.cacheSize"));
			} catch (NumberFormatException e) {
				throw new ELException("Cannot parse EL property javax.el.cacheSize", e);
			}
		}
		Cache cache = cacheSize > 0 ? new Cache(cacheSize) : null;

		return new TreeStore(builder, cache);
	}

	/**
	 * Create the factory's type converter.
	 * This implementation takes the <code>de.odysseus.el.misc.TypeConverter</code> property
	 * as the name of a class implementing the <code>de.odysseus.el.misc.TypeConverter</code> interface.
	 * If the property is not set, the default converter (<code>TypeConverter.DEFAULT</code>) is used.
	 */
	protected TypeConverter createTypeConverter(Properties properties) {
		Class<?> clazz = load(TypeConverter.class, properties);
		if (clazz == null) {
			return TypeConverter.DEFAULT;
		}
		try {
			return TypeConverter.class.cast(clazz.newInstance());
		} catch (Exception e) {
            throw new ELException("TypeConverter " + clazz + " could not be instantiated", e);
		}
	}

	/**
	 * Create the factory's builder.
	 * This implemenation takes the <code>de.odysseus.el.tree.TreeBuilder</code> property
	 * as a name of a class implementing the <code>de.odysseus.el.tree.TreeBuilder</code> interface.
	 * If the property is not set, a plain <code>de.odysseus.el.tree.impl.Builder</code> is used.
	 * If the configured class is a subclass of <code>de.odysseus.el.tree.impl.Builder</code> and
	 * which provides a constructor taking an array of <code>Builder.Feature</code>, this constructor
	 * will be invoked. Otherwise, the default constructor will be used. 
	 */
	protected TreeBuilder createTreeBuilder(Properties properties, Feature... features) {
		Class<?> clazz = load(TreeBuilder.class, properties);
		if (clazz == null) {
			return new Builder(features);
		}
		try {
			if (Builder.class.isAssignableFrom(clazz)) {
				Constructor<?> constructor = clazz.getConstructor(Feature[].class);
				if (constructor == null) {
					if (features == null || features.length == 0) {
						return TreeBuilder.class.cast(clazz.newInstance());
					} else {
			            throw new ELException("Builder " + clazz + " is missing constructor (can't pass features)");
					}
				} else {
					return TreeBuilder.class.cast(constructor.newInstance((Object)features));
				}
			} else {
				return TreeBuilder.class.cast(clazz.newInstance());
			}
		} catch (Exception e) {
            throw new ELException("TreeBuilder " + clazz + " could not be instantiated", e);
		}
	}

	private Class<?> load(Class<?> clazz, Properties properties) {
		if (properties != null) {
			String className = properties.getProperty(clazz.getName());
			if (className != null) {
		        ClassLoader loader;
		        try {
		            loader = Thread.currentThread().getContextClassLoader();
		        } catch (Exception e) {
		            throw new ELException("Could not get context class loader", e);
		        }
		        try {
		            return loader == null ? Class.forName(className) : loader.loadClass(className);
		        } catch (ClassNotFoundException e) {
		            throw new ELException("Class " + className + " not found", e);
		        } catch (Exception e) {
		            throw new ELException("Class " + className + " could not be instantiated", e);
		        }
			}			
		}
		return null;
	}

	@Override
	public final Object coerceToType(Object obj, Class<?> targetType) {
		return converter.convert(obj, targetType);
	}

	@Override
	public final ObjectValueExpression createValueExpression(Object instance, Class<?> expectedType) {
		return new ObjectValueExpression(converter, instance, expectedType);
	}

	@Override
	public final TreeValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
		return new TreeValueExpression(store, context.getFunctionMapper(), context.getVariableMapper(), converter, expression, expectedType);
	}

	@Override
	public final TreeMethodExpression createMethodExpression(ELContext context, String expression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
		return new TreeMethodExpression(store, context.getFunctionMapper(), context.getVariableMapper(), converter, expression, expectedReturnType, expectedParamTypes);
	}
}
