/* MIT License
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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Tests operations of the LoadFileCmd class.
 * @author jhorvath
 */
public class LoadFileCmdTest {
	
	public static final String RESOURCES_DIRECTORY = "src" + File.separator + "test" 
			+ File.separator + "resources";
	
	public static final String LOADFILECMDTEST_DIRECTORY = RESOURCES_DIRECTORY 
			+ File.separator + "LoadFileCmdTest";
	
	@Test
	public void perform_NullFile_ErrorMessage() {
		try {
			LoadFileCmd cmd = new LoadFileCmd(null);
			cmd.perform();
			
			Assert.assertFalse(cmd.isSuccess());
			Assert.assertEquals(LoadFileCmd.ERROR_FILE_IS_NULL, cmd.getMessage());
			
		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
	}
	
	@Test 
	public void perform_FileNotReal_ErrorMessage() {
		
		File file = new File("fakeFile.cob");
		Assert.assertTrue(!file.exists());

		try {
			LoadFileCmd cmd = new LoadFileCmd(file);
			cmd.perform();

			Assert.assertFalse(cmd.isSuccess());
			Assert.assertEquals(LoadFileCmd.ERROR_FILE_NOT_FOUND, cmd.getMessage());
			
		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
	}

	@Test
	public void perform_badFile_failure() {
		// try to load an image file instead of a text file
		File file = new File(LOADFILECMDTEST_DIRECTORY + File.separator + "PPD-icon-16px.png");
		
		Assert.assertTrue(file.exists());
		
		boolean exceptionCaught = false;
		LoadFileCmd cmd = new LoadFileCmd(file);
		try {
			cmd.perform();

			// should not get here
			Assert.fail();
			
		} catch (CobblerException ex) {
			exceptionCaught = true;

			Assert.assertFalse(cmd.isSuccess());
			Assert.assertEquals(LoadFileCmd.ERROR_UNKOWN_LOAD_PROBLEM, ex.getMessage());
		}
		Assert.assertTrue(exceptionCaught);
	}

	@Test
	public void perform_validFile_success() {
		// try to load a good file 
		File file = new File(LOADFILECMDTEST_DIRECTORY + File.separator + "MathTest.cob");
		
		Assert.assertTrue(file.exists());
		
		try {
			LoadFileCmd cmd = new LoadFileCmd(file);
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());
			
			CobblerState state = CobblerState.getInstance();
			
			Assert.assertNotNull(state.getData());
			Assert.assertFalse(state.getData().isEmpty());
			Assert.assertNotNull(state.getFile());
			Assert.assertEquals(file.getAbsolutePath(), state.getFile().getAbsolutePath());

		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
	}
}
