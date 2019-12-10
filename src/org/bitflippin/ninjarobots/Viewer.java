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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

// Panel through which the map is viewed.
// Has scrollbars so user can select which portion is visible.
// Also responsible for loading images from disk.

public class Viewer extends CustomPanel implements AdjustmentListener  {

	// Image constants.
	public static final int CH_000_00 = 0;
	public static final int CH_001_02 = 1;
	public static final int CH_001_03 = 2;
	public static final int CH_001_04 = 3;
	public static final int CH_001_05 = 4;
	public static final int CH_001_07 = 5;
	public static final int CH_001_14 = 6;
	public static final int CH_001_15 = 7;
	public static final int CH_002_02 = 8;
	public static final int CH_002_03 = 9;
	public static final int CH_002_04 = 10;
	public static final int CH_002_05 = 11;
	public static final int CH_002_14 = 12;
	public static final int CH_002_15 = 13;
	public static final int CH_004_02 = 14;
	public static final int CH_004_03 = 15;
	public static final int CH_004_04 = 16;
	public static final int CH_004_05 = 17;
	public static final int CH_004_07 = 18;
	public static final int CH_004_15 = 19;
	public static final int CH_006_03 = 20;
	public static final int CH_006_15 = 21;
	public static final int CH_007_03 = 22;
	public static final int CH_007_15 = 23;
	public static final int CH_015_06 = 24;
	public static final int CH_177_06 = 25;

	// Private components.
	private JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL);
	private JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);

	// Link to the scenario and canvas.
	private Scenario scenario;
	private CustomCanvas canvas;

	// Robot currently selected.
	private Robot selected;

	// Initialize components, load images.
	public Viewer(NinjaRobots f)  {
		super(f);
		canvas = new CustomCanvas(f, this);
		hbar.addAdjustmentListener(this);
		vbar.addAdjustmentListener(this);
		JPanel p = new JPanel(new BorderLayout());
		p.add(canvas, BorderLayout.CENTER);
		p.add(hbar, BorderLayout.SOUTH);
		p.add(vbar, BorderLayout.EAST);
		add(p);
	}

	// Initialize for s and make canvas visible.
	// If s null, shut down.
	// Note quirky thing with canvas being permanently visible.
	public void fit(Scenario s)  {
		for(int x = 0; x < CustomCanvas.WIDTH; x++)
			for(int y = 0; y < CustomCanvas.HEIGHT; y++)
				canvas.modify(x, y, CH_000_00);
		scenario = s;
		selected = null;
		if(s == null)
			canvas.setVisible(false);
		else  {
			if(s.getMapWidth() <= CustomCanvas.WIDTH)  {
				hbar.setValue(0);
				hbar.setEnabled(false);
			}
			else  {
				hbar.setEnabled(true);
				int hmax = s.getMapWidth() - CustomCanvas.WIDTH + hbar.getVisibleAmount();
				hbar.setValues(0, hbar.getVisibleAmount(), 0, hmax);
			}
			if(s.getMapHeight() <= CustomCanvas.HEIGHT)  {
				vbar.setValue(0);
				vbar.setEnabled(false);
			}
			else  {
				vbar.setEnabled(true);
				int vmax = s.getMapHeight() - CustomCanvas.HEIGHT + vbar.getVisibleAmount();
				vbar.setValues(0, vbar.getVisibleAmount(), 0, vmax);
			}
			canvas.setVisible(true);
			adjustmentValueChanged(null);
		}
	}

	// Called when user clicks canvas.
	// Let x and y be the cells that are clicked (not pixel coords).
	public void select(int x, int y)  {
		if(x >= scenario.getMapWidth() || y >= scenario.getMapHeight()) return;
		Occupant o = scenario.getCell(x + hbar.getValue(), y + vbar.getValue()).getOccupant();
		if(selected != null)  {
			Cell c = selected.getCell();
			selected.setSelected(false);
			selected = null;
			update(c.getX(), c.getY());
		}
		if(o != null && o.isRobot())  {
			selected = (Robot)(o);
			selected.setSelected(true);
			Cell c = selected.getCell();
			update(c.getX(), c.getY());
		}
	}

	// Tell the canvas to update (x,y) iff (x,y) is showing.
	public void update(int x, int y)  {
		int hv = hbar.getValue();
		int vv = vbar.getValue();
		if(
			x >= hv &&
			x < hv + CustomCanvas.WIDTH &&
			y >= vv &&
			y < vv + CustomCanvas.HEIGHT
		)  {
			canvas.modify(x - hv, y - vv, frame.scenario.getCell(x, y).image());
			canvas.update(x - hv, y - vv);
		}
	}

	// Defined by contract with AdjustmentListener.
	// Refresh entire canvas.
	public void adjustmentValueChanged(AdjustmentEvent e)  {
		int hv = hbar.getValue();
		int vv = vbar.getValue();
		int cw = CustomCanvas.WIDTH;
		int ch = CustomCanvas.HEIGHT;
		int sw = scenario.getMapWidth();
		int sh = scenario.getMapHeight();
		int xmax = (cw >= sw) ? sw : hv + cw;
		int ymax = (ch >= sh) ? sh : vv + ch;
		for(int x = hv; x < xmax; x++)
			for(int y = vv; y < ymax; y++)
				canvas.modify(x - hv, y - vv, scenario.getCell(x, y).image());
		canvas.paint(null);
	}

}
