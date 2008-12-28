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

import javax.el.ELContext;

import de.odysseus.el.misc.BooleanOperations;
import de.odysseus.el.misc.NumberOperations;
import de.odysseus.el.misc.TypeConverter;
import de.odysseus.el.tree.Bindings;

public class AstBinary extends AstRightValue {
	public interface Operator {
		public Object apply(TypeConverter converter, Object o1, Object o2);		
	}
	public static final Operator ADD = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return NumberOperations.add(converter, o1, o2); }
		@Override public String toString() { return "+"; }
	};
	public static final Operator AND = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.and(converter, o1, o2); }
		@Override public String toString() { return "&&"; }
	};
	public static final Operator DIV = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return NumberOperations.div(converter, o1, o2); }
		@Override public String toString() { return "/"; }
	};
	public static final Operator EQ = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.eq(converter, o1, o2); }
		@Override public String toString() { return "=="; }
	};
	public static final Operator GE = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.ge(converter, o1, o2); }
		@Override public String toString() { return ">="; }
	};
	public static final Operator GT = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.gt(converter, o1, o2); }
		@Override public String toString() { return ">"; }
	};
	public static final Operator LE = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.le(converter, o1, o2); }
		@Override public String toString() { return "<="; }
	};
	public static final Operator LT = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.lt(converter, o1, o2); }
		@Override public String toString() { return "<"; }
	};
	public static final Operator MOD = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return NumberOperations.mod(converter, o1, o2); }
		@Override public String toString() { return "%"; }
	};
	public static final Operator MUL = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return NumberOperations.mul(converter, o1, o2); }
		@Override public String toString() { return "*"; }
	};
	public static final Operator NE = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.ne(converter, o1, o2); }
		@Override public String toString() { return "!="; }
	};
	public static final Operator OR = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return BooleanOperations.or(converter, o1, o2); }
		@Override public String toString() { return "||"; }
	};
	public static final Operator SUB = new Operator() {
		public Object apply(TypeConverter converter, Object o1, Object o2) { return NumberOperations.sub(converter, o1, o2); }
		@Override public String toString() { return "-"; }
	};

	private final Operator operator;
	private final AstNode left, right;

	public AstBinary(AstNode left, AstNode right, Operator operator) {
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	public Operator getOperator() {
		return operator;
	}

	@Override 
	public Object eval(Bindings bindings, ELContext context) {
		return operator.apply(bindings, left.eval(bindings, context), right.eval(bindings, context));
	}

	@Override
	public String toString() {
		return "'" + operator.toString() + "'";
	}	

	@Override 
	public void appendStructure(StringBuilder b, Bindings bindings) {
		left.appendStructure(b, bindings);
		b.append(' ');
		b.append(operator);
		b.append(' ');
		right.appendStructure(b, bindings);
	}

	public int getCardinality() {
		return 2;
	}

	public AstNode getChild(int i) {
		return i == 0 ? left : i == 1 ? right : null;
	}
}
