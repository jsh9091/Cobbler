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

package com.horvath.cobbler.gui.syntax;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.gui.CobblerWindow;

public final class CobSyntaxTextArea extends RSyntaxTextArea {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor. 
	 */
	public CobSyntaxTextArea(int rows, int cols) {
		super(rows, cols);
		initListeners();
	}
	
	/**
	 * Listeners for changes within the text area. 
	 * Sets the state dirty and updates state data to reflect text area contents.
	 */
	private void initListeners() {
		getDocument().addDocumentListener(new DocumentListener() {
			CobblerState state = CobblerState.getInstance();

			@Override
			public void removeUpdate(DocumentEvent e) {
				doUpdates();
			}

			@Override
			public void insertUpdate(DocumentEvent e) { }

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				doUpdates();
			}

			/**
			 * Updates the state and window for text area changes.
			 */
			public void doUpdates() {
				state.setDirty(true);
				CobblerWindow window = CobblerWindow.getWindow();
				String text = window.getTextArea().getText();
				window.updateUndoRedoMenuitems();
				state.setData(text);
			}
		});
	}

}
