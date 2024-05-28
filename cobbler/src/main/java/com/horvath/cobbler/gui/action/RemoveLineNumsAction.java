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
import com.horvath.cobbler.command.AbstractLineNumberCmd.LineState;
import com.horvath.cobbler.command.RemoveLineNumbersCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * Action for adding hard coded line numbers to Cobol files. 
 * @author jhorvath
 */
public final class RemoveLineNumsAction extends AbstractAddRemoveLineNumsAction {

	private static final long serialVersionUID = 1L;
	private LineState lineState;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String text = CobblerWindow.getWindow().getTextArea().getText();
		
		this.lineState = examineLineState(text);
		
		if (lineState == LineState.INDETERMINATE) { 
			inderterminateWarningConfirmation(text);
			
		} else if (lineState == LineState.NOT_NUMBERED) {
			CobblerWindow.getWindow().simpleMessagePopup("No Line Numbers", 
					"The file does not have line numbers to remove.");
			
		} else {
			removeLineNumbering(text);
		}
	}

	/**
	 * Perform removal of line numbers. 
	 * @param text String 
	 */
	private void removeLineNumbering(String text) {
		CobblerWindow window = CobblerWindow.getWindow();
		window.guiWait();
		String message = null;
		
		try {
			RemoveLineNumbersCmd cmd = new RemoveLineNumbersCmd(text);
			cmd.perform();
			
			if (cmd.isSuccess()) {
				window.getTextArea().setText(CobblerState.getInstance().getData());
				message = cmd.getMessage();
			}
			
		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.WARNING);
			window.guiResume();
			window.simpleMessagePopup("Remove Numbering Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		
		window.guiResume();
		
		if (lineState == LineState.INDETERMINATE && message != null) {
			window.simpleMessagePopup("Some lines not processed", message);
		}
	}
	
	/**
	 * Prompts the user with a warning confirmation. 
	 * If user chooses no option, the operation is aborted. 
	 * 
	 * @param lines String[] 
	 */
	private void inderterminateWarningConfirmation(String text) {
		final String eol = System.lineSeparator();
		
		final String message = 
				  "Cobbler was not able to fully determine if the current file has line numbers or not. " + eol
				+ "It is recommended to stop and save a copy of the file before proceeding. Cobbler will do " + eol
				+ "its best to remove the line numbers without creating any breaking changes, but carefully " + eol
				+ "examine the file after changes have been applied. Proceed with removing line numbers?";

		// ask user if they want to overwrite the file
		int result = JOptionPane.showConfirmDialog(CobblerWindow.getWindow(),
				message, "Confirmation",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		// the user chose to stop the operation and not overwrite the file
		if (result == JOptionPane.NO_OPTION) {
			return;
		} else {
			removeLineNumbering(text);
		}
	}
}
