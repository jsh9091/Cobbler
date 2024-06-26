/*
 * MIT License
 * 
 * Copyright (c) 2024 Joshua Horvath
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.horvath.cobbler.command;

public abstract class AbstractLineNumberCmd extends CobblerCommand {
	
	/**
	 * In Cobol line numbers take up the first 6 columns of a row. Comment
	 * indicators are in column 7, and statements (Areas A & B) start at column 8.
	 */
	protected final static int LAST_NUM_COL = 6;

	public enum LineState {
		NUMBERED,
		NOT_NUMBERED,
		INDETERMINATE
	}

	/**
	 * Splits the given string on line returns and returns an array of the individual lines.
	 * 
	 * @param string String
	 * @return String[]
	 */
	public static String[] splitStringOnNewlines(String string) {
		return string.split("\\r?\\n|\\r");
	}
	
	/**
	 * Checks if the first six characters are all digits. Only returns true if
	 * the string is 6 or more characters in length and the first 6 are digits.
	 * 
	 * @param line String
	 * @return boolean
	 */
	protected boolean firstSixAllDigit(String line) {
		boolean result = false;

		if (line.length() >= LAST_NUM_COL) {
			char[] chars = line.toCharArray();
			if (Character.isDigit(chars[0]) && Character.isDigit(chars[1]) && Character.isDigit(chars[2])
					&& Character.isDigit(chars[3]) && Character.isDigit(chars[4]) && Character.isDigit(chars[5])) {
				result = true;
			}
		}

		return result;
	}
}
