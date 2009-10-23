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

import java.io.IOException;
import java.io.StringReader;

import javax.el.ELContext;
import javax.el.ELException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.odysseus.el.util.SimpleContext;

/**
 * SAX filter to provide attribute values as evaluated expressions.
 * See the <code>main(...)</code> method for a usage example.
 *
 * @author Christoph Beck
 */
public class AttributesFilter extends AbstractFilter {
	public AttributesFilter(XMLReader parent, ELContext context) {
		super(parent, context);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		AttributesImpl atts2 = new AttributesImpl(atts);
		for (int i = 0; i < atts2.getLength(); i++) {
			try {
				atts2.setValue(i, eval(atts2.getValue(i)));
			} catch (ELException e) {
				throw new SAXException("Error evaluating " + localName + "@" + atts.getLocalName(i), e);
			}
		}
		super.startElement(uri, localName, qName, atts2);
	}

	/**
	 * Usage example.
	 */
	public static void main(String[] args) throws SAXException, IOException {
		// create our expression context
		ELContext context = new SimpleContext();
		// set value for top-level property "home"
		context.getELResolver().setValue(context, null, "home", "/foo/bar");
		// create our filtered reader
		XMLReader reader = new AttributesFilter(XMLReaderFactory.createXMLReader(), context);
		// simple test content handler to print elements and attributes to stdout
		reader.setContentHandler(new DefaultHandler() {
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) {
				System.out.println("start " + localName);
				for (int i = 0; i < attributes.getLength(); i++) {
					System.out.println("  @" + attributes.getLocalName(i) + " = " + attributes.getValue(i));
				}
			}
			@Override
			public void endElement(String uri, String localName, String qName) {
				System.out.println("end " + localName);
			}
			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				System.out.println("text: "  + new String(ch, start, length));
			}
		});
		// parse our test input and watch the evaluated attributes
		String xml ="<test>foo<math>1+2=${1+2}</math><config file='${home}/config.xml'/>bar</test>";
		reader.parse(new InputSource(new StringReader(xml)));
	}
}
