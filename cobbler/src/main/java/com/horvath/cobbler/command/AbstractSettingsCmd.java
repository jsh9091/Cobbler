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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Superclass for loading and saving properties file operations. 
 * @author jhorvath
 */
public abstract class AbstractSettingsCmd extends CobblerCommand {

	public static final String USER_HOME = System.getProperty("user.home");
	public static final String SETTING_FOLDER = USER_HOME + File.separator + "Cobbler";
	public static final String APP_SETTINGS = SETTING_FOLDER + File.separator + "Cobbler.properties";
	public static final String APP_DICTIONARY = SETTING_FOLDER + File.separator + "english_dic.zip"; 
	
	protected static final String FIELD_THEME = "theme";
	protected static final String FIELD_RECENT_FILE = "recent";
	protected static final String FIELD_SPELL_CHECK_ON = "spellcheck";
		
	/**
	 * Checks that settings folder and file exist, and if they don't creates them. 
	 * @throws CobblerException 
	 */
	public static void setupSettingsFolderAndFile() throws CobblerException {
		
		try {
			File folder = new File(SETTING_FOLDER);
			if (!folder.exists()) {
				Debugger.printLog("Creating Settings Folder", AbstractSettingsCmd.class.getName());
				folder.mkdir();
			}
			
			File file = new File(APP_SETTINGS);
			if (!file.exists()) {
				Debugger.printLog("Creating Settings Properties File", AbstractSettingsCmd.class.getName());
				file.createNewFile();
			}
			
			File dictionaryFile = new File(APP_DICTIONARY);
			if (!dictionaryFile.exists()) {
				// need to copy file because cannot access from inside runnable jar
				copyFile(dictionaryFile, "/resources/english_dic.zip");
			}
			
		} catch (IOException io) {
			final String message = "Error Creating Settings folder or file. " + io.getMessage();
			Debugger.printLog(message, AbstractSettingsCmd.class.getName(),
					Level.SEVERE);
			throw new CobblerException(message, io); 
		}
	}

	/**
	 * Copies a file. 
	 * @param dictionaryFile File - the file to write to
	 * @param fileName String - the location we are copying from 
	 * @throws IOException
	 */
	private static void copyFile(File dictionaryFile, String fileName) throws IOException {
		try (InputStream is = AbstractSettingsCmd.class.getResourceAsStream(fileName);
				FileOutputStream fos = new FileOutputStream(dictionaryFile)) {
			while (is.available() > 0) {
				fos.write(is.read());
			}
		}
	}
	
}
