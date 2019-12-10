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

// Expressions that consist of a single numerical constant.
// Numeric literals n must be such that 0 <= n <= 255.

public class NumericLiteral extends AbstractExpression  {

	// Is the expression derived exclusively from constants?
	// Defined by contract with AbstractExpression.
	public boolean isConstant()  { return true; }

	// Will expression evaluate to a number?
	// Defined by contract with AbstractExpression.
	public boolean isNumeric()  { return true; }

	// Evaluate this expression.
	// Defined by contract with AbstractExpression.
	public int value(Robot r) throws ExecutionException  { return number; }

	// Number that is returned upon evaluation.
	private int number;

	// Create numeric literal from token t.
	// Require t be of type number.
	// Let n be the expression is negated.
	public NumericLiteral(Token t, boolean n) throws InterpreterException  {
		String s = t.getContent();
		number = Integer.parseInt(s);
		if(number > 255) throw new InterpreterException("Numeric literal must be less than 256 (token '" + s + "').", t);
		if(n) number = negation(number);
	}

	// Defined by contract with Expression.
	public void resolve(Command c) throws InterpreterException  { }

}
