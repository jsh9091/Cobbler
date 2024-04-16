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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;

import com.horvath.cobbler.gui.CobblerWindow;

/**
 * Action for displaying Find and Replace dialogs. 
 * @author jhorvath
 */
public final class FindReplaceDialogAction extends CobblerAction {

	private static final long serialVersionUID = 1L;

	public enum Mode {
		FIND, REPLACE
	}

	private Mode currentMode;

	/**
	 * Constructor.
	 * 
	 * @param mode Mode
	 */
	public FindReplaceDialogAction(Mode mode) {
		currentMode = mode;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CobblerWindow window = CobblerWindow.getWindow();

		if (currentMode == Mode.FIND) {
			// if the replace dialog is still open
			if (window.getReplaceDialog() != null && window.getReplaceDialog().isVisible()) {
				window.getReplaceDialog().dispose();
			}

			FindDialog findDialog = new FindDialog(window, window);
			findDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					// clear out any stale text in status bar
					window.getStatusBar().resetBar();
				}
			});
			findDialog.setVisible(true);
			window.setFindDialog(findDialog);

		} else if (currentMode == Mode.REPLACE) {
			// if the find dialog is still open
			if (window.getFindDialog() != null && window.getFindDialog().isVisible()) {
				window.getFindDialog().dispose();
			}

			ReplaceDialog replaceDialog = new ReplaceDialog(window, window);
			replaceDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					// clear out any stale text in status bar
					window.getStatusBar().resetBar();
				}
			});
			replaceDialog.setVisible(true);
			window.setReplaceDialog(replaceDialog);
		}
	}

}
