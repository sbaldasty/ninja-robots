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

// Expressions of the form [operand1][operator][operand2].
// Operands 1 and 2 must be numbers.

public class BinaryExpression extends AbstractExpression  {

	// Operands, initialized by constructor.
	private AbstractExpression operand1;
	private AbstractExpression operand2;

	// Is the expression derived exclusively from constants?
	// Defined by contract with AbstractExpression.
	public boolean isConstant()  { return operand1.isConstant() && operand2.isConstant(); }

	// Will expression evaluate to a number?
	// Defined by contract with AbstractExpression.
	public boolean isNumeric()  { return true; }

	// Binary operator, initialized by constructor.
	private char operator;

	// Let c be operator, e1 and e2 be operands.
	// Store these parameters.
	public BinaryExpression(AbstractExpression e1, AbstractExpression e2, char c)  {
		operand1 = e1;
		operand2 = e2;
		operator = c;
	}

	// Evaluate this expression.
	// Defined by contract with AbstractExpression.
	public int value(Robot r) throws ExecutionException  {
		int v1 = operand1.value(r);
		int v2 = operand2.value(r);
		int p = 0;
		switch(operator)  {
			case '\'': return query(r, v1, v2);
			case '*':  return 255 & (v1 * v2);
			case '/':
				if(v2 == 0) throw new ExecutionException("division by zero");
				return v1 / v2;
			case '%':
				if(v2 == 0) throw new ExecutionException("modulus on zero");
				return v1 % v2;
			case '+':
				p = v1 + v2;
				return (p <= 255) ? p : (p - 256);
			case '-':
				p = v1 - v2;
				return (p >= 0) ? p : (p + 256);
			case '&':  return v1 & v2;
			case '|':  return v1 | v2;
			case '^':  return v1 ^ v2;
			case '<':  return (v1 < v2) ? 255 : 0;
			case '>':  return (v1 > v2) ? 255 : 0;
			case '=':  return (v1 == v2) ? 255 : 0;
		}
		return -1;   // Never gets here.
	}

	// Perform neighbor query with apostrophe operator.
	private static int query(Robot r, int o1, int o2) throws ExecutionException  {
		Cell c = null;
		switch(o1)  {
			case BaseConstants.HERE:  c = r.getCell();                      break;
			case BaseConstants.NORTH: c = r.getCell().neighbor(Cell.NORTH); break;
			case BaseConstants.EAST:  c = r.getCell().neighbor(Cell.EAST);  break;
			case BaseConstants.SOUTH: c = r.getCell().neighbor(Cell.SOUTH); break;
			case BaseConstants.WEST:  c = r.getCell().neighbor(Cell.WEST);  break;
			default: throw new ExecutionException("invalid direction for query (" + o1 + ").");
		}
		switch(o2)  {
			case BaseConstants.OCCUPANT: return c.queryOccupant();
			case BaseConstants.TEAM:     return c.queryTeam();
			case BaseConstants.HEALTH:   return c.queryHealth();
			case BaseConstants.WORD:     return c.queryWord();
			default: throw new ExecutionException("invalid query (" + o2 + ").");
		}
	}

	// Defined by contract with Expression.
	public void resolve(Command c) throws InterpreterException  {
		operand1.resolve(c);
		operand2.resolve(c);
		if(!operand1.isNumeric() || !operand2.isNumeric())
			throw new InterpreterException("Operator applied to non-numeric expression.", c.getKeyword());
	}

}
