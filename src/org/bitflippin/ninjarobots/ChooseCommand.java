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

public class ChooseCommand extends Command  {

	// Evaluates to action code.
	private Expression action;

	// Evaluates to argument, usually direction.
	private Expression argument;

	// Verify syntax and assign known parameters.
	// Let k be keyword and p be parent command.
	public ChooseCommand(ListIterator i, Token k, Command p) throws InterpreterException  {
		super(k, p, CHOOSE);
		action = new Expression(i, false);
		argument = new Expression(i, false);
		requireEOL(i);
	}

	// Cause robot to perform some action.
	// Redefined from Command.
	public void execute(Robot r) throws ExecutionException  {
		if(r.getWait() != 0) return;
		stackPointer = parent.getStackPointer();
		int a = argument.value(r);
		int d = a - BaseConstants.NORTH;
		switch(action.value(r))  {
			case BaseConstants.MOVE:    r.actionMove(d);    break;
			case BaseConstants.WHISPER: r.actionWhisper(a); break;
			case BaseConstants.TAKE:    r.actionTake(d);    break;
			case BaseConstants.DROP:    r.actionDrop(d);    break;
			case BaseConstants.ATTACK:  r.actionAttack(d);  break;
			default: throw new ExecutionException("invalid action");
		}
	}

	// Redefined from Command.
	public void resolve() throws InterpreterException  {
		action.resolve(this);
		argument.resolve(this);
		if(!action.isNumeric() || !argument.isNumeric()) throw new InterpreterException("Expression does not resolve to a number.", getKeyword());
	}

}
