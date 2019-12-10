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

// Occupants only created by scenario map data.
// Generate new robots, treasures, or items around themselves periodically.
// Appear as stars, and cannot be destroyed.

public class Producer extends Occupant implements TunitWatcher  {

	// Map data code for product.
	private char code;

	// How many tunits to wait before refilling.
	private static final int TUNITS = 300;

	// Tunits to wait before refilling each direction.
	// Once countdown begins, it only waits again at 0.
	private int wait[] = new int[4];

	// Has the count for that direction begun?
	// Begins once cell in direction is not occupied.
	private boolean started[] = new boolean[4];

	// Can be carried by robots.
	// Defined by contract with Occupant.
	public boolean portable()  { return false; }

	// Let p be map data code for product.
	public Producer(NinjaRobots f, Cell c, char p)  {
		super(f, c);
		for(int i = 0; i < 4; i++)  {
			wait[i] = TUNITS;
			started[i] = false;
		}
		code = Character.toLowerCase(p);
	}

	// Producers always produce.
	// Defined by contract with TunitWatcher.
	public boolean isActive()  { return true; }

	// Access to class type.
	// Defined by contract with Occupant.
	public boolean isRobot()  { return false; }
	public boolean isTreasure()  { return false; }
	public boolean isHealthItem()  { return false; }
	public boolean isPowerItem()  { return false; }
	public boolean isProducer()  { return true; }

	// Called when a tunit passes.
	// Defined by contract with TunitWatcher.
	// Generate new products if appropriate.
	public void timeElapses()  {
		for(int i = 0; i < 4; i++)  {
			Cell c = cell.neighbor(i);
			Occupant o = c.getOccupant();
			if(!c.isWall())
				if(started[i])  {
					if(wait[i] > 0)
						wait[i]--;
					else if(o == null)  {
						if(code == 'd')
							c.setOccupant(new Treasure(frame, c));
						else if(code == 'u')
							c.setOccupant(new Robot(frame, c, null));
						else
							c.setOccupant(new Item(frame, c, code));
						started[i] = false;
						c.getOccupant().flash(false);
					}
				}
				else if(o == null)  {
					started[i] = true;
					wait[i] = TUNITS;
				}
		}
	}

	// How occupant appears on screen.
	// Defined by contract with Occupant.
	public int image()  { return Viewer.CH_015_06; }

}
