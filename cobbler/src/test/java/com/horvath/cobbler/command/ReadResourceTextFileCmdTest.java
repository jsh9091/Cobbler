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

import com.horvath.cobbler.exception.CobblerException;

/**
 * Tests operations of the ReadResourceTextFileCmd class.
 * 
 * @author jhorvath
 */
public class ReadResourceTextFileCmdTest {

	@Test
	public void perform_nullFilepath_exception() {
		boolean caughtException = false;

		try {
			ReadResourceTextFileCmd cmd = new ReadResourceTextFileCmd(null);
			cmd.perform();

			Assert.fail(); // should not get here
		} catch (CobblerException actual) {
			caughtException = true;
			Assert.assertEquals(ReadResourceTextFileCmd.BAD_FILEPATH, actual.getMessage());
		}
		Assert.assertTrue(caughtException);
	}

	@Test
	public void perform_emptyFilepath_exception() {
		boolean caughtException = false;

		try {
			ReadResourceTextFileCmd cmd = new ReadResourceTextFileCmd("");
			cmd.perform();

			Assert.fail(); // should not get here
		} catch (CobblerException actual) {
			caughtException = true;
			Assert.assertEquals(ReadResourceTextFileCmd.BAD_FILEPATH, actual.getMessage());
		}
		Assert.assertTrue(caughtException);
	}

	@Test
	public void perform_readIntrinsicFuncs_listReturned() {
		try {
			ReadResourceTextFileCmd cmd = new ReadResourceTextFileCmd(ReadResourceTextFileCmd.INTRINSIC_FUNCTIONS);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertFalse(cmd.getResultList().isEmpty());

			// spot checks
			Assert.assertTrue(cmd.getResultList().contains("ACOS"));
			Assert.assertTrue(cmd.getResultList().contains("LENGTH"));
			Assert.assertTrue(cmd.getResultList().contains("MIDRANGE"));
			Assert.assertTrue(cmd.getResultList().contains("YEAR-TO-YYYY"));

		} catch (CobblerException ex) {
			Assert.fail(); // should not get here
		}
	}

	@Test
	public void perform_readReservedWords_listReturned() {
		try {
			ReadResourceTextFileCmd cmd = new ReadResourceTextFileCmd(ReadResourceTextFileCmd.RESERVED_WORDS);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertFalse(cmd.getResultList().isEmpty());

			// spot checks
			Assert.assertTrue(cmd.getResultList().contains("ACCEPT"));
			Assert.assertTrue(cmd.getResultList().contains("LENGTH"));
			Assert.assertTrue(cmd.getResultList().contains("SUPPRESS"));
			Assert.assertTrue(cmd.getResultList().contains("ZEROES"));

		} catch (CobblerException ex) {
			Assert.fail(); // should not get here
		}
	}

	@Test
	public void perform_reservedWords_noEmptyStrings() {
		try {
			ReadResourceTextFileCmd cmd = new ReadResourceTextFileCmd(ReadResourceTextFileCmd.RESERVED_WORDS);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertFalse(cmd.getResultList().isEmpty());

			for (String s : cmd.getResultList()) {
				if (s == null || s.isEmpty()) {
					Assert.fail();
				}
			}

		} catch (CobblerException ex) {
			Assert.fail(); // should not get here
		}
	}
	
	@Test 
	public void perform_operators_listReturned() {
		try {
			ReadResourceTextFileCmd cmd = new ReadResourceTextFileCmd(ReadResourceTextFileCmd.OPERATORS);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			Assert.assertFalse(cmd.getResultList().isEmpty());

			// spot checks
			Assert.assertTrue(cmd.getResultList().contains("+"));
			Assert.assertTrue(cmd.getResultList().contains("**"));
			Assert.assertTrue(cmd.getResultList().contains("=="));
			Assert.assertTrue(cmd.getResultList().contains(">>"));

		} catch (CobblerException ex) {
			Assert.fail(); // should not get here
		}
	}
}
