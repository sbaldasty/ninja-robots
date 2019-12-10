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

import java.util.LinkedList;
import java.util.ListIterator;

// Expressions of the most general type.
// Formed by connecting singular expressions and binary operators in series.

public class Expression extends AbstractExpression  {

	// Expressions that form this expression.
	// For instance, x and y are subexpressions of x * y.
	protected LinkedList subexpressions = new LinkedList();

	// Single expression list of subexpressions reduces to.
	private AbstractExpression reduction;

	// Evaluate this expression.
	// Defined by contract with AbstractExpression.
	public int value(Robot r) throws ExecutionException  {
		int v = reduction.value(r);
		return negated ? negation(v) : v;
	}

	// For parameter passing.
	public Declaration getResolution()  {
		Identifier i = (Identifier)(reduction);
		return i.getResolution();
	}

	// Assume expression consists of just 1 symbol and return it.
	// Used for let and proc/fn parms.
	// Let t be token to point out error in.
	public Symbol symbol(Token t) throws InterpreterException  {
		try  { return (Symbol)(reduction); }
		catch(ClassCastException e)  { throw new InterpreterException("Variable or attribute expected, but got expression.", t); }
	}

	// Operators between subexpressions stored as strings.
	private LinkedList operators = new LinkedList();

	// Is the expression derived exclusively from constants?
	// Defined by contract with AbstractExpression.
	public boolean isConstant()  { return reduction.isConstant(); }

	// Will expression evaluate to a number?
	// Defined by contract with AbstractExpression.
	public boolean isNumeric()  { return reduction.isNumeric(); }

	// Create expression from token list i.
	// Next token will be after this expression.
	// Let n be the expression is negated.
	public Expression(ListIterator i, boolean n) throws InterpreterException  {
		negated = n;
		boolean done = false;
		while(!done)  {
			subexpressions.add(make(i, false));
			if(i.hasNext())  {
				Token t = (Token)(i.next());
				if(t.getType() == Token.BINARY_OPERATOR)
					operators.add(t.getContent());
				else  {
					done = true;
					i.previous();
				}
			}
			else
				done = true;
		}
		while(subexpressions.size() > 1)
			reduce();
		reduction = (AbstractExpression)(subexpressions.getFirst());
	}

	// Replace 2 subexpressions with single binary expression.
	private void reduce()  {
		for(int k = 7; k > 0; k--)  {
			ListIterator i1 = operators.listIterator();
			ListIterator i2 = subexpressions.listIterator();
			while(i1.hasNext())  {
				String s = (String)(i1.next());
				char c = s.charAt(0);
				if(precedence(c) == k)  {
					i1.remove();
					AbstractExpression e1 = (AbstractExpression)(i2.next());
					i2.remove();
					AbstractExpression e2 = (AbstractExpression)(i2.next());
					i2.set(new BinaryExpression(e1, e2, c));
					return;
				}
				i2.next();
			}
		}
	}

	// Find the precedence of boolean operator c.
	// Higher values mean greater precedence.
	private static int precedence(char c)  {
		switch(c)  {
			case '\'': return 7;   // Neighbor query.
			case '*':  return 6;   // Multiplication.
			case '/':  return 6;   // Division.
			case '%':  return 6;   // Modulus.
			case '+':  return 5;   // Addition.
			case '-':  return 5;   // Subtraction.
			case '&':  return 2;   // Bitwise AND.
			case '|':  return 1;   // Bitwise OR.
			case '^':  return 3;   // Bitwise XOR.
			case '<':  return 4;   // Less than.
			case '>':  return 4;   // Greater than.
			case '=':  return 4;   // Equal to.
		}
		return -1;   // Never gets here.
	}

	// Ensure numeric operator implies is numeric, resolve subexpression.
	// Defined by contract with Expression.
	public void resolve(Command c) throws InterpreterException  {
		reduction.resolve(c);
		if(negated && !reduction.isNumeric())
			throw new InterpreterException("Operator applied to non-numeric expression.", c.getKeyword());
	}

}
