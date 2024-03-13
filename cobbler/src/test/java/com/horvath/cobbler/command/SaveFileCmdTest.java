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
import com.horvath.cobbler.io.TextFileReader;

/**
 * Tests operations of the SaveFileCmd class.
 * @author jhorvath
 */
public class SaveFileCmdTest {
	
	public static final String RESOURCES_DIRECTORY = "src" + File.separator + "test" 
			+ File.separator + "resources";
	
	public static final String SAVEFILECMDTEST_DIRECTORY = RESOURCES_DIRECTORY 
			+ File.separator + "SaveFileCmdTest";
	
	@Test
	public void perform_NullFile_ErrorMessage() {
		try {
			SaveFileCmd cmd = new SaveFileCmd(null);
			cmd.perform();
			
			Assert.assertFalse(cmd.isSuccess());
			Assert.assertEquals(SaveFileCmd.ERROR_FILE_IS_NULL, cmd.getMessage());
			
		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
	}
	
	@Test
	public void perform_validFile_success() {
		// try to load a good file 
		File file = new File(SAVEFILECMDTEST_DIRECTORY + File.separator + "LoopTest.cob");
		
		Assert.assertTrue(file.exists());
		
		CobblerState state = CobblerState.getInstance();
		
		// load data in state
		try {
			LoadFileCmd loadCmd = new LoadFileCmd(file);
			loadCmd.perform();
			Assert.assertTrue(loadCmd.isSuccess());
			Assert.assertTrue(file.getAbsolutePath().equals(state.getFile().getAbsolutePath()));

		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
		
		File actualFile = new File(SAVEFILECMDTEST_DIRECTORY + File.separator + "LoopTest_ACTUAL.cob");
		
		// cleanup actual file if last fun failed
		actualFile.delete();
		
		Assert.assertFalse(actualFile.exists());
		
		SaveFileCmd saveCmd = new SaveFileCmd(actualFile);
		try {
			saveCmd.perform();
			
			Assert.assertTrue(saveCmd.isSuccess());
			Assert.assertEquals(state.getFile().getAbsolutePath(), actualFile.getAbsolutePath());
			Assert.assertTrue(actualFile.exists());
			
			// compare files on disk
			try {
				// read in control file and store contents
				TextFileReader reader = new TextFileReader(file);
				final String controlData = reader.read();
				
				// read in test file and store contents
				reader = new TextFileReader(actualFile);
				final String actualData = reader.read();
				
				// contents should be the same
				Assert.assertEquals(controlData, actualData);
				
			} catch (CobblerException ex) {
				// should not get here
				Assert.fail();
			}
			
			// cleanup test file 
			Assert.assertTrue(actualFile.delete());
			
		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
	}

}
