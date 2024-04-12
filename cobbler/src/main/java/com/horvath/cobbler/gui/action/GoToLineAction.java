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

import javax.swing.UIManager;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ui.GoToDialog;

import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * Action for displaying dialog for API to allow the user to go to a specific line number in the open document. 
 * @author jhorvath
 */
public final class GoToLineAction extends CobblerAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		
		CobblerWindow window = CobblerWindow.getWindow();
		
		GoToDialog dialog = new GoToDialog(window);
		dialog.setMaxLineNumberAllowed(window.getTextArea().getLineCount());
		dialog.setVisible(true);
		int userLine = dialog.getLineNumber();
		if (userLine > 0) {
			try {
				window.getTextArea().setCaretPosition(window.getTextArea().getLineStartOffset(userLine - 1));
			} catch (BadLocationException ex) {
				UIManager.getLookAndFeel().provideErrorFeedback(window.getTextArea());
				Debugger.printLog(ex.getMessage(), this.getClass().getName(), Level.WARNING);
			}
		}
	}

}
