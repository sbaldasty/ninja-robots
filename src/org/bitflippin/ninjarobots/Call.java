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

public class Call extends Identifier  {

	// All elements are of type AbstractExpression.
	private LinkedList actuals = new LinkedList();

	// Command that initiates this call.
	// Needed for knowing how high is the runtime stack.
	private Command command;

	// Let i be all tokens; read off list from i.
	// Assume i starts with first expression.
	// Ensure i is next token after closing paren when done.
	// Let n be expression is negated; let s be name of function.
	public Call(ListIterator i, boolean n, String s) throws InterpreterException  {
		negated = n;
		name = s;
		Token t = next(i, "Expression or closing parenthesis expected, but got 'EOL'.");
		while(t.getType() != Token.CLOSE_PAREN)  {
			t = (Token)(i.previous());
			actuals.add(new Expression(i, false));
			t = next(i, "Comma or closing parenthesis expected, but got 'EOL'.");
			if(t.getType() == Token.CLOSE_PAREN)
				break;
			if(t.getType() != Token.COMMA)
				throw new InterpreterException("Comma or closing parenthesis expected, but got'" + t.getContent() + "'.", t);
			t = next(i, "Expression expected, but got '" + t.getContent() + "'.");
		}
	}

	// Is the expression derived exclusively from constants?
	// Defined by contract with AbstractExpression.
	public boolean isConstant()  { return false; }

	// Will expression evaluate to a number?
	// Defined by contract with AbstractExpression.
	public boolean isNumeric()  { return true; }

	// Evaluate this expression.
	// Defined by contract with AbstractExpression.
	public int value(Robot r) throws ExecutionException  {
		int sp = command.getStackPointer();
		sp = Math.max(MemoryBank.PERMANENT_SIZE, sp);
		FunctionDeclaration f = (FunctionDeclaration)(resolution);
		return f.execute(r, actuals, sp);
	}

	// Resolve expression with respect to c.
	// Defined by contract with AbstractExpression.
	public void resolve(Command c) throws InterpreterException  {
		command = c;
		resolution = c.globalSearch(name);
		if(resolution == null)
			throw new InterpreterException("Undefined symbol '" + name +"'.", c.getKeyword());
		if(resolution.getType() != Command.FUNCTION)
			throw new InterpreterException("Symbol '" + name + "' does not resolve to a function.", c.getKeyword());
		FunctionDeclaration f = (FunctionDeclaration)(resolution);
		if(f.parameterCount() != actuals.size())
			throw new InterpreterException("Incorrect number of parameters.", c.getKeyword());
		ListIterator i = actuals.listIterator();
		while(i.hasNext())  {
			Expression e = (Expression)(i.next());
			e.resolve(c);
			int p = i.previousIndex();
			Declaration d1 = f.getParameter(p);
			Declaration d2 = e.getResolution();
			if(d1.getTypeDeclaration() != d2.getTypeDeclaration())
				throw new InterpreterException("Parameter " + p + " has the wrong type.", c.getKeyword());
		}
	}

}
