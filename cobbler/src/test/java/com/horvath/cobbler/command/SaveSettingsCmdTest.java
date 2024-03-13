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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.syntax.GuiTheme;

/**
 * Tests operations of the SaveSettingsCmd class.
 * @author jhorvath
 */
public class SaveSettingsCmdTest {
	
	@Test 
	public void perform_mockData_dataWritenToFile() {

		File settingsFolder = new File(AbstractSettingsCmd.SETTING_FOLDER);
		File settingsFile = new File(AbstractSettingsCmd.APP_SETTINGS);

		Properties userProperties = null;

		if (settingsFolder.exists() && settingsFile.exists()) {
			userProperties = new Properties();
			try (InputStream input = new FileInputStream(AbstractSettingsCmd.APP_SETTINGS)) {

	        	// load a properties file
				userProperties.load(input);
	            
			} catch (IOException io) {
				// should not get here
				Assert.fail();
			}
		}
		
		if (userProperties != null) {
			// command should recreate these 
			Assert.assertTrue(settingsFile.delete());
			Assert.assertTrue(settingsFolder.delete());
		}
		
		Assert.assertFalse(settingsFile.exists());
		
		CobblerState state = CobblerState.getInstance();

		// build test data
		final GuiTheme testTheme = GuiTheme.Eclipse;
		final String file1 = "MathTest.cob";
		final String file2 = "LoopTest.cob";
		
		// load into state
		state.setCurrentTheme(testTheme);
		state.getRecentFilesList().clear();
		state.updateRecentFiles(file1);
		state.updateRecentFiles(file2);
		
		try {
			// run the command we are here to test
			SaveSettingsCmd cmd = new SaveSettingsCmd();
			cmd.perform();
			
			Assert.assertTrue(cmd.success);
			
			Assert.assertTrue(settingsFolder.exists());
			Assert.assertTrue(settingsFile.exists());

			// get data actually written to file
			final String actualContent = new String(Files.readAllBytes(Paths.get(AbstractSettingsCmd.APP_SETTINGS)));
			
			// make sure actual data contains our test values 
			Assert.assertTrue(actualContent.contains(testTheme.toString()));
			Assert.assertTrue(actualContent.contains(file1));
			Assert.assertTrue(actualContent.contains(file2));
			
			// perform cleanup 
			if (userProperties != null) {
				Assert.assertTrue(settingsFile.delete());
				
				try (OutputStream output = new FileOutputStream(AbstractSettingsCmd.APP_SETTINGS)) {
					
		            // save properties to project root folder
					userProperties.store(output, null);
					
				} catch (IOException ex) {
					// should not get here
					Assert.fail();
				}
			}
		} catch (CobblerException | IOException ex) {
			// should not get here
			Assert.fail();
		}
	}

}
