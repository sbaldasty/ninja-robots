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

// Occupant that can be taken, dropped, or destroyed by robots.
// Health items restore health of robots.
// Power items make robot attacks more powerful.
// To be used, one robot drops an item on another.

public class Item extends Occupant  {

	// Access to class type.
	public boolean isRobot()  { return false; }
	public boolean isTreasure()  { return false; }
	public boolean isHealthItem()  { return type == 's'; }
	public boolean isPowerItem()  { return type == 'k'; }
	public boolean isProducer()  { return false; }

	public Item(NinjaRobots f, Cell c, char t)  {
		super(f, c);
		type = t;
	}

	// Can be carried by robots.
	// Defined by contract with Occupant.
	public boolean portable()  { return true; }

	// Defined by contract with Occupant.
	public int image()  {
		if(type == 's')
			if(hit) return Viewer.CH_006_15;
			else return Viewer.CH_006_03;
		else
			if(hit) return Viewer.CH_007_15;
			else return Viewer.CH_007_03;
	}

	// Map data code for this item ('s' or 'k').
	private char type;

}
