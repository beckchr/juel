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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import junit.framework.TestCase;

public class ExpressionFactoryTest extends TestCase {
	private String getFactoryClassName() throws IOException {
		String className = null;
		String serviceId = "META-INF/services/" + ExpressionFactory.class.getName();
		InputStream input = getClass().getClassLoader().getResourceAsStream(serviceId);
		if (input != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			className = reader.readLine();
			reader.close();
		}
		return className;
	}
	
	public void testNewInstance() throws IOException {
		ExpressionFactory factory = ExpressionFactory.newInstance();
		assertNotNull(factory);
		assertEquals(factory.getClass().getName(), getFactoryClassName());
	}

	public void testNewInstanceProperties() throws IOException  {
		Properties properties = new Properties();
		
		ExpressionFactory factory = ExpressionFactory.newInstance(properties);
		assertNotNull(factory);
		assertEquals(factory.getClass().getName(), getFactoryClassName());
		if (TestFactory.class.getName().equals(getFactoryClassName())) {
			assertSame(properties, ((TestFactory)factory).properties);
		}
	}
}
