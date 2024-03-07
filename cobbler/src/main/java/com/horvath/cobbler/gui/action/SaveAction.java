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
import com.horvath.cobbler.command.SaveFileCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 *  GUI level operations for Saving a file. 
 *  @author jhorvath 
 */
public final class SaveAction extends OpenSaveAsAction {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Enumeration to determine if operation is save or save as. 
	 */
	public enum SaveType {
		  SAVE,
		  SAVE_AS
	}
	
	private SaveType type;
	
	/**
	 * Constructor. 
	 * @param type SaveType
	 */
	public SaveAction(SaveType type) {
		this.type = type;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		File file = CobblerState.getInstance().getFile();		
		
		// if the user selected the "Save" menu option and the file exists
		if (this.type == SaveType.SAVE && file.exists()) { 
			// run the save command without Save As dialog
			runCommand(file);
			
		} else {
			File userFile = saveAsDialog(file);
			
			if (userFile == null) {
				// user canceled 
				return;
				
			} else {
				// run save command 
				runCommand(userFile);
			}
		}
	}
	
	/**
	 * Shows a save as dialog and allow user to specify a name and location to save the file. 
	 * 
	 * @param file File 
	 * @return File 
	 */
	private File saveAsDialog(File file) {

		File resultFile;

		JFileChooser chooser = new JFileChooser(getLastFolder());
		chooser.setDialogTitle("Save As");
		chooser.setSelectedFile(file);

		// automatically filter out non-Cobol files
		chooser.setFileFilter(getFileNameExtensionFilter());

		int option = chooser.showSaveDialog(CobblerWindow.getWindow());

		// user clicked "Save" button
		if (option == JFileChooser.APPROVE_OPTION) {
			// if the file name does not appear to have an extension
			if (!chooser.getSelectedFile().getName().contains(".")) {
				// add default file extension
				resultFile = new File(chooser.getSelectedFile() + "." + cobolExtensions[0]);
			} else {
				resultFile = chooser.getSelectedFile();
			}

		} else {
			// user canceled
			resultFile = null;
		}

		return resultFile;
	}
	
	/**
	 * Runs the save command. 
	 * 
	 * @param file File 
	 */
	private void runCommand(File file) {

		SaveFileCmd cmd = new SaveFileCmd(file);
		try {
			cmd.perform();
			
			if (cmd.isSuccess()) {
				CobblerWindow.getWindow().setDocumentName(CobblerState.getInstance().getFile().getName());
			}

		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage() + " " + file.getName(), this.getClass().getName(), Level.WARNING);
			CobblerWindow.getWindow().simpleMessagePopup("Save Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
