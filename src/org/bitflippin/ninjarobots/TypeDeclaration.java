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

// Compound user-defined variable templates.
// Stack pointer has special meaning: represents size of type.
// Sub-declarations have resolution-time defined addresses relative to 0.

public class TypeDeclaration extends Declaration  {

	// Verify syntax and assign known parameters.
	// Mark as resolved immediately for instant visibility.
	// Let k be keyword and p be parent command.
	public TypeDeclaration(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, TYPE);
		Token t = next(i, "Identifier expected, but got 'EOL'.");
		if(t.getType() != Token.WORD) throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
		name = t.getContent();
		requireEOL(i);
		Command c = nextCommand(i);
		int y = 0;
		stackPointer = 0;
		for(;;)  {
			if(c == null)
				throw new InterpreterException("Type block has no end.", k);
			y = c.getType();
			if(y == Command.END) break;
			if(y != Command.ARRAY && y != Command.VARIABLE && y != Command.CONSTANT)
				throw new InterpreterException("Type blocks can contain only variable, array, and constant declarations.", c.getKeyword());
			declarations.add(c);
			c = nextCommand(i);
		}
		resolve(declarations);
		resolved = true;
	}

	// Write ourself to memory bank b at address a.
	// Defined by contract with Declaration.
	// Returns next address to write, since initialization may be unfinished.
	protected int initialize(Robot r, int a) throws ExecutionException  {
		ListIterator i = declarations.listIterator();
		while(i.hasNext())  {
			Declaration d = (Declaration)(i.next());
			a = d.initialize(r, a);
		}
		return a;
	}

}
