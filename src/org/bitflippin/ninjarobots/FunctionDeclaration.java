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

public class FunctionDeclaration extends Declaration  {

	// All elements of type string.
	// Populated by constructor; used to find parm declarations.
	private LinkedList parameterNames = new LinkedList();

	// Declarations of parameters in routine body.
	public Declaration getParameter(int i)  { return parameter[i]; }
	private Declaration parameter[];

	// Find number of formal parameters.
	// Used by Call to ensure parameter numbers match.
	public int parameterCount()  { return parameterNames.size(); }

	// Initial value for result.
	private Expression initialValue;

	// Series of commands that form the program.
	private LinkedList subcommands = new LinkedList();

	// Verify header syntax and build parameter list.
	// Let k be keyword and p be parent command.
	public FunctionDeclaration(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, FUNCTION);
		Token t = next(i, "Identifier expected, but got 'EOL'.");
		if(t.getType() != Token.WORD) throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
		name = t.getContent();
		readNames(i);
		t = next(i, "Assignment operator ':' expected, but got 'EOL'.");
		if(t.getType() != Token.ASSIGNMENT)
			throw new InterpreterException("Assignment operator ':' expected, but got '" + t.getContent() + "'.", t);
		initialValue = new Expression(i, false);
		requireEOL(i);
		checkDoubles();
		readBlock(i);
		resolveParameters();
		Declaration d = parent.localSearch(name);
		if(d != null)
			throw new InterpreterException("Symbol with name '" + name + "' already exists in this block.", getKeyword());
		resolved = true;
	}

	// Resolve parameter declarations.
	// Done by constructor so calls can do type checking.
	private void resolveParameters() throws InterpreterException  {
		int s = parameterNames.size();
		for(int i = 0; i < s; i++)  {
			if(parameter[i] == null)
				throw new InterpreterException("Cannot find definition for parameter '" + parameterNames.get(i) + "'.", getKeyword());
			parameter[i].resolve();
		}
	}

	// Read parameter name list into parameterNames.
	// Called from constructor.
	private void readNames(ListIterator i) throws InterpreterException  {
		Token t = next(i, "Open parenthesis expected, but got 'EOL'.");
		if(t.getType() != Token.OPEN_PAREN)
			throw new InterpreterException("Open parenthesis expected, but got '" + t.getContent() + "'.", t);
		t = next(i, "Identifier or closing parenthesis expected, but got 'EOL'.");
		if(t.getType() == Token.WORD)
			for(;;)  {
				if(t.getType() != Token.WORD)
					throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
				parameterNames.add(t.getContent());
				t = next(i, "Comma or closing parenthesis expected, but got 'EOL'.");
				if(t.getType() == Token.CLOSE_PAREN)
					break;
				if(t.getType() != Token.COMMA)
					throw new InterpreterException("Comma or closing parenthesis expected, but got '" + t.getContent() + "'.", t);
				t = next(i, "Identifier expected, but got 'EOL'.");
			}
		else if(t.getType() != Token.CLOSE_PAREN)
			throw new InterpreterException("Identifier or closing parenthesis expected, but got '" + t.getContent() + "'.", t);
	}

	// Read function body.
	// Ensure that parameters are linked to their declarations.
	private void readBlock(ListIterator i) throws InterpreterException  {
		Command c = nextCommand(i);
		int s = parameterNames.size();
		parameter = new Declaration[s];
		for(;;)  {
			if(c == null)
				throw new InterpreterException("Function block has no end.", getKeyword());
			int y = c.getType();
			if(y == Command.END)
				break;
			if(y == Command.ELSE)
				throw new InterpreterException("Else block has no if.", c.getKeyword());
			if(y == Command.ELSEIF)
				throw new InterpreterException("Elseif block has no if.", c.getKeyword());
			int m = -1;
			if(c.isDeclaration())  {
				Declaration d = (Declaration)(c);
				m = match(d.getName());
				if(m != -1)  {
					if(d.getType() != Command.VARIABLE && d.getType() != Command.ARRAY)
						throw new InterpreterException("Parameter '" + d.getName() + "' must be a variable or array.", d.getKeyword());
					parameter[m] = d;
				}
				declarations.add(d);
			}
			if(m == -1)
				subcommands.add(c);
			c = nextCommand(i);
		}
	}

	// Find number (0 is first) of parameter called s.
	// Returns -1 if not found.
	private int match(String s)  {
		ListIterator i = parameterNames.listIterator(0);
		while(i.hasNext())  {
			String p = (String)(i.next());
			if(p.equals(s))
				return i.previousIndex();
		}
		return -1;
	}

	// Ensure no duplicate parameter names.
	private void checkDoubles() throws InterpreterException  {
		ListIterator i1 = parameterNames.listIterator();
		while(i1.hasNext())  {
			String s1 = (String)(i1.next());
			ListIterator i2 = parameterNames.listIterator();
			while(i2.hasNext())  {
				String s2 = (String)(i2.next());
				if(s1 != s2 && s1.equals(s2))
					throw new InterpreterException("Duplicate parameter name '" + s1 + "'.", getKeyword());
			}
		}
	}

	// Write ourself to memory bank b at address a.
	// Defined by contract with Declaration.
	protected int initialize(Robot r, int a) throws ExecutionException  {
		r.getMemory().write(a, initialValue.value(r));
		return a + 1;
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  {
		if(initialValue != null)  {
			initialValue.resolve(this);
			if(!initialValue.isConstant())
				throw new InterpreterException("Initial function value must be constant.", getKeyword());
		}
		resolve(subcommands);
	}

	// Execute function.
	// Used instead of normal execute because of parm passing.
	// Let a be actual parms from Call (of type Expression).
	// Let s be current top of runtime stack.
	public int execute(Robot r, LinkedList a, int s) throws ExecutionException  {
		stackPointer = s;
		address = s;
		int k = initialize(r, address);
		raiseStackPointer(1);
		ListIterator i = a.listIterator(0);
		while(i.hasNext())  {
			Expression e = (Expression)(i.next());
			Declaration d = parameter[i.previousIndex()];
			d.execute(r);
			if(e.isNumeric())
				r.getMemory().write(d.getAddress(), e.value(r));
			else
				k = e.getResolution().initialize(r, k);
		}
		executeList(subcommands, r);
		return r.getMemory().read(address);
	}

}
