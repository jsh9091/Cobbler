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

package com.horvath.cobbler.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.horvath.cobbler.application.Debugger;

/**
 * Class that defines the main application window. 
 * @author jhorvath
 */
public class CobblerWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static CobblerWindow window = null;
	
	private CobblerMenuBar menuBar;
	private RSyntaxTextArea textArea;
	private RTextScrollPane scrollpane;
	
	/**
	 * Constructor. 
	 */
	private CobblerWindow() {
		super();

		Debugger.printLog("Starting init of the GUI", this.getClass().getName());
		initializeComponents();
		configureComponents();
		layoutComponents();
	}
	
	/**
	 * Returns an instance of the window. 
	 * @return CobblerWindow
	 */
	public static CobblerWindow getWindow() {
		if (window == null) {
			window = new CobblerWindow();
		}
		return window;
	}
	
	/**
	 * Initializes the components of the window. 
	 */
	private void initializeComponents() {
		menuBar = new CobblerMenuBar();

		textArea = new RSyntaxTextArea(20, 60);
		scrollpane = new RTextScrollPane(textArea);
	}
	
	/**
	 * Initializes the main window. 
	 */
	private void configureComponents() {
		
		setTitle("Cobbler"); // TODO make this more configurable 

		setLayout(new GridBagLayout());
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}
	
	/**
	 * Performs layout operations of the GUI components within the panel.
	 */
	private void layoutComponents() {

		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.NORTH;
		add(menuBar, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weighty = 1.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.SOUTH;
		add(scrollpane, gbc);
	}
	
	/**
	 * Disables the GUI with a wait cursor. 
	 * Intended to be used during complicated operations. 
	 */
	public void guiWait() {
		Debugger.printLog("Pausing the GUI", this.getClass().getName());
		Component glasspane = this.getGlassPane();
		glasspane.setVisible(true);
		glasspane.addMouseListener(new MouseAdapter() {});
		glasspane.addKeyListener(new KeyAdapter() {});
		glasspane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	/**
	 * Restores the user's ability to interact with the main GUI window. 
	 */
	public void guiResume() {
		Debugger.printLog("Resuming the GUI", this.getClass().getName());
		Component glasspane = this.getGlassPane();
		glasspane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		glasspane.setVisible(false);
	}
	
	/**
	 * Displays a simple pop-up message with an OK button. 
	 * @param title String 
	 * @param message String
	 */
	public void simpleMessagePopup(String title, String message) {
		simpleMessagePopup(title, message, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Displays a simple pop-up message with an OK button. 
	 * Configurable with a level setting (Example: info, warning, error).
	 * @param title String 
	 * @param message String 
	 * @param level int (recommend using JOptionPane level constant)
	 */
	public void simpleMessagePopup(String title, String message, int level) {
		
		if (level == JOptionPane.INFORMATION_MESSAGE) {
			// display the message with application icon
			JOptionPane.showMessageDialog(this, message, title, level, null); // TODO application icon
			
		} else {
			// for non-information levels, show default icons
			JOptionPane.showMessageDialog(this, message, title, level);
		}
	}

	public RSyntaxTextArea getTextArea() {
		return textArea;
	}


}
