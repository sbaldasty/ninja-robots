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

import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JLabel;

// Integrated development enviromnents for robot team programs.
// Also contains other information about team like name, number, score, and members.

public class Team extends IDE  {

	// Number of teams supported.
	public static final int TEAMS = 4;

	// Program member robots use to make moves.
	// Robots access it, parse initializes it.
	public Program getProgram()  { return program; }
	private Program program;

	// Make label with team name.
	public JLabel nameLabel()  {
		String name = null;
		switch(number)  {
			case 0: name = "   Red     ";    break;
			case 1: name = "   Green     ";  break;
			case 2: name = "   Blue     ";   break;
			case 3: name = "   Purple     "; break;
		}
		return new JLabel(name);
	}

	// Robots currently on this team.
	// Modified by robots through addMember and removeMember.
	private LinkedList members;

	// Add robot r to this team.
	// Increment score appropriately.
	public void addMember(Robot r)  {
		members.add(r);
		changeScore(frame.scenario.getRobotPoints());
	}

	// Remove robot r from this team.
	// Decrement score appropriately.
	public void removeMember(Robot r)  {
		members.remove(r);
		changeScore(-frame.scenario.getRobotPoints());
	}

	// Change team score by ds.
	// Check for winner.
	public void changeScore(int ds)  {
		score += ds;
		scoreLabel.setText("" + score);
		if(score >= frame.scenario.getWinningScore())  {
			frame.simulation.finish();
			frame.simulation.report("Victory by high score; the simulation is over.");
		}
	}

	// Called whenever tokenization failed.
	// Defined by contract with IDE.
	protected void badToken()  { program = null; }

	// Generate program for use by robots.
	// Defined by contract with IDE.
	protected void parse() throws InterpreterException  {
		program = null;
		program = new Program(tokens.listIterator());
		program.resolve();
		clearButton.setEnabled(true);
	}

	// Title of input area.
	// Defined by contract with IDE.
	protected String header()  { return "Robot program editor:"; }

	// The team score.
	// Updated by robots and treasures as they are gained or lost.
	private int score;

	// Label updates on simulation automatically.
	// Changes when score changes.
	private JLabel scoreLabel = new JLabel("");
	public JLabel getScoreLabel()  { return scoreLabel; }

	// Identification: number generates name.
	public int getNumber()  { return number; }
	private int number;

	// Button to clear IDE.
	// Initialized by constructor.
	private JButton clearButton;

	// Teams need only be created once; they are reused.
	// Create team number n.
	public Team(NinjaRobots f, ProgramPanel p, int n)  {
		super(f, p.getField());
		number = n;
		clearButton = p.getClearButton();
	}

	// Should be called each time begin is pressed.
	// Clears the effects of any previous simulations.
	public void reset()  {
		score = 0;
		scoreLabel.setText("0");
		members = new LinkedList();
	}

}
