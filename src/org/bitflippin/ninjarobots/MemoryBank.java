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

// Robot permanent and stack memories in single bank.
// Permanent memory occupies lowest indecies.
// Runtime stack shared among all robots.

public class MemoryBank  {

	// Permanent memory size in numbers.
	public static final int PERMANENT_SIZE = 500;

	// Permanent memory sub-bank.
	private short permanent[] = new short[PERMANENT_SIZE];

	// How much of permanent memory has been reserved.
	// Used to choose addresses for new symbols.
	public static int getUsed()  { return used; }
	private static int used;

	// Reset permanent memory counter for new compilation.
	public static void resetCounter()  { used = 0; }

	// Reserve n spaces in permanent memory.
	public static void reserve(int n) throws InterpreterException  {
		used += n;
		if(used >= PERMANENT_SIZE)
			throw new InterpreterException("Program uses too much permanent memory", null);
	}

	// Runtime stack size in numbers.
	private static final int STACK_SIZE = 10000;

	// Runtime stack sub-bank.
	private short stack[] = new short[STACK_SIZE];

	// Memory read for whole bank.
	// If index higher than permanent memory, it is in stack.
	public int read(int index) throws ExecutionException  {
		if(index >= PERMANENT_SIZE + STACK_SIZE)
			throw new ExecutionException("stack overflow");
		if(index < PERMANENT_SIZE)
			return permanent[index];
		return stack[index - PERMANENT_SIZE];
	}

	// Memory write for whole bank.
	// If index higher than permanent memory, it is in stack.
	public void write(int index, int value) throws ExecutionException  {
		if(index >= PERMANENT_SIZE + STACK_SIZE)
			throw new ExecutionException("stack overflow");
		if(index < PERMANENT_SIZE)
			permanent[index] = (short)(value);
		else
			stack[index - PERMANENT_SIZE] = (short)(value);
	}

}
