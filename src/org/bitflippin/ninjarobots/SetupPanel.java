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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Panel that appears first.
// Creates critical elements for the main frame.
// Allows user to launch scenario and robot program IDE's.

public class SetupPanel extends CustomPanel implements ActionListener  {

	// Private components.
	private JButton beginButton = new JButton("Begin");
	private JButton inputButton = new JButton("Input");
	private JTextField field = new JTextField(15);

	// Initialize components.
	public SetupPanel(NinjaRobots f)  {
		super(new BorderLayout(), f);
		beginButton.addActionListener(this);
		inputButton.addActionListener(this);
		beginButton.setEnabled(false);
		field.setEnabled(false);
		addCtr(" ", BorderLayout.NORTH);
		JPanel p = new JPanel(new BorderLayout());
		p.add(makeTop(), BorderLayout.NORTH);
		p.add(makeBottom(), BorderLayout.CENTER);
		p.add(new JLabel(" "), BorderLayout.SOUTH);
		add(p, BorderLayout.CENTER);
		add(beginButton, BorderLayout.SOUTH);
		frame.scenario = new Scenario(frame, field, beginButton);
		setVisible(false);
		frame.getContentPane().add(this);
	}

	// Add active scenario components.
	// Label, scenario name field, and set button.
	private JPanel makeTop()  {
		CustomPanel p0 = new CustomPanel(new BorderLayout(), frame);
		p0.addCtr("Select scenario:", BorderLayout.NORTH);
		JPanel p1 = new JPanel(new FlowLayout());
		p1.add(field);
		p1.add(new JLabel("     "));
		p1.add(inputButton);
		p0.add(p1, BorderLayout.CENTER);
		p0.add(new JLabel(" "), BorderLayout.SOUTH);
		return p0;
	}

	// Add team program components.
	// Four labels, program name fields, and clear and set buttons.
	private JPanel makeBottom()  {
		CustomPanel p0 = new CustomPanel(new BorderLayout(), frame);
		p0.addCtr("Select team programs:", BorderLayout.NORTH);
		JPanel p1 = new JPanel(new GridLayout(Team.TEAMS, 1));
		for(int i = 0; i < Team.TEAMS; i++)  {
			ProgramPanel panel = new ProgramPanel(frame, i);
			p1.add(panel);
			frame.team[i] = panel.getTeam();
		}
		p0.add(p1, BorderLayout.CENTER);
		p0.add(new JLabel(" "), BorderLayout.SOUTH);
		return p0;
	}

	// Button driven action dispatcher.
	// Defined by contract with ActionListener.
	public void actionPerformed(ActionEvent e)  {
		Object o = e.getSource();
		if(o == beginButton)
			pressedBegin();
		else if(o == inputButton)
			pressedScenario();
	}

	// Swap to simulation screen.
	// Only available when scenario is loaded.
	private void pressedBegin()  {
		frame.setupPanel.setVisible(false);
		for(int i = 0; i < Team.TEAMS; i++)
			frame.team[i].reset();
		frame.scenario.reset();
		frame.simulation.reset();
	}

	// Swap to scenario IDE screen.
	private void pressedScenario()  {
		setVisible(false);
		frame.scenario.setVisible(true);
	}

}
