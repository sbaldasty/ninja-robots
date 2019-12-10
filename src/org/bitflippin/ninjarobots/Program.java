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

public class Program extends Command  {

	// Program must reach resolution after this many commands.
	// Otherwise assume infinite loop and kill robot.
	public static final int MAXIMUM_COMMANDS = 500;

	// Series of commands that form the program.
	private LinkedList subcommands = new LinkedList();

	// Let i be over token list.
	// Keep adding subcommands until i exhausted.
	public Program(ListIterator i) throws InterpreterException  {
		super(null, null, PROGRAM);
		MemoryBank.resetCounter();
		addConstants();
		Command c = nextCommand(i);
		if(c == null) throw new InterpreterException("Load or type a robot program.", null);
		while(c != null)  {
			int y = c.getType();
			if(y == Command.END) throw new InterpreterException("Unmatched end statement.", c.getKeyword());
			if(y == Command.ELSE) throw new InterpreterException("Else statement has no if.", c.getKeyword());
			if(y == Command.ELSEIF) throw new InterpreterException("Elseif statement has no if.", c.getKeyword());
			if(c.isDeclaration())
				declarations.add(c);
			subcommands.add(c);
			c = nextCommand(i);
		}
	}

	// Execute subcommands sequentially.
	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		stackPointer = MemoryBank.PERMANENT_SIZE;
		commandCount = 0;
		executeList(subcommands, r);
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  { resolve(subcommands); }

	// Add built-in constant to list of declarations.
	// Let n be name and v be value.
	private void addConstant(String n, int v)  { declarations.add(new ConstantDeclaration(n, v)); }

	// Add all the built-in constants.
	private void addConstants()  {
		addConstant("TRUE", BaseConstants.TRUE);
		addConstant("FALSE", BaseConstants.FALSE);
		addConstant("HERE", BaseConstants.HERE);
		addConstant("NORTH", BaseConstants.NORTH);
		addConstant("EAST", BaseConstants.EAST);
		addConstant("SOUTH", BaseConstants.SOUTH);
		addConstant("WEST", BaseConstants.WEST);
		addConstant("MOVE", BaseConstants.MOVE);
		addConstant("WHISPER", BaseConstants.WHISPER);
		addConstant("TAKE", BaseConstants.TAKE);
		addConstant("DROP", BaseConstants.DROP);
		addConstant("ATTACK", BaseConstants.ATTACK);
		addConstant("OCCUPANT", BaseConstants.OCCUPANT);
		addConstant("TEAM", BaseConstants.TEAM);
		addConstant("HEALTH", BaseConstants.HEALTH);
		addConstant("WORD", BaseConstants.WORD);
		addConstant("NOBODY", BaseConstants.NOBODY);
		addConstant("WALL", BaseConstants.WALL);
		addConstant("PRODUCER", BaseConstants.PRODUCER);
		addConstant("ROBOT", BaseConstants.ROBOT);
		addConstant("TREASURE", BaseConstants.TREASURE);
		addConstant("POWERITEM", BaseConstants.POWERITEM);
		addConstant("HEALTHITEM", BaseConstants.HEALTHITEM);
		addConstant("RED", BaseConstants.RED);
		addConstant("GREEN", BaseConstants.GREEN);
		addConstant("BLUE", BaseConstants.BLUE);
		addConstant("PURPLE", BaseConstants.PURPLE);
		addConstant("UNOWNED", BaseConstants.UNOWNED);
		addConstant("UNDEFINED", BaseConstants.UNDEFINED);
	}

}
