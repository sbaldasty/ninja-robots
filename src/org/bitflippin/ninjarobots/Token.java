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

import java.io.StreamTokenizer;

// Words read from scenario or program text areas.
// Generated by pressedParse in IDE.
// Used by interpreters to parse program or scenario.

public class Token  {

	// Token type constants.
	public static final int INVALID = 0;
	public static final int WORD = 1;
	public static final int NUMBER = 2;
	public static final int STRING = 3;
	public static final int BINARY_OPERATOR = 4;
	public static final int NOT_OPERATOR = 5;
	public static final int DOT_OPERATOR = 6;
	public static final int NEWLINE = 7;
	public static final int KEY_CONSTANT = 8;
	public static final int KEY_VARIABLE = 9;
	public static final int KEY_TYPE = 10;
	public static final int KEY_ARRAY = 11;
	public static final int KEY_SIZE = 12;
	public static final int KEY_END = 13;
	public static final int KEY_IF = 14;
	public static final int KEY_ELSE = 15;
	public static final int KEY_LET = 16;
	public static final int KEY_CHOOSE = 17;
	public static final int KEY_WHILE = 18;
	public static final int KEY_INVOKE = 19;
	public static final int KEY_FUNCTION = 20;
	public static final int KEY_ELSEIF = 21;
	public static final int ASSIGNMENT = 22;
	public static final int OPEN_PAREN = 23;
	public static final int CLOSE_PAREN = 24;
	public static final int OPEN_BRACKET = 25;
	public static final int CLOSE_BRACKET = 26;
	public static final int COMMA = 27;

	// Stream tokenizers do not support line counting.
	// EOL tokens must increment lineCounter.
	// Each new compilation should reset lineCounter.
	private static int lineCounter;
	public static void resetLineCounter()  { lineCounter = 0; }

	// Text that makes up this token.
	// Initialized by data from stream tokenizer.
	private String content;
	public String getContent()  { return content; }

	// Must be token type constant.
	// Initialized upon construction.
	private int type;
	public int getType()  { return type; }

	// Initialized by lineCounter upon processing.
	// Helps interpreter highlight problematic lines in IDE text area.
	private int line;
	public int getLine()  { return line; }

	// Create an EOL token.
	// This can be placed at the end of files.
	public Token()  {
		type = NEWLINE;
		content = "EOL";
		line = lineCounter;
		lineCounter++;
	}

	// Form token from just-read string on st.
	// Classify it and do other processing.
	public Token(StreamTokenizer st)  {
		if(st.ttype == '"' || st.ttype == StreamTokenizer.TT_WORD)
			content = st.sval;
		else
			content = (new Character((char)(st.ttype))).toString();
		line = lineCounter;
		if(st.ttype == StreamTokenizer.TT_EOL)  {
			type = NEWLINE;
			content = "EOL";
			lineCounter++;
		}
		else if(st.ttype == '"')
			type = STRING;
		else if(st.ttype == StreamTokenizer.TT_WORD)
			if(content.equals("array"))
				type = KEY_ARRAY;
			else if(content.equals("choose"))
				type = KEY_CHOOSE;
			else if(content.equals("constant"))
				type = KEY_CONSTANT;
			else if(content.equals("else"))
				type = KEY_ELSE;
			else if(content.equals("end"))
				type = KEY_END;
			else if(content.equals("function"))
				type = KEY_FUNCTION;
			else if(content.equals("if"))
				type = KEY_IF;
			else if(content.equals("invoke"))
				type = KEY_INVOKE;
			else if(content.equals("let"))
				type = KEY_LET;
			else if(content.equals("size"))
				type = KEY_SIZE;
			else if(content.equals("type"))
				type = KEY_TYPE;
			else if(content.equals("variable"))
				type = KEY_VARIABLE;
			else if(content.equals("while"))
				type = KEY_WHILE;
			else if(content.equals("elseif"))
				type = KEY_ELSEIF;
			else if(isNumber(content))
				type = NUMBER;
			else if(isWord(content))
				type = WORD;
			else
				type = INVALID;
		else if(st.ttype == ',')
			type = COMMA;
		else if(st.ttype == '!')
			type = NOT_OPERATOR;
		else if(st.ttype == '.')
			type = DOT_OPERATOR;
		else if(st.ttype == ':')
			type = ASSIGNMENT;
		else if(st.ttype == '(')
			type = OPEN_PAREN;
		else if(st.ttype == ')')
			type = CLOSE_PAREN;
		else if(st.ttype == '[')
			type = OPEN_BRACKET;
		else if(st.ttype == ']')
			type = CLOSE_BRACKET;
		else if(isBinary(st.ttype))
			type = BINARY_OPERATOR;
		else
			type = INVALID;
	}

	// Determine if s is of decimal number format, 0 <= s < 1000000.
	// Used in classification.
	private static boolean isNumber(String s)  {
		if(s.length() > 6)
			return false;
		for(int i = 0; i < s.length(); i++)  {
			char c = s.charAt(i);
			if(c < '0' || c > '9')
				return false;
		}
		return true;
	}

	// Determine if s is alphanumeric identifier.
	// Used in classification.
	private static boolean isWord(String s)  {
		char c = s.charAt(0);
		return c < '0' || c > '9';
	}

	// Determine if c is binary operator.
	// Used in classification.
	private static boolean isBinary(int c)  {
		return
			c == '\'' ||
			c == '+' ||
			c == '-' ||
			c == '*' ||
			c == '/' ||
			c == '%' ||
			c == '&' ||
			c == '|' ||
			c == '^' ||
			c == '=' ||
			c == '<' ||
			c == '>';
	}

}