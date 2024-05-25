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

import com.horvath.cobbler.command.AbstractLineNumberCmd.LineState;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Perform tests related to CheckLineNumberStateCmd class.
 * @author jhorvath
 */
public final class CheckLineNumberStateCmdTest {
	
	private static final String CARRIAGE_RETURN = "\r";
	private static final String LINEFEED_RETURN = "\n";
	private static final String CR_LF = CARRIAGE_RETURN + LINEFEED_RETURN;
	private static final String WORD_SPACE = " ";
	private static final String WORD_SPACE_LINE = WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE + WORD_SPACE;

	@Test 
	public void splitStringOnNewlines_carriageReturns_splitCorrect() {
		StringBuilder sb = new StringBuilder();
		sb.append("line A");
		sb.append(CARRIAGE_RETURN);
		sb.append("line B");
		sb.append(CARRIAGE_RETURN);
		sb.append("line C");
		sb.append(CARRIAGE_RETURN);
		sb.append("line D");
		sb.append(CARRIAGE_RETURN);
		
		String[] actual = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());
		Assert.assertEquals(4, actual.length);
	}
	
	@Test
	public void splitStringOnNewlines_lineFeedReturns_splitCorrect() {
		StringBuilder sb = new StringBuilder();
		sb.append("line 1");
		sb.append(LINEFEED_RETURN);
		sb.append("line 2");
		sb.append(LINEFEED_RETURN);
		sb.append("line 3");
		sb.append(LINEFEED_RETURN);
		sb.append("line 4");
		sb.append(LINEFEED_RETURN);
		
		String[] actual = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());

		Assert.assertEquals(4, actual.length);
	}
	
	@Test
	public void splitStringOnNewlines_crLf_splitCorrect() {
		StringBuilder sb = new StringBuilder();
		sb.append("line A1");
		sb.append(CR_LF);
		sb.append("line B2");
		sb.append(CR_LF);
		sb.append("line C3");
		sb.append(CR_LF);
		sb.append("line D4");
		sb.append(CR_LF);
		
		String[] actual = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());

		Assert.assertEquals(4, actual.length);
	}
	
	@Test
	public void splitStringOnNewlines_mixedReturns_splitCorrect() {
		StringBuilder sb = new StringBuilder();
		sb.append("line A-1");
		sb.append(LINEFEED_RETURN);
		sb.append("line B-2");
		sb.append(CARRIAGE_RETURN);
		sb.append("line C-3");
		sb.append(CR_LF);
		sb.append("line D-4");
		sb.append(CR_LF);
		
		String[] actual = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());

		Assert.assertEquals(4, actual.length);
	}
	
	@Test
	public void splitStringOnNewlines_noReturns_oneEntryArrayReturned() {
		String oneLine = "this string only has one line";
		
		String[] actual = AbstractLineNumberCmd.splitStringOnNewlines(oneLine);
		
		Assert.assertEquals(1, actual.length);
	}
	
	@Test
	public void splitStringOnNewlines_emptyString_oneEntryArrayReturned() {
		
		String[] actual = AbstractLineNumberCmd.splitStringOnNewlines("");
		
		Assert.assertEquals(1, actual.length);
	}
	
	@Test
	public void perform_wordspaces_linestateNotNumbered() {
		StringBuilder sb = new StringBuilder();
		sb.append(WORD_SPACE_LINE);
		sb.append(" IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append(WORD_SPACE_LINE);
		sb.append(" DATE-WRITTEN. May 25, 2024");
		sb.append(LINEFEED_RETURN);
		
		String[] lines = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());
		
		 try {
			 CheckLineNumberStateCmd cmd = new CheckLineNumberStateCmd(lines);
			 cmd.perform();
			 
			 Assert.assertTrue(cmd.isSuccess());
			 Assert.assertEquals(LineState.NOT_NUMBERED, cmd.getLineState());
			 
		 } catch (CobblerException ex) {
			 Assert.fail();
		 }
	}
	
	@Test
	public void perform_lineNumbers_linestateNotNumbered() {
		StringBuilder sb = new StringBuilder();
		sb.append("000010 IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000020 DATE-WRITTEN. May 25, 2024");
		sb.append(LINEFEED_RETURN);
		
		String[] lines = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());
		
		 try {
			 CheckLineNumberStateCmd cmd = new CheckLineNumberStateCmd(lines);
			 cmd.perform();
			 
			 Assert.assertTrue(cmd.isSuccess());
			 Assert.assertEquals(LineState.NUMBERED, cmd.getLineState());
			 
		 } catch (CobblerException ex) {
			 Assert.fail();
		 }
	}
	
	@Test
	public void perform_mixedNumsSpaces_linestateIndeterminate() {
		StringBuilder sb = new StringBuilder();
		sb.append(WORD_SPACE_LINE);
		sb.append("IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000020 DATE-WRITTEN. May 25, 2024");
		sb.append(LINEFEED_RETURN);

		String[] lines = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());

		try {
			CheckLineNumberStateCmd cmd = new CheckLineNumberStateCmd(lines);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertEquals(LineState.INDETERMINATE, cmd.getLineState());

		} catch (CobblerException ex) {
			Assert.fail();
		}
	}
	
	@Test
	public void perform_containsTab_linestateIndeterminate() {
		StringBuilder sb = new StringBuilder();
		sb.append("0\t0010 IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000020 DATE-WRITTEN. May 25, 2024");
		sb.append(LINEFEED_RETURN);

		String[] lines = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());

		try {
			CheckLineNumberStateCmd cmd = new CheckLineNumberStateCmd(lines);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertEquals(LineState.INDETERMINATE, cmd.getLineState());

		} catch (CobblerException ex) {
			Assert.fail();
		}
	}
	
	@Test
	public void perform_containsRandomLetterInNums_linestateIndeterminate() {
		StringBuilder sb = new StringBuilder();
		sb.append("000010 IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append("000M020 DATE-WRITTEN. May 25, 2024");
		sb.append(LINEFEED_RETURN);

		String[] lines = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());

		try {
			CheckLineNumberStateCmd cmd = new CheckLineNumberStateCmd(lines);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertEquals(LineState.INDETERMINATE, cmd.getLineState());

		} catch (CobblerException ex) {
			Assert.fail();
		}
	}	
	@Test
	public void perform_containsRandomLetterInSpaces_linestateIndeterminate() {
		StringBuilder sb = new StringBuilder();
		sb.append(WORD_SPACE);
		sb.append(WORD_SPACE);
		sb.append(WORD_SPACE);
		sb.append("H");
		sb.append(WORD_SPACE);
		sb.append(WORD_SPACE);
		sb.append(" IDENTIFICATION DIVISION.");
		sb.append(LINEFEED_RETURN);
		sb.append(WORD_SPACE_LINE);
		sb.append(" DATE-WRITTEN. May 25, 2024");
		sb.append(LINEFEED_RETURN);

		String[] lines = AbstractLineNumberCmd.splitStringOnNewlines(sb.toString());

		try {
			CheckLineNumberStateCmd cmd = new CheckLineNumberStateCmd(lines);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertEquals(LineState.INDETERMINATE, cmd.getLineState());

		} catch (CobblerException ex) {
			Assert.fail();
		}
	}
	
}
