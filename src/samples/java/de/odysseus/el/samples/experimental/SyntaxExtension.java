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
package de.odysseus.el.samples.experimental;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.tree.TreeBuilder;
import de.odysseus.el.tree.impl.Builder;
import de.odysseus.el.tree.impl.Parser;
import de.odysseus.el.tree.impl.Scanner;
import de.odysseus.el.tree.impl.ast.AstBinary;
import de.odysseus.el.tree.impl.ast.AstNode;
import de.odysseus.el.util.SimpleContext;

/**
 * Sample: add an operator to match against a regular expression, e.g.
 * <code>${'abab' matches '(ab)*'}</code> or <code>${'abab' ~ '(ab)*'}</code>.
 * 
 * Note: parser extensions are experimental at this time and the API is likely to change. 
 */
public class SyntaxExtension extends Builder {
	private static final long serialVersionUID = 1L;

	/**
	 * We need a new token for "~".
	 */
	static Scanner.ExtensionToken TILDE = new Scanner.ExtensionToken("~");

	/**
	 * And another token for keyword "matches".
	 */
	static Scanner.ExtensionToken KEYWORD = new Scanner.ExtensionToken("matches");

	/**
	 * This is our operator which will be passed to an <code>AstBinary</code>.
	 */
	static AstBinary.Operator OPERATOR = new AstBinary.SimpleOperator() {
		@Override
		public Object apply(TypeConverter converter, Object o1, Object o2) {
			return converter.convert(o1, String.class).matches(converter.convert(o2, String.class));
		}
	};
	
	/**
	 * This is our handler which will create the abstract syntax node.
	 */
	static Parser.ExtensionHandler HANDLER = new Parser.ExtensionHandler(Parser.ExtensionPoint.EQ) {
		@Override
		public AstNode createAstNode(AstNode... children) {
			return new AstBinary(children[0], children[1], OPERATOR);
		};
	};

	/**
	 * Here's our extended parser implementation.
	 */
	static class ExtendedParser extends Parser {		
		public ExtendedParser(Builder context, String input) {
			super(context, input);
			putExtensionHandler(TILDE, HANDLER);
			putExtensionHandler(KEYWORD, HANDLER);
		}

		/**
		 * Use a modified scanner which recognizes <code>'~'</code> and keyword <code>'matches'</code>.
		 */
		@Override
		protected Scanner createScanner(String expression) {
			return new Scanner(expression) {
				@Override
				protected Token keyword(String s) {
					if ("matches".equals(s)) {
						return KEYWORD;
					}
					return super.keyword(s);
				}
				
				@Override
				protected Token nextEval() throws ScanException {
					if (getInput().charAt(getPosition()) == '~') {
						return TILDE;
					}
					return super.nextEval();
				}
			};
		}
	}
	
	public SyntaxExtension() {
		super();
	}

	public SyntaxExtension(Feature... features) {
		super(features);
	}

	/**
	 * Make sure to use our modified parser.
	 */
	@Override
	protected Parser createParser(String expression) {
		return new ExtendedParser(this, expression);
	}
	
	public static void main(String[] args) {
		System.setProperty(TreeBuilder.class.getName(), SyntaxExtension.class.getName());		
		ExpressionFactory factory = new ExpressionFactoryImpl(System.getProperties());
		
		String[] expressions = {
				"${'abab' matches '(ab)*'}",
				"${'abab' matches '(ba)*'}",
				"${'abab' ~ '(ab)*'}",
				"${'abab' ~ '(ba)*'}"
		};

		SimpleContext context = new SimpleContext();
		for (String expression : expressions) {
			ValueExpression e = factory.createValueExpression(context, expression, boolean.class);
			System.out.println(expression + " --> " + e.getValue(context));
		}
	}
}
