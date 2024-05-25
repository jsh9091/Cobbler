package com.horvath.cobbler.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.AbstractLineNumberCmd.LineState;
import com.horvath.cobbler.command.AddLineNumbersCmd;
import com.horvath.cobbler.command.CheckLineNumberStateCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

public class AddLineNumbersAction extends CobblerAction {

	private static final long serialVersionUID = 1L;

	private LineState lineState;
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String text = CobblerWindow.getWindow().getTextArea().getText();
		
		examineLineState(text);
		
		if (lineState == LineState.INDETERMINATE) { 
			inderterminateWarningConfirmation(text);
		} else {
			doRenumbering(text);
		}
	}
	
	/**
	 * Examines and determines if the file has hard coded numbers or not. 
	 * @param lines String[]
	 */
	private void examineLineState(String text) {
		try {
			CheckLineNumberStateCmd cmd = new CheckLineNumberStateCmd(text);
			cmd.perform();
			
			if (cmd.isSuccess()) {
				this.lineState = cmd.getLineState();
			} else {
				this.lineState = LineState.INDETERMINATE;
			}

		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.WARNING);
			CobblerWindow.getWindow().simpleMessagePopup("Line Number Examination Error", 
					ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void doRenumbering(String text) {
		
		CobblerWindow window = CobblerWindow.getWindow();
		window.guiWait();
		
		try {
			AddLineNumbersCmd cmd = new AddLineNumbersCmd(text, this.lineState, 10);
			cmd.perform();
			
			if (cmd.isSuccess()) {
				window.getTextArea().setText(CobblerState.getInstance().getData());
			}
			
		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.WARNING);
			window.guiResume();
			window.simpleMessagePopup("Numbering Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		
		window.guiResume();
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
				+ "It is recommended to stop and save a copy of the file before proceeding. Cobbler " + eol
				+ "will do its best to add line numbers without creating any breaking changes, but carefully " + eol
				+ "examine the file after changes have been applied. Proceed with adding line numbers?";

		// ask user if they want to overwrite the file
		int result = JOptionPane.showConfirmDialog(CobblerWindow.getWindow(),
				message, "Confirmation",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		// the user chose to stop the operation and not overwrite the file
		if (result == JOptionPane.NO_OPTION) {
			return;
		} else {
			doRenumbering(text);
		}
	}

}
