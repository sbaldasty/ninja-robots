/*
 * Copyright (c) 2004, Steven Baldasty <sbaldasty@bitflippin.org>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * Contributors:
 *    Steven Baldasty <sbaldasty@bitflippin.org>
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package org.bitflippin.ninjarobots;

import java.util.ListIterator;

// References to non-function identifiers that appear in expressions.

public class Symbol extends Identifier  {

	// Used only if symbol references array.
	// Index of array read from brackets if brackets exist.
	private Expression index;

	// Used only if symbol references instance of compound type.
	// What comes after the dot operator, if dot operator exists.
	private Symbol attribute;

	// Require next token of i be the identifier.
	// Next token will be after identifier when done.
	// Let n be the expression is negated.
	public Symbol(ListIterator i, boolean n) throws InterpreterException  {
		negated = n;
		Token t = next(i, null);
		name = t.getContent();
		if(i.hasNext())  {
			t = (Token)(i.next());
			if(t.getType() == Token.OPEN_BRACKET)  {
				index = new Expression(i, false);
				t = next(i, "Close bracket expected, but got '" + t.getContent() + "'.");
				if(t.getType() != Token.CLOSE_BRACKET)
					throw new InterpreterException("Close bracket expected, but got '" + t.getContent() + "'.", t);
				if(i.hasNext())
					t = (Token)(i.next());
			}
			if(t.getType() == Token.DOT_OPERATOR)
				attribute = (new Expression(i, false)).symbol(t);
			else
				i.previous();
		}
	}

	// Is the expression derived exclusively from constants?
	// Defined by contract with AbstractExpression.
	public boolean isConstant()  { return resolution.getType() == Command.CONSTANT; }

	// Will expression evaluate to a number?
	// Defined by contract with AbstractExpression.
	public boolean isNumeric()  {
		int y = resolution.getType();
		if(y == Command.CONSTANT)
			return true;   // Constant.
		if(resolution.getTypeName() == null)  {
			if(y == Command.VARIABLE)
				return true;   // Normal variable.
			if(y == Command.ARRAY && index != null)
				return true;   // Numeric array element.
			if(y == Command.FUNCTION)
				return true;   // Function.
		}
		else if(attribute != null)
			return attribute.isNumeric();   // Possibly ends with numeric attribute.
		return false;
	}

	// Get address of resolution identifier wrt r.
	// Assume this is variable or array.
	public int address(Robot r) throws ExecutionException  {
		TypeDeclaration t = resolution.getTypeDeclaration();
		int b = resolution.getAddress();
		int i = (index == null) ? 0 : index.value(r);
		int a = (attribute == null) ? 0 : attribute.address(r);
		int s = (t == null) ? 1 : t.getStackPointer();
		if(index != null)  {
			ArrayDeclaration d = (ArrayDeclaration)(resolution);
			if(d.getSize().value(r) <= i)
				throw new ExecutionException("array index out of bounds");
		}
		return b + s * i + a;
	}

	// Evaluate this expression.
	// Defined by contract with AbstractExpression.
	public int value(Robot r) throws ExecutionException  {
		if(isConstant())  {
			ConstantDeclaration d = (ConstantDeclaration)(resolution);
			return negated ? negation(d.getNumber()) : d.getNumber();
		}
		return r.getMemory().read(address(r));
	}

	// Resolve expression with respect to c.
	// Defined by contract with AbstractExpression.
	public void resolve(Command c) throws InterpreterException  {
		resolution = c.globalSearch(name);
		if(resolution == null)
			throw new InterpreterException("Undefined symbol '" + name +"'.", c.getKeyword());
		if(resolution.getType() == Command.TYPE)
			throw new InterpreterException("Symbol '" + name +"' does not work here.", c.getKeyword());
		if(index != null)  {
			index.resolve(c);
			if(!index.isNumeric())
				throw new InterpreterException("Array index does not resolve to a number.", c.getKeyword());
			if(resolution.getType() != Command.ARRAY)
				throw new InterpreterException("Subscripted symbol is not an array.", c.getKeyword());
		}
		if(attribute != null)
			attribute.resolve(resolution.getTypeDeclaration());
	}

}
