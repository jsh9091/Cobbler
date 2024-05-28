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
import com.horvath.cobbler.command.AbstractLineNumberCmd.LineState;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Perform tests related to AddLineNumbersCmd class.
 * @author jhorvath
 */
public class AddLineNumbersCmdTest {
	
	private static final String LINEFEED_RETURN = "\n";
	private static final String WORD_SPACE = " ";
	private static final String WORD_SPACE_LINE = WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE;

	@Test
	public void perform_allWordSpaces_numbered() {
		StringBuilder sb = new StringBuilder();
		sb.append(WORD_SPACE_LINE);
		sb.append(WORD_SPACE); // column 7 - comment marker column
		sb.append("IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		
		sb.append(WORD_SPACE_LINE);
		sb.append(WORD_SPACE); // column 7 - comment marker column
		sb.append("DATE-WRITTEN. May 26, 2024");
		sb.append(LINEFEED_RETURN);
		
		try {
			AddLineNumbersCmd cmd = new AddLineNumbersCmd(sb.toString(), LineState.NOT_NUMBERED, 30);
			cmd.perform();
			
			Assert.assertTrue(cmd.isSuccess());
			Assert.assertTrue(cmd.getResult().contains("000030 "));
			Assert.assertTrue(cmd.getResult().contains("000060 "));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000030 "));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000060 "));
			
		} catch (CobblerException ex) {
			Assert.fail();
		}
	}

	@Test
	public void perform_allNumbered_renumbered() {
		StringBuilder sb = new StringBuilder();
		sb.append("000010 IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		
		sb.append("000020 DATE-WRITTEN. May 26, 2024");
		sb.append(LINEFEED_RETURN);
		
		try {
			AddLineNumbersCmd cmd = new AddLineNumbersCmd(sb.toString(), LineState.NUMBERED, 20);
			cmd.perform();
			
			Assert.assertTrue(cmd.isSuccess());
			Assert.assertFalse(cmd.getResult().contains("000010"));
			Assert.assertTrue(cmd.getResult().contains("000020"));
			Assert.assertTrue(cmd.getResult().contains("000040"));
			Assert.assertFalse(CobblerState.getInstance().getData().contains("000010"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000020"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000040"));
			
		} catch (CobblerException ex) {
			Assert.fail();
		}
	}

	@Test
	public void perform_someSpaceLinesSomeNumberedLines_completelyNumberedRenumbered() {
		StringBuilder sb = new StringBuilder();
		sb.append("000010 IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		
		sb.append(WORD_SPACE_LINE);
		sb.append(WORD_SPACE); // column 7 - comment marker column
		sb.append("DATE-WRITTEN. May 26, 2024");
		sb.append(LINEFEED_RETURN);

		sb.append("000030 IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		
		try {
			AddLineNumbersCmd cmd = new AddLineNumbersCmd(sb.toString(), LineState.INDETERMINATE, 50);
			cmd.perform();
			
			Assert.assertTrue(cmd.isSuccess());
			Assert.assertFalse(cmd.getResult().contains("000010"));
			Assert.assertFalse(cmd.getResult().contains("000030"));
			Assert.assertTrue(cmd.getResult().contains("000050 "));
			Assert.assertTrue(cmd.getResult().contains("000100 "));
			Assert.assertTrue(cmd.getResult().contains("000150 "));
			Assert.assertFalse(CobblerState.getInstance().getData().contains("000010"));
			Assert.assertFalse(CobblerState.getInstance().getData().contains("000030"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000050 "));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000100 "));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000150 "));
			
		} catch (CobblerException ex) {
			Assert.fail();
		}
	}

	@Test
	public void perform_unexpectedCharacteres_pushedAsideAndNumbered() {
		StringBuilder sb = new StringBuilder();
		sb.append("    H  IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);

		sb.append(WORD_SPACE_LINE);
		sb.append(WORD_SPACE); // column 7 - comment marker column
		sb.append("DATE-WRITTEN. May 26, 2024");
		sb.append(LINEFEED_RETURN);
		
		try {
			AddLineNumbersCmd cmd = new AddLineNumbersCmd(sb.toString(), LineState.INDETERMINATE, 20);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertTrue(cmd.getResult().contains("000020"));
			Assert.assertTrue(cmd.getResult().contains("000040"));
			Assert.assertTrue(cmd.getResult().contains("000020 H  IDENTIFICATION DIVISION."));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000020"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000040"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000020 H  IDENTIFICATION DIVISION."));
			
		} catch (CobblerException ex) {
			Assert.fail();
		}
	}

	@Test 
	public void perform_unexpectedCommentIndicator_pushedAsidedToCommentCol() {
		StringBuilder sb = new StringBuilder();
		sb.append("  * Check the user entered a valid operator");
		sb.append(LINEFEED_RETURN);

		sb.append(WORD_SPACE_LINE);
		sb.append(WORD_SPACE); // column 7 - comment marker column
		sb.append("IF Operator IS NOT = \"+\"");
		sb.append(LINEFEED_RETURN);
		
		try {
			AddLineNumbersCmd cmd = new AddLineNumbersCmd(sb.toString(), LineState.INDETERMINATE, 20);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertTrue(cmd.getResult().contains("000020"));
			Assert.assertTrue(cmd.getResult().contains("000040"));
			Assert.assertTrue(cmd.getResult().contains("000020* Check the user entered a valid operator"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000020"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000040"));
			Assert.assertTrue(CobblerState.getInstance().getData().contains("000020* Check the user entered a valid operator"));
			
		} catch (CobblerException ex) {
			Assert.fail();
		}
	}

}
