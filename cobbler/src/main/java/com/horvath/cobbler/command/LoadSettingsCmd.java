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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.syntax.GuiTheme;

/**
 * Reads application properties file into state. 
 * @author jhorvath
 */
public final class LoadSettingsCmd extends AbstractSettingsCmd {
	
	/**
	 * Controls the default maximum number of files displayed in the recent files menu.
	 */
	public static final int DEFAULT_RECENT_FILES = 5;
	
	public static final int MAX_SUPPORTED_RECENT_FILES = 20;

	@Override
	public void perform() throws CobblerException {
		Debugger.printLog("Load Settings Properties File", this.getClass().getName());
		
		success = false; 
		
		setupSettingsFolderAndFile();
		
        try (InputStream input = new FileInputStream(APP_SETTINGS)) {

        	Properties prop = new Properties();    

        	// load the properties file
            prop.load(input);

            if (prop.isEmpty()) {
            	// load default values into state
            	defaultSettings();
            	
            } else {
            	CobblerState state = CobblerState.getInstance();
            	
            	// load theme data from file into state
            	String themeText = prop.getProperty(FIELD_THEME);
            	GuiTheme loadedTheme = GuiTheme.fromString(themeText);
            	if (loadedTheme != null) {
                	state.setCurrentTheme(loadedTheme);            		
            	} else {
            		state.setCurrentTheme(GuiTheme.Default);   
            	}
            	
            	// load recent files 
            	ArrayList<String> fileList = new ArrayList<>();
            	for (int i = 0; i < state.getMaxNumOfRecentFiles(); i++) {
            		String filepath = prop.getProperty(FIELD_RECENT_FILE + i);
            		if (filepath == null) {
            			break;
            		}
            		fileList.add(filepath);
            	}
            	if (!fileList.isEmpty()) {
            		state.getRecentFilesList().addAll(fileList);
            	}
            	
            	// load spell check setting
            	String spellcheckOnProp = prop.getProperty(FIELD_SPELL_CHECK_ON);
            	if ("true".equalsIgnoreCase(spellcheckOnProp)) {
            		state.setSpellcheckOn(true);
            	} else {
            		state.setSpellcheckOn(false);
            	}
            	
            	// load value for if we should show end of line characters or not
            	String showInvisibleProp = prop.getProperty(FIELD_SHOW_INVISIBLES);
            	if ("true".equalsIgnoreCase(showInvisibleProp)) {
            		state.setShowInvisibleCharacters(true);
            	} else {
            		state.setShowInvisibleCharacters(false);
            	}
            	
            	// load value for maximum number of recent files
            	String maxRecentFilesString = prop.getProperty(FIELD_RECENT_FILES_MAX);
            	try {
            		int max = Integer.parseInt(maxRecentFilesString);
            		   
            		   // make sure number in range we will allow
            		   if (max < 1) {
            			   max = DEFAULT_RECENT_FILES;
            		   } else if (max > MAX_SUPPORTED_RECENT_FILES) {
            			   max = MAX_SUPPORTED_RECENT_FILES;
            		   }
            		   
            		   state.setMaxNumOfRecentFiles(max);
            		   
            		} catch (NumberFormatException e) {
            			state.setMaxNumOfRecentFiles(DEFAULT_RECENT_FILES);
            		}
            }

            success = true; 
            
        } catch (IOException io) {
        	defaultSettings();
			final String message = "Error Reading the properties file." + io.getMessage();
			Debugger.printLog(message, this.getClass().getName(),
					Level.SEVERE);
			throw new CobblerException(message, io); 
        }
	}
	
	/**
	 * Loads default values to state. 
	 */
	private void defaultSettings() {
		CobblerState state = CobblerState.getInstance();
		state.setCurrentTheme(GuiTheme.Default);
		state.setSpellcheckOn(true);
		state.setShowInvisibleCharacters(false);
		state.setMaxNumOfRecentFiles(DEFAULT_RECENT_FILES);
	}
}
