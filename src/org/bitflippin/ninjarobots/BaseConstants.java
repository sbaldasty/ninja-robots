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

// Set of constants available automatically to robot programs.
// Used by various parts of the simulator and interpreter.

public class BaseConstants  {

	// Boolean.
	public static final int TRUE = 255;
	public static final int FALSE = 0;

	// Direction.
	public static final int HERE = 1;
	public static final int NORTH = 2;
	public static final int EAST = 3;
	public static final int SOUTH = 4;
	public static final int WEST = 5;

	// Action.
	public static final int MOVE = 6;
	public static final int WHISPER = 7;
	public static final int TAKE = 8;
	public static final int DROP = 9;
	public static final int ATTACK = 10;

	// Queries.
	public static final int OCCUPANT = 11;
	public static final int TEAM = 12;
	public static final int HEALTH = 13;
	public static final int WORD = 14;

	// Occupants.
	public static final int NOBODY = 15;
	public static final int WALL = 16;
	public static final int PRODUCER = 17;
	public static final int ROBOT = 18;
	public static final int TREASURE = 19;
	public static final int POWERITEM = 20;
	public static final int HEALTHITEM = 21;

	// Teams.
	public static final int RED = 22;
	public static final int GREEN = 23;
	public static final int BLUE = 24;
	public static final int PURPLE = 25;
	public static final int UNOWNED = 26;


	// Parameters.
	public static final int UNDEFINED = 27;

}
