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

public class ElseCommand extends Command  {

	// Series of commands nested within the 'else' block.
	// The list ends before the 'end' command and may be empty.
	private LinkedList subcommands = new LinkedList();

	// Let k be keyword and p be parent command.
	public ElseCommand(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, ELSE);
		requireEOL(i);
		Command c = nextCommand(i);
		for(;;)  {
			if(c == null) throw new InterpreterException("Else block has no end.", k);
			int y = c.getType();
			if(y == Command.END) break;
			if(y == Command.ELSE) throw new InterpreterException("Else block has no if.", c.getKeyword());
			if(y == Command.ELSEIF) throw new InterpreterException("Elseif block has no if.", c.getKeyword());
			if(c.isDeclaration())
				declarations.add(c);
			subcommands.add(c);
			c = nextCommand(i);
		}
	}

	// Execute all commands in block.
	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		stackPointer = parent.getStackPointer();
		if(r.getWait() != 0) return;
		executeList(subcommands, r);
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  { resolve(subcommands); }

}
