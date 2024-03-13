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

package com.horvath.cobbler.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.LoadFileCmd;
import com.horvath.cobbler.command.SaveSettingsCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * Action for opening a recent file. 
 */
public class OpenRecentAction extends CobblerAction {
	
	private static final long serialVersionUID = 1L;

	private String filepath;
	
	/**
	 * Constructor. 
	 * @param filepath
	 */
	public OpenRecentAction(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Debugger.printLog("Open a recent file", this.getClass().getName());
		
		if (CobblerWindow.checkForDirtyState()) {
			return;
		}
		
		File file = new File(filepath);
		
		if (!file.exists()) {
			CobblerWindow.getWindow().simpleMessagePopup("File Not Found", 
					"The selected file was not found at the location it was " 
			+ System.lineSeparator() + "opened from. The file was probably moved or renamed.", 
			JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try {
			// perform core logic of loading the file into state 
			LoadFileCmd cmd = new LoadFileCmd(file);
			cmd.perform();
			
			if (cmd.isSuccess()) {
				CobblerWindow window = CobblerWindow.getWindow();
				CobblerState state = CobblerState.getInstance();
				
				// update the text area GUI
				window.getTextArea().setText(state.getData());
				window.getTextArea().setCaretPosition(0);
				window.getTextArea().discardAllEdits();
				
				// display document name to user in GUI
				window.setDocumentName(state.getFile().getName());
				
				// recent files updates
				state.updateRecentFiles(file.getAbsolutePath());
				window.updateRecentFilesMenu();
				
				// update the settings file to reorder the recently opened files
				SaveSettingsCmd saveSettingsCmd = new SaveSettingsCmd();
				saveSettingsCmd.perform();
				
				// need to clear state because GUI updates impact the state dirty flag
				state.setDirty(false);
				
			} else {
				CobblerWindow.getWindow().simpleMessagePopup("Load Error",
						LoadFileCmd.ERROR_UNKOWN_LOAD_PROBLEM + " " + cmd.getMessage(),
						JOptionPane.WARNING_MESSAGE);
			}
			
		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage() + " " + file.getName(), this.getClass().getName(), Level.WARNING);
			CobblerWindow.getWindow().simpleMessagePopup("Load Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
