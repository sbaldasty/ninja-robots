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

public class Robot extends Occupant implements TunitWatcher  {

	// Whether or not robot is selected.
	// Selected robot appears yellow on the map.
	public void setSelected(boolean v)  { selected = v; }
	private boolean selected = false;

	// How much health robot has to start.
	// Health items bring health to this amount.
	public static final int MAX_HEALTH = 20;

	// When 0, robot dies.
	public int getHealth()  { return health; }
	private int health = MAX_HEALTH;

	// Memory bank.
	public MemoryBank getMemory()  { return memory; }
	private MemoryBank memory = new MemoryBank();

	// What team robot is on - may be null.
	// May be set by other robots if null.
	public Team team;

	// What robot is carrying - may be null.
	private Occupant item;

	// Word robot has most recently whispered.
	// Other robots may hear the word and respond to it.
	public int getWord()  { return word; }
	private int word = 0;

	// Extra strength from an item.
	private int powerBoost = 0;

	// How many tunits until next turn.
	// Visible to interpreter to see if took turn yet.
	public int getWait()  { return wait; }
	protected int wait = 2;

	// Called when a tunit passes.
	// Wait until next turn reduced, or next turn taken.
	// Defined by contract with TunitWatcher.
	public void timeElapses()  {
		if(--wait == 0)  {
			Program p = team.getProgram();
			try  { if(p != null) p.execute(this); }
			catch(ExecutionException e)  { kill(e.getError()); }
		}
		if(wait == 0) wait = 8;
	}

	// Defined by contract with TunitWatcher.
	public boolean isActive()  { return health > 0; }

	// Initialize team and clear permanent memory.
	public Robot(NinjaRobots f, Cell c, Team t)  {
		super(f, c);
		team = t;
		if(team != null)
			t.addMember(this);
		for(int i = 0; i < MemoryBank.PERMANENT_SIZE; i++)
			try  { memory.write(i, -1); }
			catch(ExecutionException e)  {
				System.out.println("Internal error 0002.");
				System.exit(0);
			}
	}

	// Eliminate this robot and dump death message s.
	public void kill(String s)  {
		health = 0;
		team.removeMember(this);
		flash(true);
		wait = 2;
		frame.simulation.report("(" + cell.getX() + "," + cell.getY() + "): dies of " + s);
	}

	// Called when robot suffers damage dmg.
	public void attack(int dmg)  {
		health -= dmg;
		if(health <= 0)
			kill("being attacked");
		else
			flash(false);
	}

	// Access to class type.
	// Defined by contract with Occupant.
	public boolean isRobot()  { return true; }
	public boolean isTreasure()  { return false; }
	public boolean isHealthItem()  { return false; }
	public boolean isPowerItem()  { return false; }
	public boolean isProducer()  { return false; }

	// Can be carried by robots.
	// Defined by contract with Occupant.
	public boolean portable()  { return false; }

	// Depends on various factors.
	// Defined by contract with occupant.
	public int image()  {
		if(hit)
			if(item == null) return Viewer.CH_001_15;
			else return Viewer.CH_002_15;
		if(team == null)
			return Viewer.CH_001_07;
		if(selected)
			if(item == null) return Viewer.CH_001_14;
			else return Viewer.CH_002_14;
		int k = (item == null) ? 0 : 4;
		switch(k + team.getNumber())  {
			case 0: return Viewer.CH_001_04;
			case 1: return Viewer.CH_001_02;
			case 2: return Viewer.CH_001_03;
			case 3: return Viewer.CH_001_05;
			case 4: return Viewer.CH_002_04;
			case 5: return Viewer.CH_002_02;
			case 6: return Viewer.CH_002_03;
			case 7: return Viewer.CH_002_05;
		}
		return -1; // Never gets here.
	}

