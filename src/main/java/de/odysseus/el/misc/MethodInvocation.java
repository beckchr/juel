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
package de.odysseus.el.misc;

/**
 * Method invocation interface.
 *
 * @author Christoph Beck
 */
public interface MethodInvocation {
	/**
	 * Get the method name
	 */
	public String getName();

	/**
	 * Get the number of arguments in this method invocation
	 */
	public int getParamCount();
	
	/**
	 * @return <code>true</code> if this node supports varargs.
	 */
	public boolean isVarArgs();
}
