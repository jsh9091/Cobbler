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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;

import com.horvath.cobbler.application.CobblerApplication;
import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Saves state settings data to properties file. 
 * @jhorvath 
 */
public final class SaveSettingsCmd extends AbstractSettingsCmd {

	@Override
	public void perform() throws CobblerException {
		success = false;
		
		Debugger.printLog("Saving Settings Properties File", this.getClass().getName());
		
		setupSettingsFolderAndFile();
		
		try (OutputStream output = new FileOutputStream(APP_SETTINGS)) {

            Properties prop = new Properties();
            CobblerState state =  CobblerState.getInstance();

            // set the properties values from state
            prop.setProperty(FIELD_THEME, state.getCurrentTheme().toString());
            prop.setProperty(FIELD_SPELL_CHECK_ON, String.valueOf(state.isSpellcheckOn()));
            prop.setProperty(FIELD_SHOW_INVISIBLES, String.valueOf(state.isShowInvisibleCharacters()));
            prop.setProperty(FIELD_RECENT_FILES_MAX, String.valueOf(state.getMaxNumOfRecentFiles()));
            
            // for (String recentFile : CobblerState.getInstance().getRecentFilesList()) {
            for (int i = 0; i < state.getRecentFilesList().size(); i++) {
            	String filepath = state.getRecentFilesList().get(i);
            	prop.setProperty(FIELD_RECENT_FILE + i, filepath);
            }

			final String comment = "Settings file for " + CobblerApplication.APP_NAME + " version "
					+ CobblerApplication.APP_VERSION + " for user: " + System.getProperty("user.name");
			
            // save properties to disk
            prop.store(output, comment);

            success = true;

        } catch (IOException io) {
			final String message = "Error Saving the properties file." + io.getMessage();
			Debugger.printLog(message, this.getClass().getName(),
					Level.SEVERE);
			throw new CobblerException(message, io); 
        }
	}

}
