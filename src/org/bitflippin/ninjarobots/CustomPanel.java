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

import java.awt.Component;
import java.awt.LayoutManager;

import javax.swing.JLabel;
import javax.swing.JPanel;

// Panels with new ways to add components for convenience.
// Optionally support link to main frame for specialized panels.

public class CustomPanel extends JPanel  {

	// Link to main frame.
	protected NinjaRobots frame;

	// Carry over default constructor.
	// Support initialization of link to main frame.
	public CustomPanel(NinjaRobots f)  {
		super();
		frame = f;
	}

	// Carry over layout constructor.
	// Support initialization of link to main frame.
	public CustomPanel(LayoutManager layout, NinjaRobots f)  {
		super(layout);
		frame = f;
	}

	// Original constructors.
	public CustomPanel()  { super(); }
	public CustomPanel(LayoutManager layout)  { super(layout); }

	// Put s in new JLabel, JLabel in new JPanel with FlowLayout.
	// Add JPanel to this panel.
	public void addCtr(String s)  {
		JPanel p = new JPanel();
		p.add(new JLabel(s));
		add(p);
	}

	// Put s in new JLabel, JLabel in new JPanel with FlowLayout.
	// Add JPanel to this panel with constraints.
	public void addCtr(String s, Object constraints)  {
		JPanel p = new JPanel();
		p.add(new JLabel(s));
		add(p, constraints);
	}

	// Put comp in new JPanel with FlowLayout.
	// Add JPanel to this panel.
	public void addCtr(Component comp)  {
		JPanel p = new JPanel();
		p.add(comp);
		add(p);
	}

	// Put comp in new JPanel with FlowLayout.
	// Add JPanel to this panel with constraints.
	public void addCtr(Component comp, Object constraints)  {
		JPanel p = new JPanel();
		p.add(comp);
		add(p, constraints);
	}

}
