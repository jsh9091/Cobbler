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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.horvath.cobbler.application.CobblerApplication;
import com.horvath.cobbler.gui.action.NewDocumentAction;
import com.horvath.cobbler.gui.action.OpenFileAction;
import com.horvath.cobbler.gui.action.SaveAction;
import com.horvath.cobbler.gui.action.ShutdownAction;

/**
 * Main menu bar of application. 
 * @author jhorvath 
 */
public final class CobblerMenuBar extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	
	JMenu fileMenu;
	JMenuItem aboutItem;
	JMenuItem newItem;
	JMenuItem openItem;
	JMenuItem closeItem;
	JMenuItem saveItem;
	JMenuItem saveAsItem;
	JMenuItem quitItem;
	
	JMenu editMenu;
	JMenuItem selectAllItem;
	JMenuItem copyItem;
	JMenuItem cutItem;
	JMenuItem pasteItem;
	
	/**
	 * Constructor. 
	 */
	public CobblerMenuBar() {
		initComponents();
		configureComponents();
	}
	
	private void initComponents() {
		
		fileMenu = new JMenu("File");
		aboutItem = new JMenuItem(); 
		newItem = new JMenuItem();
		openItem = new JMenuItem();
		closeItem = new JMenuItem();
		saveItem = new JMenuItem();
		saveAsItem = new JMenuItem();
		quitItem = new JMenuItem();
		
		editMenu = new JMenu("Edit");
		selectAllItem = new JMenuItem();
		cutItem = new JMenuItem();
		copyItem = new JMenuItem();
		pasteItem = new JMenuItem();
	}
	
	private void configureComponents() {
		
		aboutItem.setAction(new AbstractAction("About Cobbler") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().simpleMessagePopup("About Cobbler v. " + CobblerApplication.APP_VERSION, 
						"Cobbler is a simple text editor.");
		    }
		});
		
		newItem.setAction(new NewDocumentAction());
		newItem.setText("New");
		
		openItem.setAction(new OpenFileAction());
		openItem.setText("Open...");
		
		closeItem.setAction(new NewDocumentAction());
		closeItem.setText("Close");
		
		saveItem.setAction(new SaveAction(SaveAction.SaveType.SAVE));
		saveItem.setText("Save");
		
		saveAsItem.setAction(new SaveAction(SaveAction.SaveType.SAVE_AS));
		saveAsItem.setText("Save As...");
		
		quitItem.setAction(new ShutdownAction());
		quitItem.setText("Quit");
		
		fileMenu.add(aboutItem);
		fileMenu.addSeparator();
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(closeItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);
		
		selectAllItem.setAction(new AbstractAction("Select All") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
			    CobblerWindow.getWindow().getTextArea().selectAll();
		    }
		});

		copyItem.setAction(new AbstractAction("Copy") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().getTextArea().copy();
		    }
		});
		
		cutItem.setAction(new AbstractAction("Cut") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().getTextArea().cut();
		    }
		});
		
		pasteItem.setAction(new AbstractAction("Paste") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
			    CobblerWindow.getWindow().getTextArea().paste();
		    }
		});
		
		editMenu.add(selectAllItem);
		editMenu.add(copyItem);
		editMenu.add(cutItem);
		editMenu.add(pasteItem);
		
		this.add(fileMenu);
		this.add(editMenu);	
	}
}
