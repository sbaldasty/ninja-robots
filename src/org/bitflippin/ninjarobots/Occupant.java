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

// Something that may be in a cell.

public abstract class Occupant  {

	// Initialize variables.
	public Occupant(NinjaRobots f, Cell c)  {
		frame = f;
		cell = c;
	}

	// Link to main frame.
	protected NinjaRobots frame;

	// Where occupant is located.
	public Cell getCell()  { return cell; }
	protected Cell cell;

	// Whether or not occupant is flashing.
	// Flashing occupants appear bright white.
	protected boolean hit = false;

	// Make occupant flash.
	public void flash(boolean erase)  {
		Viewer v = frame.simulation.getViewer();
		int x = cell.getX();
		int y = cell.getY();
		hit = true;
		v.update(x, y);
		try  { Thread.sleep(100); }
		catch(InterruptedException e)  {
			System.out.println("Sleep interrupted (flash timer).");
			System.exit(0);
		}
		hit = false;
		if(erase) cell.setOccupant(null);
		v.update(x, y);
	}

	// Access to child class types.
	abstract public boolean isRobot();
	abstract public boolean isTreasure();
	abstract public boolean isHealthItem();
	abstract public boolean isPowerItem();
	abstract public boolean isProducer();

	// Can be carried by robots.
	abstract public boolean portable();

	// Code for how occupant appears on screen.
	// Must return image constant.
	abstract public int image();

}
