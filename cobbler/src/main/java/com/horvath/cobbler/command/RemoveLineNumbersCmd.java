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
 * Command for removing hard coded line numbers from Cobol files. 
 * @author jhorvath
 */
public final class RemoveLineNumbersCmd extends AbstractLineNumberCmd {
	
	private String[] lines;
	private int skipCount;
	private String result = ""; 
	protected final static String SKIP_COUNT_MESSAGE = " lines were skipped in line number removal.";
	
	/**
	 * Constructor. 
	 * @param text String 
	 * @param lineState LineState 
	 */
	public RemoveLineNumbersCmd(String text) {
		this.lines = splitStringOnNewlines(text);
		this.skipCount = 0;
	}

	@Override
	public void perform() throws CobblerException {
		Debugger.printLog("Removing line numbers. ", this.getClass().getName());
		success = false;
		
		removeNumbering();
		
		this.message = this.skipCount + SKIP_COUNT_MESSAGE;
		
		CobblerState.getInstance().setData(result);
		success = true;
	}
	
	/**
	 * Remove the line numbers. 
	 */
	private void removeNumbering() {
		StringBuilder sb = new StringBuilder();
		
		for (String line : this.lines) {
			
			if (firstSixAllDigit(line)) {
				final String sixSpaces = "      ";
				sb.append(sixSpaces);
				sb.append(line.substring(LAST_NUM_COL, line.length()));
				sb.append(System.lineSeparator());
				
			} else {
				this.skipCount++;
				sb.append(line);
				sb.append(System.lineSeparator());
			}

		}
		
		this.result = sb.toString();
	}

	public String getResult() {
		return result;
	}
}
