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
import java.util.Properties;
import java.util.logging.Level;

import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;

public final class LoadSettingsCmd extends AbstractSettingsCmd {

	@Override
	public void perform() throws CobblerException {
		Debugger.printLog("Load Settings Properties File", this.getClass().getName());
		
		setupSettingsFolderAndFile();
		
        try (InputStream input = new FileInputStream(APP_SETTINGS)) {

        	Properties prop = new Properties();    

        	// load a properties file
            prop.load(input);
            
            if (prop.isEmpty()) {
            	// load default values into state
            	
            } else {
            	// load data from file into state
            }

        } catch (IOException io) {
			final String message = "Error Reading the properties file." + io.getMessage();
			Debugger.printLog(message, this.getClass().getName(),
					Level.SEVERE);
			throw new CobblerException(message, io); 
        }
	}
}
