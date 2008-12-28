package de.odysseus.el.util;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import de.odysseus.el.misc.MethodInvocation;

/**
 * Public method resolver.
 * 
 * For a method invocation of method <code>name</code> on object <code>base</code> with
 * <code>n</code> arguments, this resolver will match a public method <code>m</code>
 * declared in <code>base</code>' class or any of it's superclasses such that
 * <ul>
 * <li><code>m</code> has name <code>name</code></li>
 * <li><code>m</code>'s return type is not <code>void</code></li>
 * <li><code>m</code> takes <code>n</code> parameters or <code>m</code> is a vararg-method
 * and varargs are enabled and <code>m</code> takes at least <code>n+1</code> parameters</li>.
 * </ul>
 * The current implementation does no caching. A future version may do this for better
 * performance. However, feel free to implement your own, optimized method resolver.
 * 
 * Note: This resolver is for use with <em>JUEL</em>'s method invocation feature. Method
 * invocations must be explicitly enabled. See to the documentation for further details.
 */
public class PublicMethodResolver extends ELResolver {
	private boolean match(MethodInvocation call, Method method) {
		if (method.getName().equals(call.getName()) && method.getReturnType() != void.class) {
			if (call.getParamCount() == method.getParameterTypes().length) {
				return true;
			}
			if (method.isVarArgs() && call.isVarArgs()) {
				if (call.getParamCount() >= method.getParameterTypes().length - 1) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public Method getValue(ELContext context, Object base, Object prop) {
		if (base != null && prop instanceof MethodInvocation) {
			MethodInvocation call = (MethodInvocation)prop;
			for (Method method : base.getClass().getMethods()) {
				if (match(call, method)) {
					context.setPropertyResolved(true);
					return method;
				}
			}
		}
		return null;
	}

	@Override
	public void setValue(ELContext context, Object base, Object property, Object value) {
		throw new PropertyNotWritableException();
	}

	@Override
	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return MethodInvocation.class;
	}

	@Override
	public boolean isReadOnly(ELContext context, Object base, Object property) {
		return true;
	}

	@Override
	public Class<?> getType(ELContext context, Object base, Object property) {
		return null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}
}
