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
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.SaveSettingsCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;
import com.horvath.cobbler.gui.SettingsDialog;
import com.horvath.cobbler.gui.syntax.GuiTheme;

/**
 * Action for saving settings data. 
 * @author jhorvath
 */
public final class SaveSettingsAction extends CobblerAction {

	private static final long serialVersionUID = 1L;
	
	private SettingsDialog dialog; 
	
	/**
	 * Constructor. 
	 * @param dialog SettingsDialog
	 */
	public SaveSettingsAction(SettingsDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// get the user selected value from the GUI
		final String theme = (String) dialog.getThemeMenu().getSelectedItem();
		
		final boolean clearRecent = dialog.getClearRecentCheckBox().isSelected();
		
		// get the enumeration value 
		GuiTheme selectedTheme = GuiTheme.Default;
		for (GuiTheme day : GuiTheme.values()) { 
		    if (day.name().equals(theme)) {
		    	selectedTheme = day;
		    	break;
		    }
		}
		
		// update state
		CobblerState.getInstance().setCurrentTheme(selectedTheme);
		if (clearRecent) {
			CobblerState.getInstance().getRecentFilesList().clear();
		}
		
		try {
			// run command to update properties file 
			SaveSettingsCmd cmd = new SaveSettingsCmd();
			cmd.perform();
			
			if (cmd.isSuccess()) {
				// update GUI
				dialog.dispose();
				CobblerWindow.getWindow().updateTextAreaTheme();
				if (clearRecent) {
					CobblerWindow.getWindow().updateRecentFilesMenu();
				}
			}
			
		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.WARNING);
			CobblerWindow.getWindow().simpleMessagePopup("Error", 
					"There was a problem saving the settings data."
					+ System.lineSeparator() + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
