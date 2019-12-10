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

// Lines of code that may contain blocks of other commands.

public class Command  {

	// Command type constants.
	public static final int CONSTANT = 0;
	public static final int VARIABLE = 1;
	public static final int TYPE = 2;
	public static final int ARRAY = 3;
	public static final int END = 4;
	public static final int IF = 5;
	public static final int ELSEIF = 6;
	public static final int ELSE = 7;
	public static final int LET = 8;
	public static final int CHOOSE = 9;
	public static final int WHILE = 10;
	public static final int INVOKE = 11;
	public static final int FUNCTION = 12;
	public static final int PROGRAM = 13;

	// Type is command type constant.
	// Set by subclass constructors for each type.
	private int type;
	public int getType()  { return type; }

	// Keyword for this command (first token on line).
	// Used externally for highlighting problematic line.
	private Token keyword;
	public Token getKeyword()  { return keyword; }

	// Any declaration commands in this block.
	protected LinkedList declarations = new LinkedList();

	// Command of which this command is a subcommand.
	// Used for block's symbol table access.
	protected Command parent;

	// Incremented when commands execute.
	// Reset with every new program execution.
	protected static int commandCount;

	// Track origin of symbol space on runtime stack.
	// Initialized on execution from parent stack pointer.
	public int getStackPointer()  { return stackPointer; }
	protected int stackPointer;

	// Increment stack pointer by n numbers.
	// Used by declarations at runtime.
	public void raiseStackPointer(int n)  { stackPointer += n; }

	// Called whenever instruction executes.
	// Kill robot if gone too long.
	protected static void incrementCommandCount(Robot r) throws ExecutionException  {
		if(++commandCount == Program.MAXIMUM_COMMANDS)
			throw new ExecutionException("thinking too long");
	}

	// Whether this command is declaration.
	// No by default, overridden by Declaration class.
	public boolean isDeclaration()  { return false; }

	// Require li derived from linked list of tokens.
	// Read first token and create command by calling constructor of subclass.
	// Next token of li is EOF or EOL when done.
	// Result null if no more commands in li.
	protected Command nextCommand(ListIterator i) throws InterpreterException  {
		Token t = null;
		while(i.hasNext())  {
			t = (Token)(i.next());
			if(t.getType() != Token.NEWLINE) break;
		}
		if(t == null || t.getType() == Token.NEWLINE) return null;
		switch(t.getType())  {
			case Token.KEY_ARRAY:     return new ArrayDeclaration(i, t, this);
			case Token.KEY_CHOOSE:    return new ChooseCommand(i, t, this);
			case Token.KEY_CONSTANT:  return new ConstantDeclaration(i, t, this);
			case Token.KEY_ELSE:      return new ElseCommand(i, t, parent);
			case Token.KEY_ELSEIF:    return new ElseifCommand(i, t, parent);
			case Token.KEY_END:       return new Command(i, t);
			case Token.KEY_FUNCTION:  return new FunctionDeclaration(i, t, this);
			case Token.KEY_IF:        return new IfCommand(i, t, this);
			case Token.KEY_INVOKE:    return new InvokeCommand(i, t, this);
			case Token.KEY_LET:       return new LetCommand(i, t, this);
			case Token.KEY_TYPE:      return new TypeDeclaration(i, t, this);
			case Token.KEY_VARIABLE:  return new VariableDeclaration(i, t, this);
			case Token.KEY_WHILE:     return new WhileCommand(i, t, this);
			default:
				throw new InterpreterException("Invalid start of command: '" + t.getContent() + "'.", t);
		}
	}

	// Generate an 'end' command.
	// Need only ensure no more tokens on same line.
	// Let l be line.
	private Command(ListIterator i, Token k) throws InterpreterException  {
		requireEOL(i);
		type = END;
		keyword = k;
	}

	// Superconstructor for all subclasses.
	// Let l be line, p be parent command, y be command type.
	protected Command(Token k, Command p, int y)  {
		keyword = k;
		parent = p;
		type = y;
	}

	// Resolve any symbols and check other integrity requirements.
	// Should be overridden by subclasses.
	public void resolve() throws InterpreterException  { }

	// Resolve list of subcommands.
	// Used by block based commands.
	protected void resolve(LinkedList l) throws InterpreterException  {
		ListIterator i = l.listIterator();
		while(i.hasNext())  {
			Command c = (Command)(i.next());
			c.resolve();
		}
	}

	// Execute list of subcommands l sequentially for r.
	// Used for execution of block based commands.
	public void executeList(LinkedList l, Robot r) throws ExecutionException  {
		ListIterator i = l.listIterator();
		while(i.hasNext() && r.getWait() == 0)  {
			Command c = (Command)(i.next());
			c.execute(r);
		}
		incrementCommandCount(r);
	}

	// Get the next token from token list i.
	// If no next token, report error s and throw exception.
	protected static Token next(ListIterator i, String s) throws InterpreterException  {
		if(!i.hasNext()) throw new InterpreterException(s, null);
		return (Token)(i.next());
	}

	// Require next token is EOL.
	protected static void requireEOL(ListIterator i) throws InterpreterException  {
		if(i.hasNext())  {
			Token t = (Token)(i.next());
			if(t.getType() != Token.NEWLINE) throw new InterpreterException("'EOL' expected, but got '" + t.getContent() + "'.", t);
		}
	}

	// Should be overridden by subclasses.
	// Robot executes Program, program executes other commands.
	public void execute(Robot r) throws ExecutionException  {  }

	// Find declaration named s in local list.
	// Null if s not found.
	public Declaration localSearch(String s)  {
		ListIterator i = declarations.listIterator();
		while(i.hasNext())  {
			Declaration d = (Declaration)(i.next());
			if(d.getResolved() && d.getName().equals(s))
				return d;
		}
		return null;
	}

	// Find declaration named s in local list or broader scope.
	// Null if s not found.
	public Declaration globalSearch(String s)  {
		Declaration d = localSearch(s);
		if(parent == null || d != null)
			return d;
		return parent.globalSearch(s);
	}

}
