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

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// Cause simulation to run.
// Simulation must run on separate thread to avoid GUI freeze.
// New runners must be created whenever run is pressed.

public class Runner extends Thread implements ChangeListener  {

	// If stop has been requested.
	// Stops are requested externally by simulator.
	private boolean stopping = false;
	public boolean isStopping()  { return stopping; }

	// Request the thread to stop.
	// Requests made only by simulator when stop is pressed.
	public void requestStop()  { stopping = true; }

	// How long to sleep between tunits.
	// Function of value of JSlider on simulation screen.
	private int delay;

	// Link to the simlulation.
	private Simulation simulation;

	// Let d be delay.
	public Runner(int d, Simulation s)  {
		super();
		delay = d;
		simulation = s;
		setPriority(MIN_PRIORITY);
	}

	// Cycle through the tunits, telling everyone to move.
	// Redefined from Thread.
	public void run()  {
		while(!isStopping())  {
			if(!simulation.getIterator().hasNext())  {
				simulation.resetIterator();
				simulation.timeElapses();
				try  { sleep(10 + (10 - delay) * 100); }
				catch(InterruptedException e)  {
					System.out.println("Sleep interrupted (runner).");
					System.exit(0);
				}
			}
			else  {
				Object o = simulation.getIterator().next();
				TunitWatcher w = (TunitWatcher)(o);
				if(w.isActive())
					w.timeElapses();
				if(!w.isActive())
					simulation.getIterator().remove();
			}
		}
	}

	// Attached to speed bar on the simulation panel.
	// Defined by contract with ChangeListener.
	// Update value of delay.
	public void stateChanged(ChangeEvent e)  {
		JSlider s = (JSlider)(e.getSource());
		delay = s.getValue();
	}

}
