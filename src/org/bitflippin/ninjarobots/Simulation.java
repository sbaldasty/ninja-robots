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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

// Screen where simulation runs.
// Contains controls and viewport, starts and stops runners.

public class Simulation extends CustomPanel implements ActionListener, TunitWatcher  {

	// Private components.
	private JButton doneButton = new JButton("Done");
	private JButton runButton = new JButton("Run");
	private JButton stopButton = new JButton("Stop");
	private JButton infoButton = new JButton("Info");
	private JSlider speedBar = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);

	// Text area robots use to dump why they die.
	public CustomTextArea getDeathArea()  { return deathArea; }
	private CustomTextArea deathArea = new CustomTextArea(frame, null, 6);

	// How many tunits have elapsed.
	private int tunits;
	private JLabel tunitLabel = new JLabel("");

	// Add new tunit watcher.
	// Use iterator if exists to avoid concurrent modification.
	public void addTunitWatcher(TunitWatcher w)  {
		if(iterator == null)
			tunitWatchers.add(w);
		else
			iterator.add(w);
	}

	// Things that need notification when tunits pass.
	private LinkedList tunitWatchers;

	// For the runner to use; recreated every tunit.
	public ListIterator getIterator()  { return iterator; }
	private ListIterator iterator;

	// Separate thread to run simulation in background.
	// Needs to be recreated every time run is clicked.
	private Runner runner;

	// Special viewer.
	public Viewer getViewer()  { return viewer; }
	private Viewer viewer;

	// Initialize components.
	public Simulation(NinjaRobots f)  {
		super(f);
		viewer = new Viewer(f);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		doneButton.addActionListener(this);
		runButton.addActionListener(this);
		stopButton.addActionListener(this);
		stopButton.setEnabled(false);
		infoButton.setEnabled(false);
		speedBar.setMajorTickSpacing(5);
		speedBar.setMinorTickSpacing(1);
		speedBar.setPaintTicks(true);
		speedBar.setSnapToTicks(true);
		int h = (int)(speedBar.getPreferredSize().getHeight());
		speedBar.setPreferredSize(new Dimension(115, h));
		CustomPanel p = new CustomPanel(new BorderLayout());
		p.add(viewer, BorderLayout.WEST);
		CustomPanel q = new CustomPanel();
		q.setLayout(new BoxLayout(q, BoxLayout.Y_AXIS));
		CustomPanel r = new CustomPanel(new GridLayout(2, 2));
		r.add(runButton);
		r.add(doneButton);
		r.add(stopButton);
		r.add(infoButton);
		q.addCtr(r);
		CustomPanel s = new CustomPanel(new BorderLayout());
		s.addCtr(tunitLabel, BorderLayout.NORTH);
		s.addCtr(speedBar, BorderLayout.CENTER);
		q.addCtr(s);
		GridLayout l = new GridLayout(Team.TEAMS, 2);
		l.setVgap(0);
		CustomPanel t = new CustomPanel(l);
		t.setBorder(new EtchedBorder());
		for(int i = 0; i < Team.TEAMS; i++)  {
			t.add(frame.team[i].nameLabel());
			t.addCtr(frame.team[i].getScoreLabel());
		}
		q.addCtr(t);
		p.add(q, BorderLayout.EAST);
		deathArea.getArea().setEditable(false);
		p.add(deathArea, BorderLayout.SOUTH);
		add(p);
		setVisible(false);
		frame.getContentPane().add(this);
	}

	// End the simulation; it cannot run again.
	public void finish()  {
		stopButton.setEnabled(false);
		runButton.setEnabled(false);
		doneButton.setEnabled(true);
		runner.requestStop();
	}

	// Make visible and set everything up.
	// Called when begin is pressed.
	public void reset()  {
		runButton.setEnabled(true);
		stopButton.setEnabled(false);
		doneButton.setEnabled(true);
		viewer.fit(frame.scenario);
		tunits = 0;
		tunitLabel.setText("0 tunits elapsed");
		tunitWatchers = frame.scenario.getActiveStarters();
		resetIterator();
		deathArea.getArea().setText("");
		setVisible(true);
	}

	// Called by runner after every tunit.
	// Simply resets iterator to first tunit watcher again.
	public void resetIterator()  { iterator = tunitWatchers.listIterator(0); }

	// Button driven action dispatcher.
	// Defined by contract with ActionListener.
	public void actionPerformed(ActionEvent e)  {
		Object o = e.getSource();
		if(o == doneButton)
			pressedDone();
		else if(o == runButton)
			pressedRun();
		else if(o == stopButton)
			pressedStop();
	}

	// Back to setup screen.
	// Only available when runner is stopped.
	private void pressedDone()  {
		doneButton.setEnabled(false);
		runButton.setEnabled(false);
		setVisible(false);
		viewer.fit(null);
		frame.setupPanel.setVisible(true);
	}

	// Resume or begin simulation.
	// Generate new runner because threads only run once.
	private void pressedRun()  {
		runButton.setEnabled(false);
		doneButton.setEnabled(false);
		stopButton.requestFocus();
		if(runner != null)
			speedBar.removeChangeListener(runner);
		runner = new Runner(speedBar.getValue(), this);
		speedBar.addChangeListener(runner);
		runner.start();
		stopButton.setEnabled(true);
	}

	// Ask runner to halt simulation.
	// Simulation stops after next tunit watcher moves.
	private void pressedStop()  {
		stopButton.setEnabled(false);
		runner.requestStop();
		while(runner.isAlive());
		runButton.setEnabled(true);
		doneButton.setEnabled(true);
		runButton.requestFocus();
	}

	// Defined by contract with TunitWatcher.
	// Simulation never dies.
	public boolean isActive()  { return true; }

	// Defined by contract with TunitWatcher.
	// Update label and time.
	public void timeElapses()  {
		tunitLabel.setText("" + (++tunits) + " tunits elapsed");
		if(tunits == frame.scenario.getTimeLimit())  {
			finish();
			report("Time expired; the simulation is over.");
		}
	}

	// Add s to the death area.
	public void report(String s)  {
		JTextArea a = deathArea.getArea();
		a.append(s + "\n");
		a.setCaretPosition(a.getText().length());
	}

}
