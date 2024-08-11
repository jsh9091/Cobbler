package com.horvath.cobbler.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.AbstractLineNumberCmd.LineState;
import com.horvath.cobbler.command.AddLineNumbersCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * Action for adding hard coded line numbers to Cobol files. 
 * @author jhorvath
 */
public final class AddLineNumbersAction extends AbstractAddRemoveLineNumsAction {

	private static final long serialVersionUID = 1L;

	private LineState lineState;
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String text = CobblerWindow.getWindow().getTextArea().getText();
		
		this.lineState = examineLineState(text);
		
		if (lineState == LineState.INDETERMINATE) { 
			inderterminateWarningConfirmation(text);
		} else {
			doRenumbering(text);
		}
	}
	
	/**
	 * Perform numbering operation. 
	 * @param text String 
	 */
	private void doRenumbering(String text) {
		
		CobblerWindow window = CobblerWindow.getWindow();
		window.guiWait();
		
		try {
			AddLineNumbersCmd cmd = new AddLineNumbersCmd(text, this.lineState, CobblerState.getInstance().getAddLineIncrementValue());
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
