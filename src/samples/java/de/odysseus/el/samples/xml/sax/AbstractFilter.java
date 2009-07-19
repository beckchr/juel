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
package de.odysseus.el.samples.xml.sax;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import de.odysseus.el.ExpressionFactoryImpl;

/**
 * SAX filter to provide template text as evaluated expressions.
 * See the <code>main(...)</code> method for a usage example.
 *
 * @author Christoph Beck
 */
public abstract class AbstractFilter extends XMLFilterImpl {
	private static final ExpressionFactory factory = new ExpressionFactoryImpl();

	private final ELContext context;

	protected AbstractFilter(XMLReader parent, ELContext context) {
		super(parent);

		this.context = context;
	}

	protected String eval(String value) throws ELException {
		return (String)factory.createValueExpression(context, value, String.class).getValue(context);
	}
}
