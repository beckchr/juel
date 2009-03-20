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
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;

import de.odysseus.el.misc.LocalMessages;
import de.odysseus.el.misc.MethodInvocation;
import de.odysseus.el.tree.Bindings;

public class AstMethod extends AstInvocation {
	protected final AstNode prefix;

	public AstMethod(AstNode prefix, AstNode name, List<AstNode> nodes, boolean varargs) {
		super(name, nodes, varargs);
		this.prefix = prefix;
	}

	/**
	 * Resolve method
	 * @param context
	 * @param base
	 * @return either a <code>java.lang.reflect.Method</code> or a <code>javax.el.MethodInfo</code>
	 * @throws PropertyNotFoundException
	 */
	protected Method resolveMethod(ELContext context, Object base, final String name) throws MethodNotFoundException {
		Object value = null;
		try {
			value = context.getELResolver().getValue(context, base, new MethodInvocation() {
				public String getName() {
					return name;
				}
				public int getParamCount() {
					return AstMethod.this.getParamCount();
				}
				public boolean isVarArgs() {
					return AstMethod.this.isVarArgs();
				}
				@Override
				public String toString() {
					return getName();
				}
			});
		} catch (PropertyNotFoundException e) {
			throw new MethodNotFoundException(LocalMessages.get("error.property.method.resolve", name, base.getClass()));
		}
		if (!context.isPropertyResolved()) {
			throw new MethodNotFoundException(LocalMessages.get("error.property.method.resolve", name, base.getClass()));
		}
		Method method = null;
		if (value instanceof Method) {
			method = (Method)value;
		} else if (value instanceof MethodInfo) {
			try {
				method = value.getClass().getMethod(name, ((MethodInfo)value).getParamTypes());
			} catch (NoSuchMethodException e) {
				throw new MethodNotFoundException(LocalMessages.get("error.property.method.notfound", name, base.getClass()), e);
			}
		} else {
			throw new MethodNotFoundException(LocalMessages.get("error.property.method.notfound", name, base.getClass()));
		}
		return method;
	}
	
	@Override
	public Object eval(Bindings bindings, ELContext context) {
		Object base = prefix.eval(bindings, context);
		if (base == null) {
			throw new MethodNotFoundException(LocalMessages.get("error.property.base.null", prefix));
		}
		String name = getName(bindings, context);
		if (name == null || name.length() == 0) {
			throw new MethodNotFoundException(LocalMessages.get("error.property.method.notfound", name, base.getClass()));
		}
		Method method = resolveMethod(context, base, name);
		if (varargs && method.isVarArgs()) {
			if (method.getParameterTypes().length > getParamCount() + 1) {
				throw new ELException(LocalMessages.get("error.property.method.invocation", name, base.getClass()));
			}
		} else {
			if (method.getParameterTypes().length != getParamCount()) {
				throw new ELException(LocalMessages.get("error.property.method.invocation", name, base.getClass()));
			}
		}
		try {
			return invoke(bindings, context, base, method);
		} catch (IllegalAccessException e) {
			throw new ELException(LocalMessages.get("error.property.method.access", name, base.getClass()));
		} catch (InvocationTargetException e) {
			throw new ELException(LocalMessages.get("error.property.method.invocation", name, base.getClass()), e.getCause());
		}
	}

	@Override
	public String toString() {
		return ". " + name + "(...)";
	}	

	@Override 
	public void appendStructure(StringBuilder b, Bindings bindings) {
		prefix.appendStructure(b, bindings);
		b.append(".");
		b.append(name);
		b.append("(");
		if (nodes != null && nodes.size() > 0) {
			nodes.get(0).appendStructure(b, bindings);
			for (int i = 1; i < nodes.size(); i++) {
				b.append(", ");
				nodes.get(i).appendStructure(b, bindings);
			}
		}
		b.append(")");
	}

	public int getCardinality() {
		return 1 + getParamCount();
	}

	public AstNode getChild(int i) {
		return i == 0 ? prefix : getParam(i-1);
	}
}
