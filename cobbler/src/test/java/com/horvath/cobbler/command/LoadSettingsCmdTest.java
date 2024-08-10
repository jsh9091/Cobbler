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
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.syntax.GuiTheme;

/**
 * Tests operations of the LoadSettingsCmd class.
 * @author jhorvath
 */
public class LoadSettingsCmdTest {

	@Test
	public void perform_mockData_dataLoadedToState() {

		File settingsFolder = new File(AbstractSettingsCmd.SETTING_FOLDER);
		File settingsFile = new File(AbstractSettingsCmd.APP_SETTINGS);

		Properties userProperties = setupFileSystemForTest(settingsFolder, settingsFile);

		Assert.assertTrue(settingsFolder.exists());
		Assert.assertTrue(settingsFile.exists());

		// build test data
		final GuiTheme testTheme = GuiTheme.VS;
		final String file1 = "MathTest2.cob";
		final String file2 = "ConditionalTest4.cob";
		final int maxRecentFiles = 14;

		// write test data to disk
		try (OutputStream output = new FileOutputStream(AbstractSettingsCmd.APP_SETTINGS)) {
			Properties prop = new Properties();
			prop.setProperty(AbstractSettingsCmd.FIELD_THEME, testTheme.toString());
			prop.setProperty(AbstractSettingsCmd.FIELD_RECENT_FILE + 0, file1);
			prop.setProperty(AbstractSettingsCmd.FIELD_RECENT_FILE + 1, file2);
			prop.setProperty(AbstractSettingsCmd.FIELD_SPELL_CHECK_ON, "true");
			prop.setProperty(AbstractSettingsCmd.FIELD_SHOW_INVISIBLES, "true");
			prop.setProperty(AbstractSettingsCmd.FIELD_RECENT_FILES_MAX, String.valueOf(maxRecentFiles));
			prop.store(output, null);

		} catch (IOException ex) {
			// should not get here
			Assert.fail();
		}

		try {
			// run the command we are here to test
			LoadSettingsCmd cmd = new LoadSettingsCmd();
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());

			CobblerState state = CobblerState.getInstance();

			// compare test data to actual state data
			Assert.assertEquals(testTheme, state.getCurrentTheme());
			Assert.assertTrue(state.getRecentFilesList().contains(file1));
			Assert.assertTrue(state.getRecentFilesList().contains(file2));
			Assert.assertTrue(state.isSpellcheckOn());
			Assert.assertTrue(state.isShowInvisibleCharacters());
			Assert.assertEquals(maxRecentFiles, state.getMaxNumOfRecentFiles());

			// perform cleanup
			if (userProperties != null) {
				restoreUserSettings(userProperties, settingsFile);
				
			} else {
				Assert.assertTrue(settingsFile.delete());
				Assert.assertTrue(settingsFolder.delete());
			}

		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
	}
	
	@Test
	public void perform_noSettingsFile_DefaultsLoaded() {

		File settingsFolder = new File(AbstractSettingsCmd.SETTING_FOLDER);
		File settingsFile = new File(AbstractSettingsCmd.APP_SETTINGS);

		Properties userProperties = setupFileSystemForTest(settingsFolder, settingsFile);
		
		Assert.assertTrue(settingsFile.delete());
		
		try {
			// run the command we are here to test
			LoadSettingsCmd cmd = new LoadSettingsCmd();
			cmd.perform();

			Assert.assertTrue(cmd.isSuccess());

			CobblerState state = CobblerState.getInstance();

			// compare test data to actual state data, we expect default values set in LoadSettingsCmd
			Assert.assertEquals(GuiTheme.Default, state.getCurrentTheme());
			Assert.assertTrue(state.isSpellcheckOn());
			Assert.assertFalse(state.isShowInvisibleCharacters());

			// perform cleanup
			if (userProperties != null) {
				restoreUserSettings(userProperties, settingsFile);
				
			} else {
				Assert.assertTrue(settingsFile.delete());
				Assert.assertTrue(settingsFolder.delete());
			}

		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
		
	}
	
	/**
	 * Prepares the user Cobbler folder for testing. 
	 * If user settings file is found, it is stored so it can be replaced after tests are done. 
	 * 
	 * @param settingsFolder File
	 * @param settingsFile File 
	 * @return Properties
	 */
	private Properties setupFileSystemForTest(File settingsFolder, File settingsFile) {
		Properties userProperties = null;
		
		if (settingsFolder.exists() && settingsFile.exists()) {
			userProperties = new Properties();
			try (InputStream input = new FileInputStream(AbstractSettingsCmd.APP_SETTINGS)) {

				// load properties file
				userProperties.load(input);
				
				// need to remove dictionary for proper test operations
				File dictionary = new File(AbstractSettingsCmd.APP_DICTIONARY);
				dictionary.delete();

			} catch (IOException io) {
				// should not get here
				Assert.fail();
			}
		} else {
			Assert.assertTrue(settingsFolder.mkdir());
			try {
				Assert.assertTrue(settingsFile.createNewFile());
			} catch (IOException e) {
				// should not get here
				Assert.fail();
			}
		}
		return userProperties;
	}
	
	/**
	 * Restores user properties to file system. 
	 * 
	 * @param userProperties Properties
	 * @param settingsFile File 
	 */
	private void restoreUserSettings(Properties userProperties, File settingsFile) {
		if (userProperties != null && settingsFile.exists()) {
			Assert.assertTrue(settingsFile.delete());

			try (OutputStream output = new FileOutputStream(AbstractSettingsCmd.APP_SETTINGS)) {

				// save properties to project root folder
				userProperties.store(output, null);

			} catch (IOException ex) {
				// should not get here
				Assert.fail();
			}
		}
	}

}
