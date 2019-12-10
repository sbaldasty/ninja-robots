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

// Event when an occupant flashes bright white once.
// Happens when created or attacked.

public class Flash extends Thread  {

	// Limit number of flashes that happen simultaneously.
	private static int count = 0;
	public static int getCount()  { return count; }

	// Link to viewer.
	private Viewer viewer;

	// Coordinates of update.
	private int x;
	private int y;

	// Let cx and cy be coordinates of flash.
	// Automatically start.
	public Flash(Viewer v, int cx, int cy)  {
		super();
		viewer = v;
		x = cx;
		y = cy;
		v.update(x, y);
		count++;
		setPriority(MIN_PRIORITY);
		start();
	}

	// Wait and then perform update.
	public void run()  {
		try  { sleep(100); }
		catch(InterruptedException e)  {
			System.out.println("Sleep interrupted (flash timer).");
			System.exit(0);
		}
		viewer.update(x, y);
		count--;
	}

}
