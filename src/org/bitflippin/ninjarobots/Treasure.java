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

// Diamonds that give any owning teams points.
// Can be taken, dropped, and attacked by robots.
// Assume color of owning team - ownership can change.

public class Treasure extends Occupant  {

	// Access to class type.
	public boolean isRobot()  { return false; }
	public boolean isTreasure()  { return true; }
	public boolean isHealthItem()  { return false; }
	public boolean isPowerItem()  { return false; }
	public boolean isProducer()  { return false; }

	// Defined by contract with Occupant, but never called.
	protected int go()  { return 1; }

	// Can be carried by robots.
	// Defined by contract with Occupant.
	public boolean portable()  { return true; }

	// Who owns treasure at this time.
	public Team getTeam()  { return team; }
	private Team team;

	// Initialize variables.
	public Treasure(NinjaRobots f, Cell c)  { super(f, c); }

	// Award this treasure to t.
	public void claimFor(Team t)  {
		team = t;
		t.changeScore(frame.scenario.getTreasurePoints());
		frame.simulation.getViewer().update(cell.getX(), cell.getY());
	}

	// Depends on owner and being hit.
	// Defined by contract with Occupant.
	public int image()  {
		if(hit == true)
			return Viewer.CH_004_15;
		if(team == null)
			return Viewer.CH_004_07;
		switch(team.getNumber())  {
			case 0: return Viewer.CH_004_04;
			case 1: return Viewer.CH_004_02;
			case 2: return Viewer.CH_004_03;
			case 3: return Viewer.CH_004_05;
		}
		return -1;   // Never gets here.
	}

}
