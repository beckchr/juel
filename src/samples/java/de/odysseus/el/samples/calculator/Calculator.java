package de.odysseus.el.samples.calculator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.tree.TreeBuilderException;
import de.odysseus.el.util.SimpleContext;

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

/**
 * Simple command line calculator demo.
 * Read one expression per line from stdin (without "${" and "}").
 * Expression "m[0-9]*" is used to save the last evaluation result to "memory".
 * Other expressions are evaluated.
 * An empty line terminates the calculator.
 */
public class Calculator {
	public static void main(String[] args) throws NoSuchMethodException, IOException {
		ExpressionFactory factory = new ExpressionFactoryImpl();

		SimpleContext context = new SimpleContext();

		// variables e, pi
		context.setVariable("e", factory.createValueExpression(Math.E, double.class));
		context.setVariable("pi", factory.createValueExpression(Math.PI, double.class));
		
		// functions sin, cos, tan, exp, log, abs, sqrt, min, max, pow
		context.setFunction("", "sin", Math.class.getMethod("sin", double.class));
		context.setFunction("", "cos", Math.class.getMethod("cos", double.class));
		context.setFunction("", "tan", Math.class.getMethod("tan", double.class));
		context.setFunction("", "exp", Math.class.getMethod("exp", double.class));
		context.setFunction("", "log", Math.class.getMethod("log", double.class));
		context.setFunction("", "abs", Math.class.getMethod("abs", double.class));
		context.setFunction("", "sqrt", Math.class.getMethod("sqrt", double.class));
		context.setFunction("", "min", Math.class.getMethod("min", double.class, double.class));
		context.setFunction("", "max", Math.class.getMethod("max", double.class, double.class));
		context.setFunction("", "pow", Math.class.getMethod("pow", double.class, double.class));

		// print out the rules of the game...
		System.out.println("> Enter one expression per line (without \"${\" and \"}\"). An expressions matching");
		System.out.println("> \"m[0-9]*\" saves the previous evaluation result to \"memory\". Other expressions");
		System.out.println("> are simply evaluated. Functions are sin, cos, tan, exp, log, abs, sqrt, min,");
		System.out.println("> max and pow, variables are e and pi. An empty line terminates the calculator.");
		
		// read/evaluate expressions
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Object display = 0;
		while (true) {
			System.out.print("< ");
			String line = reader.readLine();
			if (line != null) {
				line = line.trim();
			}
			if (line == null || line.length() == 0) {
				System.out.println("> Good bye.");
				System.exit(0);
			}
			try {
				ValueExpression expr =
					factory.createValueExpression(context, "${" + line + "}", Object.class);
				if (line.matches("m[0-9]*")) { // "save to memory"
					expr.setValue(context, display);
				} else {
					display = expr.getValue(context);
				}
				System.out.println("> " + display);
			} catch (TreeBuilderException e) {
				// demonstrate use of TreeBuilderException and create customized message...
				StringBuilder message = new StringBuilder();
				message.append("Syntax Error at position ");
				// one-based position within line
				message.append(e.getPosition()-1);
				message.append(": encountered ");
				// treat closing brace as end of file...
				message.append(e.getPosition() == e.getExpression().length()-1 ? "<EOF>" : e.getEncountered());
				message.append(", expected ");
				message.append(e.getExpected());
				System.out.println("> " + message);
			} catch (ELException e) {
				System.out.println("> " + e.getMessage());
			}
		}
	}
}
