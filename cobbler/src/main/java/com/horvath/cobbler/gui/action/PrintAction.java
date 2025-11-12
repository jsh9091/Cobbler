/*
 * MIT License
 * 
 * Copyright (c) 2025 Joshua Horvath
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
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JOptionPane;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.gui.CobblerWindow;
import com.horvath.cobbler.io.PrintProcessor;

/**
 * Action for printing the open file to a physical printer. 
 * @author jhorvath
 */
public class PrintAction extends CobblerAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		// get the current contents of the document from state
		final String code = CobblerState.getInstance().getData();
		
		// if there is nothing to print
		if (code.trim().isEmpty()) {
			CobblerWindow.getWindow().simpleMessagePopup("Printing Stopped", 
					"The current document is empty. " + System.lineSeparator() + "Printing operation stopped.");
			return;
		}
		
		// set up printer code
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setJobName(CobblerState.getInstance().getFile().getName());
		job.setPrintable(new PrintProcessor(code));
		
		// if the user did not cancel
		if (job.printDialog()) {
			try {
				// fire the operation of sending print job to the printer
				job.print();
			} catch (PrinterException ex) {
				CobblerWindow.getWindow().simpleMessagePopup("Printing Error",
						"There was a problem printing the document: " + ex.getMessage(), 
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
