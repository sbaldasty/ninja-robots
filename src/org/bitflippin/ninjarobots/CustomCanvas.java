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

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

// Canvas on which cells are drawn.
// Visual cell map updated by viewer.

public class CustomCanvas extends Canvas implements MouseListener  {

	// Dimensions in cells.
	// Dimensions in pixels derived from this.
	public static final int HEIGHT = 15;
	public static final int WIDTH = 18;

	// Map of image constants that appear here.
	private int map[][] = new int[HEIGHT][WIDTH];

	// Image repository.
	// Each element loaded from disk upon construction.
	private Image list[] = new Image[26];

	// Link to canvas graphics and viewer.
	private Graphics graphics;
	private Viewer viewer;
	
	// Set size and load images.
	public CustomCanvas(NinjaRobots f, Viewer v)  {
		super();
		setVisible(false);
		setSize(WIDTH * Cell.WIDTH, HEIGHT * Cell.HEIGHT);
		addMouseListener(this);
		viewer = v;
		load(Viewer.CH_000_00, "ch_000_00.gif");
		load(Viewer.CH_001_02, "ch_001_02.gif");
		load(Viewer.CH_001_03, "ch_001_03.gif");
		load(Viewer.CH_001_04, "ch_001_04.gif");
		load(Viewer.CH_001_05, "ch_001_05.gif");
		load(Viewer.CH_001_07, "ch_001_07.gif");
		load(Viewer.CH_001_14, "ch_001_14.gif");
		load(Viewer.CH_001_15, "ch_001_15.gif");
		load(Viewer.CH_002_02, "ch_002_02.gif");
		load(Viewer.CH_002_03, "ch_002_03.gif");
		load(Viewer.CH_002_04, "ch_002_04.gif");
		load(Viewer.CH_002_05, "ch_002_05.gif");
		load(Viewer.CH_002_14, "ch_002_14.gif");
		load(Viewer.CH_002_15, "ch_002_15.gif");
		load(Viewer.CH_004_02, "ch_004_02.gif");
		load(Viewer.CH_004_03, "ch_004_03.gif");
		load(Viewer.CH_004_04, "ch_004_04.gif");
		load(Viewer.CH_004_05, "ch_004_05.gif");
		load(Viewer.CH_004_07, "ch_004_07.gif");
		load(Viewer.CH_004_15, "ch_004_15.gif");
		load(Viewer.CH_006_03, "ch_006_03.gif");
		load(Viewer.CH_006_15, "ch_006_15.gif");
		load(Viewer.CH_007_03, "ch_007_03.gif");
		load(Viewer.CH_007_15, "ch_007_15.gif");
		load(Viewer.CH_015_06, "ch_015_06.gif");
		load(Viewer.CH_177_06, "ch_177_06.gif");
	}

	// Load image n from GIF file s.
	private void load(int n, String s)  {
		Image i = (new ImageIcon(s)).getImage();
		int o = BufferedImage.TYPE_INT_ARGB;
		BufferedImage b = new BufferedImage(Cell.WIDTH, Cell.HEIGHT, o);
		b.getGraphics().drawImage(i, 0, 0, null);
		list[n] = b;
	}

	// Modify image map: let i be image constant.
	public void modify(int x, int y, int i)  { map[y][x] = i; }

	// Draw image at cell coords.
	public void update(int x, int y)  {
		Image i = list[map[y][x]];
		graphics.drawImage(i, Cell.WIDTH * x, Cell.HEIGHT * y, this);
	}

	// Redraw everything from the map.
	public void paint(Graphics g)  {
		if(graphics == null)
			graphics = getGraphics();
		for(int j = 0; j < HEIGHT; j++)
			for(int i = 0; i < WIDTH; i++)
				update(i, j);
	}

	// Report tile clicked to viewer.
	// Defined by contract with MouseListener.
	public void mousePressed(MouseEvent e)  {
		int x = e.getX() / Cell.WIDTH;
		int y = e.getY() / Cell.HEIGHT;
		viewer.select(x, y);
	}

	// Defined by contract with MouseListener.
	public void mouseClicked(MouseEvent e)  {  }
	public void mouseEntered(MouseEvent e)  {  }
	public void mouseExited(MouseEvent e)  {  }
	public void mouseReleased(MouseEvent e)  {  }

}
