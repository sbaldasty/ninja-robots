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

public class InvokeCommand extends Command  {

	private Call call;

	// Verify syntax and assign known parameters.
	// Let k be keyword and p be parent command.
	public InvokeCommand(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, INVOKE);
		Token t = next(i, "Function name expected, but got 'EOL'.");
		if(t.getType() != Token.WORD) throw new InterpreterException("Function name expected, but got '" + t.getContent() + "'.", t);
		next(i, "Open parenthesis expected, but got 'EOL'.");
		call = new Call(i, false, t.getContent());
		requireEOL(i);
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  { call.resolve(this); }

	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		stackPointer = parent.getStackPointer();
		if(r.getWait() != 0) return;
		call.value(r);
		incrementCommandCount(r);
	}

}
