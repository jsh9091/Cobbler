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

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Performs operations of adding hard coded line numbers to a Cobol file. 
 * @author jhorvath 
 */
public final class AddLineNumbersCmd extends AbstractLineNumberCmd {
	
	private String[] lines;
	private LineState lineState;
	private int increment;
	private String result = ""; 
	
	/**
	 * Constructor. 
	 * @param text String 
	 * @param lineState LineState
	 * @param increment int 
	 */
	public AddLineNumbersCmd(String text, LineState lineState, int increment) {
		this.lines = splitStringOnNewlines(text);
		this.lineState = lineState;
		this.increment = increment;
	}

	@Override
	public void perform() throws CobblerException {
		Debugger.printLog("Adding line numbers. increment: " + increment + " - " + lineState, this.getClass().getName());
		success = false;
		
		if (lineState == LineState.INDETERMINATE) {
			// TODO
			
		} else {
			doNumbering();
		}
		
		CobblerState.getInstance().setData(result);
		success = true;
	}
	
	/**
	 * Build hard coded line numbers. 
	 * This method replaces any characters in the first six columns of each line.
	 */
	private void doNumbering() {
		int counter = increment; 
		StringBuilder sb = new StringBuilder();
		
		for (String line : this.lines) {
			
			String formatted = String.format("%06d", counter);
			
			if (line.length() < LAST_NUM_COL || line.trim().isEmpty()) {
				sb.append(formatted);
				sb.append(System.lineSeparator());
			} else {
				line = formatted + line.substring(LAST_NUM_COL, line.length());
				sb.append(formatted);
				sb.append(line.substring(LAST_NUM_COL, line.length()));
				sb.append(System.lineSeparator());
			}
			
			counter = counter + increment;
		}
		
		this.result = sb.toString();
	}
	
	/**
	 * Returns the processed string. 
	 * @return String 
	 */
	public String getResult() {
		return result;
	}

}
