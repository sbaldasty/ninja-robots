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

public class ConstantDeclaration extends Declaration  {

	// What constant should equal.
	private AbstractExpression expression;

	// Evaluated form of expression.
	public int getNumber() throws ExecutionException  { return (number == -1) ? expression.value(null) : number; }
	private int number;

	// Verify syntax and assign known parameters.
	// Let k be keyword and p be parent command.
	public ConstantDeclaration(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, CONSTANT);
		number = -1;
		Token t = next(i, "Identifier expected, but got 'EOL'.");
		if(t.getType() != Token.WORD) throw new InterpreterException("Identifier expected, but got '" + t.getContent() + "'.", t);
		name = t.getContent();
		t = next(i, "Assignment operator ':' expected, but got 'EOL'.");
		if(t.getType() != Token.ASSIGNMENT) throw new InterpreterException("Assignment operator ':' expected, but got '" + t.getContent() + "'.", t);
		expression = new Expression(i, false);
		resolve();
		requireEOL(i);
	}

	// Used by Program to generate built-in constants.
	// Let n be name and v be value.
	public ConstantDeclaration(String n, int v)  {
		super(null, null, CONSTANT);
		name = n;
		number = v;
		resolved = true;
	}

	// Resolve expression and make sure it is constant.
	// Redefined from Command (but resolved from this constructor).
	public void resolve() throws InterpreterException  {
		if(resolved) return;
		Declaration d = parent.localSearch(name);
		if(d != null) throw new InterpreterException("Symbol with name '" + name + "' already exists in this block.", getKeyword());
		expression.resolve(this);
		if(!expression.isConstant()) throw new InterpreterException("Expression does not resolve to a constant.", getKeyword());
		resolved = true;
	}

	// Write ourself to memory bank b at address a.
	// Returns next address to write, since initialization may be unfinished.
	// Defined by contract with Declaration.
	protected int initialize(Robot r, int a) throws ExecutionException  { return a; }

}
