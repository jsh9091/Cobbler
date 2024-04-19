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

package com.horvath.cobbler.application;

import java.io.File;
import java.util.logging.Level;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.horvath.cobbler.command.LoadFileCmd;
import com.horvath.cobbler.command.LoadSettingsCmd;
import com.horvath.cobbler.command.NewEmptyDocumentCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;
import com.horvath.cobbler.gui.action.OpenFileAction;

/**
 * Main application class.
 * 
 * @author jhorvath
 */
public final class CobblerApplication {
	
	public static final String APP_VERSION = "0.2";
	public static final String APP_NAME = "Cobbler";

	public static void main(String[] args) {

		final String arg = args.length > 0 ? args[0] : "";

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CobblerApplication().initialize(arg);
			}
		});
	}

	/**
	 * Initializes application systems.
	 */
	private void initialize(String arg) {
		Debugger.setDebugging(true);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ex) {

			Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.SEVERE);
		}

		CobblerState.getInstance();
		
		
		try {
			// create and load settings data file
			LoadSettingsCmd settingsCmd = new LoadSettingsCmd();
			settingsCmd.perform();

			File file = new File(arg);
			if (file.exists()) {
				// load file into state
				LoadFileCmd cmd = new LoadFileCmd(file);
				cmd.perform();
				
				if (cmd.isSuccess()) {
					OpenFileAction.updateGuiForOpenedFile(file.getAbsolutePath());
				}
				
			} else {
				// initialize the application with a new empty document
				NewEmptyDocumentCmd newDocCmd = new NewEmptyDocumentCmd();
				newDocCmd.perform();
			}

		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.WARNING);
		}
		
		CobblerWindow.getWindow().setVisible(true);
	}

}
