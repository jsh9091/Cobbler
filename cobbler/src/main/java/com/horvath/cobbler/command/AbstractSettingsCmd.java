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
import java.io.IOException;
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
	
	protected static final String FIELD_THEME = "theme";
		
	/**
	 * Checks that settings folder and file exist, and if they don't creates them. 
	 * @throws CobblerException 
	 */
	protected void setupSettingsFolderAndFile() throws CobblerException {
		
		try {
			File folder = new File(SETTING_FOLDER);
			if (!folder.exists()) {
				Debugger.printLog("Creating Settings Folder", this.getClass().getName());
				folder.mkdir();
			}
			
			File file = new File(APP_SETTINGS);
			if (!file.exists()) {
				Debugger.printLog("Creating Settings Properties File", this.getClass().getName());
				file.createNewFile();
			}
			
		} catch (IOException io) {
			final String message = "Error Creating Settings folder or file. " + io.getMessage();
			Debugger.printLog(message, this.getClass().getName(),
					Level.SEVERE);
			throw new CobblerException(message, io); 
		}
	}

}
