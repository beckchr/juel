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
package de.odysseus.el.tree.impl.ast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELException;

import de.odysseus.el.misc.LocalMessages;
import de.odysseus.el.tree.Bindings;
import de.odysseus.el.tree.FunctionNode;

public class AstFunction extends AstInvocation implements FunctionNode {
	private final int index;

	public AstFunction(String name, int index, List<AstNode> nodes) {
		this(name, index, nodes, false);
	}

	public AstFunction(String name, int index, List<AstNode> nodes, boolean varargs) {
		super(name, nodes, varargs);
		this.index = index;
	}

	@Override 
	public Object eval(Bindings bindings, ELContext context) {
		Method method = bindings.getFunction(index);
		try {
			return invoke(bindings, context, null, method);
		} catch (IllegalAccessException e) {
			throw new ELException(LocalMessages.get("error.function.access", name), e);
		} catch (InvocationTargetException e) {
			throw new ELException(LocalMessages.get("error.function.invocation", name), e.getCause());
		}
	}

	@Override
	public String toString() {
		return name + "(...)";
	}	

	@Override 
	public void appendStructure(StringBuilder b, Bindings bindings) {
		b.append(bindings != null && bindings.isFunctionBound(index) ? "<fn>" : name);
		b.append("(");
		if (getCardinality() > 0) {
			nodes.get(0).appendStructure(b, bindings);
			for (int i = 1; i < getCardinality(); i++) {
				b.append(", ");
				nodes.get(i).appendStructure(b, bindings);
			}
		}
		b.append(")");
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public int getCardinality() {
		return getParamCount();
	}

	public AstNode getChild(int i) {
		return getParam(i);
	}
}
