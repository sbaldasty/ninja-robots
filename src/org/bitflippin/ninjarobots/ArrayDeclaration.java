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

public class ArrayDeclaration extends Declaration  {

	// Elements in new array; must be readable number.
	public Expression getSize()  { return size; }
	private Expression size;

	// Initial value for each element.
	
	private Expression expression;

	// Verify syntax and assign known parameters.
	// Let k be keyword and p be parent command.
	public ArrayDeclaration(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, ARRAY);
		Token t = next(i, "Identifier expected, but got 'EOL'.");
		if(t.getType() != Token.WORD) throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
		name = t.getContent();
		t = next(i, "Keyword 'size' expected, but got 'EOL'.");
		if(t.getType() != Token.KEY_SIZE) throw new InterpreterException("Keyword 'size' expected, but got '" + t.getContent() + "'.", t);
		size = new Expression(i, false);
		t = next(i, "Keyword 'type' or assignment operator ':' expected, but got 'EOL'.");
		int y = t.getType();
		if(y == Token.KEY_TYPE)  {
			t = next(i, "Identifier expected, but got 'EOL'.");
			if(t.getType() != Token.WORD) throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
			typeName = t.getContent();
		}
		else if(y == Token.ASSIGNMENT)
			expression = new Expression(i, false);
		else
			throw new InterpreterException("Keyword 'size' or 'type' expected, but got '" + t.getContent() + "'.", t);
		requireEOL(i);
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  {
		Declaration d = parent.localSearch(name);
		if(d != null) throw new InterpreterException("Symbol with name '" + name + "' already exists in this block.", getKeyword());
		handleSize();
		if(expression != null)
			handleExpression();
		else
			handleType();
		if(parent.getType() != Command.TYPE && permanent)  {
			int s = findSize();
			if(s < 1) throw new InterpreterException("Array must contain at least one element.", getKeyword());
			address = MemoryBank.getUsed();
			MemoryBank.reserve(s);
		}
		resolved = true;
	}

	// Find necessary memory reservation at compile time.
	// Requires array declared in main program or type block.
	private int findSize()  {
		int v = 0;
		try  { v = size.value(null); }
		catch(ExecutionException e)  {
			System.out.println("Internal error 0003.");
			System.exit(0);
		}
		return v;
	}

	// Resolve size parameter.
	// Helps break up resolve method.
	private void handleSize() throws InterpreterException  {
		size.resolve(this);
		if(!size.isConstant())  {
			int y = parent.getType();
			if(y == PROGRAM)
				throw new InterpreterException("Array in global scope must have constant size.", getKeyword());
			else if(y == TYPE)
				throw new InterpreterException("Array in type block must have constant size.", getKeyword());
		}
		if(!size.isNumeric())
			throw new InterpreterException("Size does not resolve to a number.", getKeyword());
	}

	// Resolve initial value expression.
	// Helps break up resolve method.
	private void handleExpression() throws InterpreterException  {
		expression.resolve(this);
		if(!expression.isNumeric())
			throw new InterpreterException("Initial value does not resolve to a number.", getKeyword());
		if(parent.getType() == Command.TYPE)  {
			if(!expression.isConstant())
				throw new InterpreterException("Initial value must be constant in type block.", getKeyword());
			address = parent.getStackPointer();
			parent.raiseStackPointer(findSize());
		}
		if(permanent && !expression.isConstant())
			throw new InterpreterException("Initial value must be constant in global scope.", getKeyword());
	}

	// Resolve type parameter.
	// Helps break up resolve method.
	private void handleType() throws InterpreterException  {
		Declaration d = globalSearch(typeName);
		if(d == null || d.getType() != Command.TYPE)
			throw new InterpreterException("Undefined type name.", getKeyword());
		typeDeclaration = (TypeDeclaration)(d);
		if(parent.getType() == Command.TYPE)  {
			address = parent.getStackPointer();
			parent.raiseStackPointer(findSize());
		}
	}

	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		int s = size.value(r);
		if(!permanent)  {
			address = parent.getStackPointer();
			parent.raiseStackPointer((typeDeclaration == null) ? s : s * typeDeclaration.getStackPointer());
		}
		else if(r.getMemory().read(address) != -1)
			return;
		initialize(r, address);
	}

	// Write ourself to memory bank b at address a.
	// Returns next address to write, since initialization may be unfinished.
	// Defined by contract with Declaration.
	protected int initialize(Robot r, int a) throws ExecutionException  {
		int i;
		int s = size.value(r);
		MemoryBank b = r.getMemory();
		if(expression == null)
			for(i = 0; i < s; i++)
				a = typeDeclaration.initialize(r, a);
		else  {
			int v = expression.value(r);
			for(i = 0; i < s; i++)
				b.write(a++, v);
		}
		return a;
	}

}
