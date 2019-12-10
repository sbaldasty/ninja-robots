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


abstract public class Declaration extends Command  {

	// Identifier declared on this line.
	public String getName()  { return name; }
	protected String name;

	// Name of any compound type of which this identifier is instance.
	// If null, identifier has normal number type.
	public String getTypeName()  { return typeName; }
	protected String typeName;

	// Declaration of any compound type.
	// Derived by global search with typeName.
	public TypeDeclaration getTypeDeclaration()  { return typeDeclaration; }
	protected TypeDeclaration typeDeclaration;

	// If belongs in permanent memory.
	// Address will not be resolved upon execution.
	// Used only for arrays and variables.
	protected boolean permanent;

	// Index of memory bank where symbol starts.
	// Defined on resolution if permanent, otherwise execution.
	public int getAddress()  { return address; }
	protected int address;

	// Has declaration been resolved yet?
	public boolean getResolved()  { return resolved; }
	protected boolean resolved;

	// Carryover of command constructor.
	protected Declaration(Token k, Command p, int y)  {
		super(k, p, y);
		if(p == null)
			permanent = false; // Built-in constant, and does not matter.
		else
			permanent = (p.getType() == PROGRAM);
		resolved = false;
	}

	// Whether this command is declaration.
	// Redefined from Command.
	public boolean isDeclaration()  { return true; }

	// Write ourself to the memory bank.
	// Returns next address to write, since initialization may be unfinished.
	abstract protected int initialize(Robot r, int addr) throws ExecutionException;

}
