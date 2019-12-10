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

// Expressions that evaluate to a number.

abstract public class AbstractExpression  {

	// Expression prefaced with not operator.
	// Value should be processed as normal, then inverted.
	protected boolean negated;

	// Is the expression derived exclusively from constants?
	abstract public boolean isConstant();

	// Will expression evaluate to a number?
	// That is, is it legitimate to call value.
	abstract public boolean isNumeric();

	// Evaluate this expression.
	abstract public int value(Robot r) throws ExecutionException;

	// Resolve expression with respect to c.
	// That is, symbol search should start with c and go back.
	abstract public void resolve(Command c) throws InterpreterException;

	// Create singular expression from token list i.
	// Singular expressions are of any subclass but Expression.
	// Next token will be after singular expression.
	// Let n be the expression is negated.
	protected static AbstractExpression make(ListIterator i, boolean n) throws InterpreterException  {
		Token t = next(i, "Expression expected, but got EOL.");
		int y = t.getType();
		if(y == Token.OPEN_PAREN)  {
			AbstractExpression e = new Expression(i, n);
			Token p = next(i, "Closing parenthesis expected, but got 'EOL'.");
			if(p.getType() != Token.CLOSE_PAREN) throw new InterpreterException("Closing parenthesis expected, but got '" + p.getContent() + "'.", p);
			return e;
		}
		else if(y == Token.NUMBER)
			return new NumericLiteral(t, n);
		else if(y == Token.NOT_OPERATOR)
			return make(i, !n);
		else if(y == Token.WORD)  {
			if(i.hasNext() && ((Token)(i.next())).getType() == Token.OPEN_PAREN)
				return new Call(i, n, t.getContent());
			else  {
				i.previous();
				i.previous();
				return new Symbol(i, n);
			}
		}
		throw new InterpreterException("Expression expected, but got '" + t.getContent() + "'.", t);
	}

	// Get the next token from token list i.
	// If no next token, report error s and throw exception.
	protected static Token next(ListIterator i, String s) throws InterpreterException  {
		if(!i.hasNext()) throw new InterpreterException(s, null);
		return (Token)(i.next());
	}

	// Negation of n in 8 bits.
	protected static int negation(int n)  { return 255 - n; }

}
