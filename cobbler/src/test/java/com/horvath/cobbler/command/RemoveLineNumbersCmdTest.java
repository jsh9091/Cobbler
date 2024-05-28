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

import org.junit.Assert;
import org.junit.Test;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Perform tests related to RemoveLineNumbersCmd class.
 * @author jhorvath
 */
public class RemoveLineNumbersCmdTest {
	
	private static final String LINEFEED_RETURN = "\n";
	private static final String WORD_SPACE = " ";
	private static final String WORD_SPACE_LINE = WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE;
	
	@Test
	public void perform_allLinesNumbered_linesRemoved() {
		StringBuilder sb = new StringBuilder();
		sb.append("000010* Auto generated hello world file");
		sb.append(LINEFEED_RETURN);
		sb.append("000020 IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000030 PROGRAM-ID. HELLO-WORLD.");
		sb.append(LINEFEED_RETURN);
		sb.append("000040 AUTHOR. jhorvath");
		sb.append(LINEFEED_RETURN);
		sb.append("000050 DATE-WRITTEN. 05/28/2024");
		sb.append(LINEFEED_RETURN);
		sb.append("000060 ENVIRONMENT DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000070 DATA DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000080 WORKING-STORAGE SECTION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000090");
		sb.append(LINEFEED_RETURN);
		sb.append("000100 01 MyName PIC X(30) VALUE \"jhorvath\".");
		sb.append(LINEFEED_RETURN);
		sb.append("000110");
		sb.append(LINEFEED_RETURN);
		sb.append("000120 PROCEDURE DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000130");
		sb.append(LINEFEED_RETURN);
		sb.append("000140 DISPLAY \"Hello, \" MyName.");
		sb.append(LINEFEED_RETURN);
		sb.append("000150");
		sb.append(LINEFEED_RETURN);
		sb.append("000160 STOP RUN.");
		sb.append(LINEFEED_RETURN);
		
		try {
			RemoveLineNumbersCmd cmd = new RemoveLineNumbersCmd(sb.toString());
			cmd.perform();
			
			Assert.assertTrue(cmd.isSuccess());
			
			final String result = cmd.getResult();
			
			Assert.assertTrue(result.equals(CobblerState.getInstance().getData()));
			
			// spot check numbers that should not appear
			Assert.assertFalse(result.contains("000010"));
			Assert.assertFalse(result.contains("000040"));
			Assert.assertFalse(result.contains("000120"));
			Assert.assertFalse(result.contains("000160"));
			
			// spot check full line contents
			Assert.assertTrue(result.contains(WORD_SPACE_LINE + "* Auto generated hello world file"));
			Assert.assertTrue(result.contains(WORD_SPACE_LINE + WORD_SPACE + "IDENTIFICATION DIVISION."));
			Assert.assertTrue(result.contains(WORD_SPACE_LINE + WORD_SPACE + "DATE-WRITTEN. 05/28/2024"));
			Assert.assertTrue(result.contains(WORD_SPACE_LINE + WORD_SPACE + "WORKING-STORAGE SECTION."));
			Assert.assertTrue(result.contains(WORD_SPACE_LINE + WORD_SPACE + "PROCEDURE DIVISION."));
			Assert.assertTrue(result.contains(WORD_SPACE_LINE + WORD_SPACE + "STOP RUN."));

			Assert.assertTrue(cmd.getMessage().isEmpty());
			
		} catch (CobblerException ex) {
			Assert.fail();
		}
	}

	
	@Test
	public void perform_someLinesNotNumbered_messageWithSkippedCount() {
		StringBuilder sb = new StringBuilder();
		// 4 lines of test code
		sb.append("000010* Auto generated hello world file");
		sb.append(LINEFEED_RETURN);
		// put in 3 lines that should be skipped
		sb.append(WORD_SPACE_LINE);
		sb.append(WORD_SPACE);
		sb.append("IDENTIFICATION DIVISION."); // spaces where numbers would be
		sb.append(LINEFEED_RETURN);
		sb.append("0H0030 PROGRAM-ID. HELLO-WORLD."); // letter in a number column
		sb.append(LINEFEED_RETURN);
		sb.append("AUTHOR. jhorvath"); // statement starting all the way at beginning of line
		sb.append(LINEFEED_RETURN);
		
		try {
			RemoveLineNumbersCmd cmd = new RemoveLineNumbersCmd(sb.toString());
			cmd.perform();
			
			Assert.assertTrue(cmd.isSuccess());
			
			final String result = cmd.getResult();
			
			Assert.assertTrue(result.equals(CobblerState.getInstance().getData()));
			
			// spot check numbers that should not appear
			Assert.assertFalse(result.contains("000010"));
			
			// spot check full line contents
			Assert.assertTrue(result.contains(WORD_SPACE_LINE + "* Auto generated hello world file"));


			Assert.assertFalse(cmd.getMessage().isEmpty());
			Assert.assertEquals(3 + RemoveLineNumbersCmd.SKIP_COUNT_MESSAGE, cmd.getMessage());
			
		} catch (CobblerException ex) {
			Assert.fail();
		}
	}

}
