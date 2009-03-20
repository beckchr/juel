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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.el.ELContext;

import de.odysseus.el.tree.Bindings;

public abstract class AstInvocation extends AstRightValue {
	protected final List<AstNode> nodes;
	protected final boolean varargs;
	protected final AstNode name;

	public AstInvocation(AstNode name, List<AstNode> nodes, boolean varargs) {
		this.name = name;
		this.nodes = nodes;
		this.varargs = varargs;
	}

	/**
	 * Invoke method.
	 * @param bindings
	 * @param context
	 * @param base
	 * @param method
	 * @return method result
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected Object invoke(Bindings bindings, ELContext context, Object base, Method method)
		throws InvocationTargetException, IllegalAccessException {
		Class<?>[] types = method.getParameterTypes();
		Object[] params = null;
		if (types.length > 0) {
			params = new Object[types.length];
			if (varargs && method.isVarArgs()) {
				for (int i = 0; i < params.length - 1; i++) {
					Object param = getParam(i).eval(bindings, context);
					if (param != null || types[i].isPrimitive()) {
						params[i] = bindings.convert(param, types[i]);
					}
				}
				int varargIndex = types.length-1;
				Class<?> varargType = types[varargIndex].getComponentType();
				int length = getParamCount() - varargIndex;
				Object array = null;
				if (length == 1) { // special: eventually use argument as is
					Object param = getParam(varargIndex).eval(bindings, context);
					if (types[varargIndex].isInstance(param)) {
						array = param;
					} else {
						array = Array.newInstance(varargType, 1);
						if (param != null || varargType.isPrimitive()) {
							Array.set(array, 0, bindings.convert(param, varargType));
						}
					}
				} else {
					array = Array.newInstance(varargType, length);
					for (int i = 0; i < length; i++) {
						Object param = getParam(varargIndex + i).eval(bindings, context);
						if (param != null || varargType.isPrimitive()) {
							Array.set(array, i, bindings.convert(param, varargType));
						}
					}
				}
				params[varargIndex] = array;
			} else {
				for (int i = 0; i < params.length; i++) {
					Object param = getParam(i).eval(bindings, context);
					if (param != null || types[i].isPrimitive()) {
						params[i] = bindings.convert(param, types[i]);
					}
				}
			}
		}
		return method.invoke(base, params);
	}
	
	protected String getName(Bindings bindings, ELContext context) {
		return (String)name.getValue(bindings, context, String.class);
	}
	
	public int getParamCount() {
		return nodes == null ? 0 : nodes.size();
	}

	protected AstNode getParam(int i) {
		return nodes == null ? null : nodes.get(i);
	}	

	public boolean isVarArgs() {
		return varargs;
	}
}
