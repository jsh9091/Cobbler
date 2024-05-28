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

import java.util.ArrayList;

import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Performs check on string for is the file appears to be numbered, not
 * numbered, or if the state of the file cannot be easily determined.
 * @author jhorvath
 */
public final class CheckLineNumberStateCmd extends AbstractLineNumberCmd {

	private String[] lines;
	private boolean indeterminate = false;
	private LineState lineState;
	
	/**
	 * Constructor. 
	 * @param text String
	 */
	public CheckLineNumberStateCmd(String text) {
		this.lines = splitStringOnNewlines(text);
	}
	
	@Override
	public void perform() throws CobblerException {
		Debugger.printLog("Checking hard coded line state of opened file.", this.getClass().getName());
		
		success = false;
		
		ArrayList<Boolean> spacesBooleanList = new ArrayList<>();
		ArrayList<Boolean> digitsBooleanList = new ArrayList<>();
		
		for (String line : lines) {
			
			if (line.contains("\t")) {
				indeterminate = true; 
			}
			
			if (line.length() >= LAST_NUM_COL) {
				// do the whitespace and digit tests
				boolean spaceline = isSixCharsForWhiteSpace(line);
				spacesBooleanList.add(spaceline);
				
				boolean digitsLine = isSixCharsForDigits(line);
				digitsBooleanList.add(digitsLine);
				
			} else {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				} else {
					// some strange condition has been found
					indeterminate = true;
				}
			}
			
			if (indeterminate) {
				break;
			}
		}
		
		// check lists for indeterminate state
		checkListsForIndeterminateState(spacesBooleanList, digitsBooleanList);
		
		// set result enumeration
		if (digitsBooleanList.contains(true)) {
			lineState = LineState.NUMBERED;
			
		} else if (spacesBooleanList.contains(true)) {
			lineState = LineState.NOT_NUMBERED;
			
		} 
		
		// if global indeterminate
		if (indeterminate) {
			lineState = LineState.INDETERMINATE;
		}
		
		success = true;
	}
	
	/**
	 * Looks at the first six chars of line to check if they are all word spaces or not.
	 * Returns true if the first six chars are wordspaces, otherwise returns false.
	 * 
	 * @param line String 
	 * @return boolean 
	 */
	private boolean isSixCharsForWhiteSpace(String line) {
		boolean firstSixColumnsWhiteSpace = true;
		char[] chars = line.toCharArray();
		
		for (int i = 0; i < LAST_NUM_COL; i++) {
			char c = chars[i];
			
			if (c != ' ') {
				firstSixColumnsWhiteSpace = false;
				
				// if the non-word space char is not a digit
				if (!Character.isDigit(c)) {
					this.indeterminate = true;
				}
				break;
			}
		}
		
		return firstSixColumnsWhiteSpace;
	}
	
	/**
	 * Looks at the first six chars of line to check if they are all digits or not. 
	 * Returns true if first six chars are digits, otherwise returns false.
	 * 
	 * @param line String 
	 * @return boolean 
	 */
	private boolean isSixCharsForDigits(String line) {
		boolean firstSixColumnsdigits = true;
		char[] chars = line.toCharArray();
				
		for (int i = 0; i < 6; i++) {
			char c = chars[i];
			
			// if the char is NOT a digit
			if (!Character.isDigit(c)) {
				firstSixColumnsdigits = false;
				
				// if the non-digit char is not a word space
				if (c != ' ') {
					this.indeterminate = true;
				}
				break;
			}
		}
		
		return firstSixColumnsdigits;
	}
	
	/**
	 * Checks that the given array lists do not both contain true values. 
	 * If both lists contain a true value, then state is indertiminate.
	 *  
	 * @param listA ArrayList<Boolean>
	 * @param listB ArrayList<Boolean>
	 */
	private void checkListsForIndeterminateState(ArrayList<Boolean> listA, ArrayList<Boolean> listB) {
		boolean foundTrueListA = false;
		boolean foundTrueListB = false;
		
		for (boolean b : listA) {
			if (b) {
				foundTrueListA = true;
				break;
			}
		}
		
		for (boolean b : listB) {
			if (b) {
				foundTrueListB = true;
				break;
			}
		}
		
		if (foundTrueListA && foundTrueListB ) {
			this.indeterminate = true;
		}
	}

	/**
	 * Get the line state result of the string given to command.
	 * @return LineState
	 */
	public LineState getLineState() {
		return lineState;
	}

}