	// Attempt to move in direction d.
	// Only works on non-occupied non-wall.
	// If treasure or unowned robot in way, claim it for team.
	public void actionMove(int d) throws ExecutionException  {
		Cell c = cell.neighbor(d);
		if(c == null) throw new ExecutionException("moving with an invalid direction (" + d + BaseConstants.NORTH + ").");
		if(c.isWall()) throw new ExecutionException("moving onto an occupied space (" + d + BaseConstants.NORTH + ").");
		if(c.getOccupant() == null)  {
			cell.setOccupant(null);
			c.setOccupant(this);
			frame.simulation.getViewer().update(cell.getX(), cell.getY());
			cell = c;
			frame.simulation.getViewer().update(cell.getX(), cell.getY());
		}
		else if(c.getOccupant().isRobot())  {
			Robot r = (Robot)(c.getOccupant());
			if(r.team == null)  {
				r.team = team;
				team.addMember(r);
				frame.simulation.addTunitWatcher(r);
				r.flash(false);
			}
			else
				throw new ExecutionException("moving onto an occupied space (" + d + BaseConstants.NORTH + ").");
		}
		else if(c.getOccupant().isTreasure())  {
			Treasure t = (Treasure)(c.getOccupant());
			t.claimFor(team);
		}
		else
			throw new ExecutionException("moving onto an occupied space (" + d + BaseConstants.NORTH + ").");
		wait = 8;
	}

	// Whisper word w in direction d.
	public void actionWhisper(int w) throws ExecutionException  {
		word = w;
		wait = 2;
	}

	// Throw strike in direction d.
	public void actionAttack(int d) throws ExecutionException  {
		Cell c = cell.neighbor(d);
		if(c == null) throw new ExecutionException("attacking with an invalid direction (" + d + BaseConstants.NORTH + ").");
		if(c.isWall()) throw new ExecutionException("attacking an invincible target (" + d + BaseConstants.NORTH + ")");
		if(c.getOccupant() == null) throw new ExecutionException("attacking empty space (" + d + BaseConstants.NORTH + ")");
		if(c.getOccupant().isProducer()) throw new ExecutionException("attacking an invincible target (" + d + BaseConstants.NORTH + ")");
		if(c.getOccupant().isRobot())  {
			Robot r = (Robot)(c.getOccupant());
			r.attack(frame.scenario.getViolenceLevel() + powerBoost);
		}
		else
			c.getOccupant().flash(true);
		wait = 3;
		powerBoost = 0;
	}

	// Take any item in direction d.
	public void actionTake(int d) throws ExecutionException  {
		Cell c = cell.neighbor(d);
		if(c == null)
			throw new ExecutionException("taking with an invalid direction (" + d + BaseConstants.NORTH + ").");
		if(item != null)
			throw new ExecutionException("taking while already holding an item (" + d + BaseConstants.NORTH + ").");
		if(c.getOccupant() == null || !c.getOccupant().portable())
			throw new ExecutionException("taking a non-item / non-treasure (" + d + BaseConstants.NORTH + ").");
		item = c.getOccupant();
		c.setOccupant(null);
		frame.simulation.getViewer().update(c.getX(), c.getY());
		frame.simulation.getViewer().update(cell.getX(), cell.getY());
		wait = 6;
	}

	// Drop currently held item toward direction d.
	// Cell in d must be unoccupied unless item of type 's' or 'k'.
	// Then cell may be occupied by robot.
	public void actionDrop(int d) throws ExecutionException  {
		Cell c = cell.neighbor(d);
		if(c == null) throw new ExecutionException("dropping with an invalid direction (" + d + BaseConstants.NORTH + ").");
		if(item == null) throw new ExecutionException("trying to drop without holding an item (" + d + BaseConstants.NORTH + ").");
		if(c.isWall()) throw new ExecutionException("dropping onto an occupied space (" + d + BaseConstants.NORTH + ").");
		if(c.getOccupant() == null)  {
			c.setOccupant(item);
			item = null;
			frame.simulation.getViewer().update(cell.getX(), cell.getY());
			frame.simulation.getViewer().update(c.getX(), c.getY());
		}
		else if(c.getOccupant().isRobot())  {
			if(item.isHealthItem())  {
				health = MAX_HEALTH;
				item = null;
				frame.simulation.getViewer().update(cell.getX(), cell.getY());
			}
			else if(item.isPowerItem())  {
				powerBoost++;
				item = null;
				frame.simulation.getViewer().update(cell.getX(), cell.getY());
			}
			else
				throw new ExecutionException("dropping onto an occupied space (" + d + BaseConstants.NORTH + ").");
		}
		else
			throw new ExecutionException("dropping onto an occupied space (" + d + BaseConstants.NORTH + ").");
		wait = 3;
	}

}
