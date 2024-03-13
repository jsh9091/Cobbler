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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.LoadFileCmd;
import com.horvath.cobbler.command.SaveSettingsCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * GUI level operations for loading a file. 
 * @author jhorvath 
 */
public final class OpenFileAction extends OpenSaveAsAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		Debugger.printLog("Open a new file", this.getClass().getName());
		
		if (CobblerWindow.checkForDirtyState()) {
			return;
		}
		
		// create the "open as" dialog
		JFileChooser chooser = new JFileChooser(getLastFolder());
		chooser.setDialogTitle("Select a COBOL file");
		
		// automatically filter out non-Cobol files
		chooser.setFileFilter(getFileNameExtensionFilter());

		// display the dialog for user to select a file
		int returnValue = chooser.showOpenDialog(CobblerWindow.getWindow());
		
		// if the user selected a file 
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			
			// update the folder location for future Open/Save As dialogs
			setLastFolder(selectedFile.getParentFile().getAbsolutePath());
			
			try {
				// perform core logic of loading the file into state 
				LoadFileCmd cmd = new LoadFileCmd(selectedFile);
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
					state.updateRecentFiles(selectedFile.getAbsolutePath());
					window.updateRecentFilesMenu();
					
					// update the settings file to store the newly opened file location
					SaveSettingsCmd saveSettingsCmd = new SaveSettingsCmd();
					saveSettingsCmd.perform();
					
					// need to clear state because GUI updates impact the state dirty flag
					CobblerState.getInstance().setDirty(false);
					
				} else {
					CobblerWindow.getWindow().simpleMessagePopup("Load Error",
							LoadFileCmd.ERROR_UNKOWN_LOAD_PROBLEM + " " + cmd.getMessage(),
							JOptionPane.WARNING_MESSAGE);
				}
				
			} catch (CobblerException ex) {
				Debugger.printLog(ex.getMessage() + " " + selectedFile.getName(), this.getClass().getName(), Level.WARNING);
				CobblerWindow.getWindow().simpleMessagePopup("Load Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
