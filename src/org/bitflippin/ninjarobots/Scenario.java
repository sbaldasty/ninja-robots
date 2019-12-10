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
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.JTextField;

// IDE's for scenarios.
// Generate scenario from information in input area when parse is pressed.
// Also generate the map if successful.

public class Scenario extends IDE  {

	// Attributes from specs file.
	private int mapHeight;
	private int mapWidth;
	private int timeLimit;
	private int violenceLevel;
	private int winningScore;
	private int robotPoints;
	private int treasurePoints;

	// Access to attributes.
	public int getMapHeight()  { return mapHeight; }
	public int getMapWidth()  { return mapWidth; }
	public int getTimeLimit()  { return timeLimit; }
	public int getViolenceLevel()  { return violenceLevel; }
	public int getWinningScore()  { return winningScore; }
	public int getRobotPoints()  { return robotPoints; }
	public int getTreasurePoints()  { return treasurePoints; }

	// Defined by contract with IDE.
	protected String header()  { return "Scenario editor:"; }

	// Iterator over tokens.
	private ListIterator iterator;

	// Latest error message.
	// Report this if an error happens.
	private String message;

	// Link to begin button on setup screen.
	// Needed to enable and disable.
	private JButton beginButton;

	// Map data tokens.
	private Token data[];

	// Map and access to its cells.
	public Cell getCell(int x, int y)  { return map[y][x]; }
	private Cell map[][];

	// Let l be label on setup screen to show file name.
	// Let b be begin button on setup screen.
	public Scenario(NinjaRobots f, JTextField t, JButton b)  {
		super(f, t);
		beginButton = b;
	}

	// Called whenever tokenization failed.
	// Defined by contract with IDE.
	protected void badToken()  { if(beginButton != null) beginButton.setEnabled(false); }

	// Get scenario attributes from token list.
	// Defined by contract with IDE.
	protected void parse() throws InterpreterException  {
		if(beginButton != null) beginButton.setEnabled(false);
		iterator = tokens.listIterator();
		if(!iterator.hasNext())
			throw new InterpreterException("Load or type scenario specifications.", null);
		mapHeight = parm("HEIGHT", 4, 255);
		mapWidth = parm("WIDTH", 4, 255);
		timeLimit = parm("TIME", 1, 999999);
		violenceLevel = parm("VIOLENCE", 0, 255);
		winningScore = parm("WIN", 1, 999999);
		robotPoints = parm("ROBOT", 0, 999999);
		treasurePoints = parm("TREASURE", 0, 999999);
		data = new Token[mapHeight];
		for(int i = 0; i < mapHeight; i++)  {
			message = "Append " + (mapHeight - i) + " lines of map data.";
			requireEOL();
			Token t = nextToken();
			while(t.getType() == Token.NEWLINE)
				t = nextToken();
			if(t.getType() != Token.STRING)
				throw new InterpreterException("A string literal containing map data belongs here.", t);
			data[i] = t;
		}
		requireEOF();
		checkLengths();
		checkContent();
		message = "The map must be walled off on the top.";
		checkWall(data[0]);
		checkSides();
		checkWinner('r');
		checkWinner('b');
		checkWinner('g');
		checkWinner('p');
		message = "The map must be walled off on the bottom.";
		checkWall(data[mapHeight - 1]);
		if(beginButton != null) beginButton.setEnabled(true);
	}

	// Get the next token from the list iterator.
	// If it does not exist, terminate.
	private Token nextToken() throws InterpreterException  {
		if(!iterator.hasNext())
			throw new InterpreterException(message, null);
		return (Token)(iterator.next());
	}

	// Require list contains no more significant tokens.
	private void requireEOF() throws InterpreterException  {
		while(iterator.hasNext())  {
			Token t = nextToken();
			if(t.getType() != Token.NEWLINE)
				throw new InterpreterException("End of file expected.", t);
		}
	}

	// Read the next token.
	// Require that it is of type NEWLINE.
	private void requireEOL() throws InterpreterException  {
		Token t = nextToken();
		if(t.getType() != Token.NEWLINE)
			throw new InterpreterException("Extra characters on line.", t);
	}

	// Read parameter name and value from token list.
	private int parm(String n, int min, int max) throws InterpreterException  {
		message = "The keyword " + n + " must begin this line.";
		if(iterator.hasPrevious())
			requireEOL();
		Token t = nextToken();
		while(t.getType() == Token.NEWLINE)
			t = nextToken();
		if(t.getType() != Token.WORD || !t.getContent().equals(n))
			throw new InterpreterException(message, t);
		message = "Follow " + n + " with a numeric value between " + min + " and " + max + ".";
		t = nextToken();
		if(t.getType() != Token.NUMBER)
			throw new InterpreterException(message, t);
		int v = Integer.parseInt(t.getContent());
		if(v < min || v > max)
			throw new InterpreterException(message, t);
		return v;
	}

	// Make sure all map data have correct length.
	private void checkLengths() throws InterpreterException  {
		for(int i = 0; i < mapHeight; i++)  {
			int l = data[i].getContent().length();
			if(l < mapWidth)
				throw new InterpreterException("Append " + (mapWidth - l) + " more cell(s) to this row of map data.", data[i]);
			else if(l > mapWidth)
				throw new InterpreterException("This row of map data has " + (l - mapWidth) + " too many cell(s).", data[i]);
		}
	}

	// Make sure map data contains only legal characters.
	private void checkContent() throws InterpreterException  {
		String v = ".#dbgrpuskDUSK";
		for(int i = 0; i < mapHeight; i++)
			for(int j = 0; j < mapWidth; j++)  {
				String s = data[i].getContent();
				if(v.indexOf(s.charAt(j)) == -1)
					throw new InterpreterException("Invalid cell type '" + s.substring(j, j + 1) + "'.", data[i]);
			}
	}

	// Make sure this line is all walls.
	private void checkWall(Token t) throws InterpreterException  {
		String s = t.getContent();
		for(int i = 0; i < mapWidth; i++)
			if(s.charAt(i) != '#')
				throw new InterpreterException(message, t);
	}

	// Make sure the first and last cells of each line are walls.
	private void checkSides() throws InterpreterException  {
		for(int i = 0; i < mapHeight; i++)  {
			if(data[i].getContent().charAt(0) != '#')
				throw new InterpreterException("This row of map data must begin with a wall.", data[i]);
			if(data[i].getContent().charAt(mapWidth - 1) != '#')
				throw new InterpreterException("This row of map data must end with a wall.", data[i]);
		}
	}

	// Make sure no team wins the game instantly.
	private void checkWinner(char c) throws InterpreterException  {
		int score = 0;
		for(int y = 0; y < mapHeight; y++)  {
			String s = data[y].getContent();
			for(int x = 0; x < mapWidth; x++)
				if(s.charAt(x) == c)
					score += robotPoints;
		}
		if(score >= winningScore)
			throw new InterpreterException("Too many robots of type '" + (new Character(c)).toString() + "'; reduce the number or value of robots, or raise the winning score.", null);
	}

	// Reconstruct map.  Called when begin is pressed.
	public void reset()  {
		map = new Cell[mapHeight][mapWidth];
		activeStarters = new LinkedList();
		for(int y = 0; y < mapHeight; y++)  {
			String s = data[y].getContent();
			for(int x = 0; x < mapWidth; x++)
				map[y][x] = new Cell(frame, x, y, s.charAt(x));
		}
	}

	// List of robots and producers.
	public LinkedList getActiveStarters()  { return activeStarters; }
	private LinkedList activeStarters;

}
