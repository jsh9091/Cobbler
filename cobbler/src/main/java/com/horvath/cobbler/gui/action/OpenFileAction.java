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
import javax.swing.filechooser.FileNameExtensionFilter;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.LoadFileCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * GUI level operations for loading a file. 
 * @author jhorvath 
 */
public class OpenFileAction extends OpenSaveAsAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		Debugger.printLog("Open a new file", this.getClass().getName());
		
		// create the "open as" dialog
		JFileChooser chooser = new JFileChooser(getLastFolder());
		chooser.setDialogTitle("Select a COBOL file");
		
		// automatically filter out non-Cobol files
		FileNameExtensionFilter filter = new FileNameExtensionFilter("COBOL Source Files", "cob", "COB", "cbl", "CBL",
				"cpy", "CPY", "pco", "PCO", "fd", "FD", "sel", "SEL", "ws", "WSL");
		chooser.setFileFilter(filter);

		// display the dialog for user to select a file
		int returnValue = chooser.showOpenDialog(null);
		
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
					// update the GUI
					CobblerWindow.getWindow().getTextArea().setText(CobblerState.getInstance().getData());
					CobblerWindow.getWindow().getTextArea().setCaretPosition(0);
					
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
