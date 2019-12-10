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

public class VariableDeclaration extends Declaration  {

	// Initial value for variable.
	private Expression expression;

	// Verify syntax and assign known parameters.
	// Let k be keyword and p be parent command.
	public VariableDeclaration(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, VARIABLE);
		Token t = next(i, "Identifier expected, but got 'EOL'.");
		if(t.getType() != Token.WORD)
			throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
		name = t.getContent();
		t = next(i, "Assignment operator ':' or keyword 'type' expected, but got 'EOL'.");
		int y = t.getType();
		if(y == Token.KEY_TYPE)  {
			t = next(i, "Identifier expected, but got 'EOL'.");
			if(t.getType() != Token.WORD)
				throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
			typeName = t.getContent();
		}
		else if(y == Token.ASSIGNMENT)
			expression = new Expression(i, false);
		else
			throw new InterpreterException("Assignment operator ':' or keyword 'type' expected, but got '" + t.getContent() + "'.", t);
		requireEOL(i);
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  {
		Declaration d = parent.localSearch(name);
		if(d != null) throw new InterpreterException("Symbol with name '" + name + "' already exists in this block.", getKeyword());
		if(expression != null)  {
			expression.resolve(this);
			if(parent.getType() == Command.TYPE)  {
				if(!expression.isConstant())
					throw new InterpreterException("Initial value must be constant in type block.", getKeyword());
				address = parent.getStackPointer();
				parent.raiseStackPointer(1);
			}
			else if(permanent)  {
				if(!expression.isConstant())
					throw new InterpreterException("Initial value must be constant in global scope.", getKeyword());
				address = MemoryBank.getUsed();
				MemoryBank.reserve(1);
			}
			if(!expression.isNumeric())
				throw new InterpreterException("Initial value does not resolve to a number.", getKeyword());
		}
		else  {
			d = globalSearch(typeName);
			if(d == null || d.getType() != Command.TYPE)
				throw new InterpreterException("Undefined type name.", getKeyword());
			typeDeclaration = (TypeDeclaration)(d);
			if(parent.getType() == Command.TYPE)  {
				address = parent.getStackPointer();
				parent.raiseStackPointer(typeDeclaration.getStackPointer());
			}
			else if(permanent)  {
				address = MemoryBank.getUsed();
				MemoryBank.reserve(typeDeclaration.getStackPointer());
			}
		}
		resolved = true;
	}

	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		if(!permanent)  {
			address = parent.getStackPointer();
			parent.raiseStackPointer((typeDeclaration == null) ? 1 : typeDeclaration.getStackPointer());
		}
		else if(r.getMemory().read(address) != -1)
			return;
		initialize(r, address);
	}

	// Write ourself to memory bank b at address a.
	// Returns next address to write, since initialization may be unfinished.
	// Defined by contract with Declaration.
	protected int initialize(Robot r, int a) throws ExecutionException  {
		if(expression == null)
			return typeDeclaration.initialize(r, a);
		else
			r.getMemory().write(a, expression.value(r));
		return a + 1;
	}

}
