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

// Pieces that make up the map of a scenario.
// Created when scenario is parsed.

public class Cell  {

	// Dimensions of images in pixels.
	public static final int HEIGHT = 16;
	public static final int WIDTH = 16;

	// Directional constants.
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	// Find cell in direction d.
	// Easily talk between two adjacent cells of arbitrary alignment.
	public Cell neighbor(int d)  {
		switch(d)  {
			case NORTH: return scenario.getCell(x, y - 1);
			case EAST:  return scenario.getCell(x + 1, y);
			case SOUTH: return scenario.getCell(x, y + 1);
			case WEST:  return scenario.getCell(x - 1, y);
			default:    return null;
		}
	}

	// Link to scenario of which its map is part.
	// Initialized by constructor.
	private Scenario scenario;

	// Whether cell is wall.
	// All cells either wall or open.
	public boolean isWall()  { return wall; }
	private boolean wall;

	// Coordinates on map.
	// Initialized by constructor, never changed.
	public int getX()  { return x; }
	private int x;
	public int getY()  { return y; }
	private int y;

	// Generate from map data c, and occupy it as c requires.
	// Let myX and myY be coordinates on the map.
	public Cell(NinjaRobots f, int myX, int myY, char c)  {
		wall = false;
		scenario = f.scenario;
		x = myX;
		y = myY;
		switch(c)  {
			case '.':   // Space - do nothing.
				break;
			case '#':   // Wall - set wall.
				wall = true;
				break;
			case 'd':   // Diamond - place here.
				occupant = new Treasure(f, this);
				break;
			case 'b':   // Blue robot.
				occupant = new Robot(f, this, f.team[2]);
				f.scenario.getActiveStarters().add(occupant);
				break;
			case 'g':   // Green robot.
				occupant = new Robot(f, this, f.team[1]);
				f.scenario.getActiveStarters().add(occupant);
				break;
			case 'r':   // Red robot.
				occupant = new Robot(f, this, f.team[0]);
				f.scenario.getActiveStarters().add(occupant);
				break;
			case 'p':   // Purple robot.
				occupant = new Robot(f, this, f.team[3]);
				f.scenario.getActiveStarters().add(occupant);
				break;
			case 'u':   // Unowned robot.
				occupant = new Robot(f, this, null);
				break;
			case 's':   // Item.
			case 'k':
				occupant = new Item(f, this, c);
				break;
			default:   // Producer.
				occupant = new Producer(f, this, c);
				f.scenario.getActiveStarters().add(occupant);
		}
	}

	// Any occupant that may be in this cell.
	// Occupant may be null.
	public Occupant getOccupant()  { return occupant; }
	private Occupant occupant;
	public void setOccupant(Occupant o)  { occupant = o; }

	// Code for picture that represents cell.
	// If occupied, returns occupant image code.
	// Used by viewer to update the canvas.
	public int image()  {
		if(wall) return Viewer.CH_177_06;
		if(occupant == null) return Viewer.CH_000_00;
		return occupant.image();
	}

	// Return base constant for occupant type or wall.
	public int queryOccupant()  {
		if(wall)
			return BaseConstants.WALL;
		else if(occupant == null)
			return BaseConstants.NOBODY;
		else if(occupant.isRobot())
			return BaseConstants.ROBOT;
		else if(occupant.isProducer())
			return BaseConstants.PRODUCER;
		else if(occupant.isTreasure())
			return BaseConstants.TREASURE;
		else if(occupant.isHealthItem())
			return BaseConstants.HEALTHITEM;
		return BaseConstants.POWERITEM;
	}

	// Return base constant for team.
	public int queryTeam() throws ExecutionException  {
		if(occupant == null)
			throw new ExecutionException("query for team on non-robot / non-treasure.");
		else if(occupant.isTreasure())  {
			Treasure t = (Treasure)(occupant);
			if(t.getTeam() == null)
				return BaseConstants.UNOWNED;
			return t.getTeam().getNumber() + BaseConstants.RED;
		}
		else if(occupant.isRobot())  {
			Robot r = (Robot)(occupant);
			if(r.team == null)
				return BaseConstants.UNOWNED;
			return r.team.getNumber() + BaseConstants.RED;
		}
		throw new ExecutionException("query for team on non-robot / non-treasure.");
	}

	// Return base constant for health.
	public int queryHealth() throws ExecutionException  {
		if(occupant == null)
			throw new ExecutionException("query for health on non-robot.");
		else if(occupant.isRobot())  {
			Robot r = (Robot)(occupant);
			return r.getHealth();
		}
		throw new ExecutionException("query for health on non-robot.");
	}

	// Return last spoken word of any robot here.
	public int queryWord() throws ExecutionException  {
		if(occupant == null || !occupant.isRobot()) throw new ExecutionException("query for word on non-robot.");
		Robot r = (Robot)(occupant);
		return r.getWord();
	}

}
