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
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// Wrapper for text areas used in IDE's and robot death dump.
// These areas have fixed width, tabs, fonts, and scrollbars.

class CustomTextArea extends CustomPanel  {

	// Access to text area enclosed by this structure.
	private JTextArea area;
	public JTextArea getArea()  { return area; }

	// Text areas for the editing panels.
	// Title s and h chars high.
	public CustomTextArea(NinjaRobots f, String s, int height)  {
		super(new BorderLayout(), f);
		area = new JTextArea(height, 60);
		area.setFont(new Font("courier", Font.PLAIN, 12));
		area.setTabSize(2);
		if(s != null)
			addCtr(s, BorderLayout.NORTH);
		int v = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;
		int h = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;
		JScrollPane j = new JScrollPane(area, v, h);
		addCtr(j, BorderLayout.CENTER);
		addCtr(" ", BorderLayout.SOUTH);
	}

}
