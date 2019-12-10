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

public class ElseifCommand extends Command  {

	// What should evaluate to true (nonzero) or false (zero).
	private Expression expression;

	// Series of commands nested within the 'if' block.
	// The list ends before the 'end' or 'else' command.
	private LinkedList ifCommands = new LinkedList();

	// Else of elseif command; may be void.
	private Command elseCommand;

	// Verify syntax and assign known parameters.
	// Let k be keyword and p be parent command.
	public ElseifCommand(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, ELSEIF);
		expression = new Expression(i, false);
		requireEOL(i);
		Command c = nextCommand(i);
		int y = 0;
		for(;;)  {
			if(c == null) throw new InterpreterException("Elseif block has no end.", k);
			y = c.getType();
			if(y == Command.END || y == Command.ELSE || y == Command.ELSEIF) break;
			if(c.isDeclaration())
				declarations.add(c);
			ifCommands.add(c);
			c = nextCommand(i);
		}
		if(y == Command.ELSEIF || y == Command.ELSE)
			elseCommand = c;
	}

	// Execute if commands or else command.
	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		stackPointer = parent.getStackPointer();
		if(r.getWait() != 0) return;
		if(expression.value(r) != BaseConstants.FALSE)
			executeList(ifCommands, r);
		else if(elseCommand != null)  {
			elseCommand.execute(r);
			incrementCommandCount(r);
		}
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  {
		expression.resolve(this);
		if(!expression.isNumeric()) throw new InterpreterException("Expression does not resolve to a number.", getKeyword());
		resolve(ifCommands);
		if(elseCommand != null)
			elseCommand.resolve();
	}

}
