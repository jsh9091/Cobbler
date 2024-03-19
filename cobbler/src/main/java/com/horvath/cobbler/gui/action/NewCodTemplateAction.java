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
import com.horvath.cobbler.command.NewTemplateDocCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * Action for generating a new hello world COBOL program.  
 * @author jhorvath 
 */
public class NewCodTemplateAction extends OpenSaveAsAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (CobblerWindow.checkForDirtyState()) {
			return;
		}
				
		try {
			NewTemplateDocCmd cmd = new NewTemplateDocCmd();
			cmd.perform();
			
			if (cmd.isSuccess()) {
				// refresh GUI
				CobblerWindow.getWindow().getTextArea().setText(CobblerState.getInstance().getData());
				CobblerWindow.getWindow().getTextArea().discardAllEdits();
				CobblerWindow.getWindow().setDocumentName(CobblerState.getInstance().getFile().getName());
			}
			
		} catch (CobblerException ex) {
			Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.WARNING);
			CobblerWindow.getWindow().simpleMessagePopup("New Document Template Error", ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		
	}

}
