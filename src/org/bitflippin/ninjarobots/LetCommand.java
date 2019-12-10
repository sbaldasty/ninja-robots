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

public class LetCommand extends Command  {

	// Variable that receives new value (must be writable).
	private Expression variable;

	// Value to assign variable (must be readable).
	private Expression newValue;

	// Verify syntax and assign known parameters.
	// Let k be keyword and p be parent command.
	public LetCommand(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, LET);
		variable = new Expression(i, false);
		Token t = next(i, "Assignment operator ':' expected, but got 'EOL'.");
		if(t.getType() != Token.ASSIGNMENT) throw new InterpreterException("Assignment operator ':' expected, but got '" + t.getContent() + "'.", t);
		newValue = new Expression(i, false);
		requireEOL(i);
	}

	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		if(r.getWait() != 0) return;
		stackPointer = parent.getStackPointer();
		try  {
			int a = variable.symbol(null).address(r);
			r.getMemory().write(a, newValue.value(r));
		}
		catch(InterpreterException e)  {
			System.out.println("Internal error 0004.");
			System.exit(0);
		}
		incrementCommandCount(r);
	}

	// Ensure variable writable and expression numeric.
	// Redefined from Command.
	public void resolve() throws InterpreterException  {
		variable.resolve(this);
		if(!variable.isNumeric() || variable.isConstant())
			throw new InterpreterException("Variable cannot be written.", getKeyword());
		newValue.resolve(this);
		if(!newValue.isNumeric())
			throw new InterpreterException("Expression does not resolve to a number.", getKeyword());
	}

}
