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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.horvath.cobbler.application.CobblerApplication;
import com.horvath.cobbler.gui.action.NewCobTemplateAction;
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
	
	protected JMenu fileMenu;
	protected JMenuItem aboutItem;
	protected JMenuItem settingItem;
	protected JMenuItem newItem;
	protected JMenuItem newTemplatedItem;
	protected JMenuItem openItem;
	protected JMenu recentFilesMenu;
	protected JMenuItem closeItem;
	protected JMenuItem saveItem;
	protected JMenuItem saveAsItem;
	protected JMenuItem quitItem;
	
	protected JMenu editMenu;
	protected JMenuItem undoItem;
	protected JMenuItem redoItem;
	protected JMenuItem selectAllItem;
	protected JMenuItem copyItem;
	protected JMenuItem cutItem;
	protected JMenuItem pasteItem;
	
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
		settingItem = new JMenuItem();
		newItem = new JMenuItem();
		newTemplatedItem = new JMenuItem();
		openItem = new JMenuItem();
		recentFilesMenu = new JMenu("Recent Files");
		closeItem = new JMenuItem();
		saveItem = new JMenuItem();
		saveAsItem = new JMenuItem();
		quitItem = new JMenuItem();
		
		editMenu = new JMenu("Edit");
		undoItem = new JMenuItem();
		redoItem = new JMenuItem();
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
						"Cobbler is a simple COBOL text editor.");
		    }
		});
		
		settingItem.addActionListener(e -> {
			SettingsDialog settingsDialog = new SettingsDialog();
			settingsDialog.setVisible(true);
		});
		settingItem.setText("Settings...");
		
		newItem.setAction(new NewDocumentAction());
		newItem.setText("New");
		newItem.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		newTemplatedItem.setAction(new NewCobTemplateAction());
		newTemplatedItem.setText("New Document Template");
		newTemplatedItem.setToolTipText("Creates a new Hello World COBOL program.");
		
		openItem.setAction(new OpenFileAction());
		openItem.setText("Open...");
		openItem.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		closeItem.setAction(new NewDocumentAction());
		closeItem.setText("Close");
		
		saveItem.setAction(new SaveAction(SaveAction.SaveType.SAVE));
		saveItem.setText("Save");
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		saveAsItem.setAction(new SaveAction(SaveAction.SaveType.SAVE_AS));
		saveAsItem.setText("Save As...");
		
		quitItem.setAction(new ShutdownAction());
		quitItem.setText("Quit");
		quitItem.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		// add file menu items to the menu
		fileMenu.add(aboutItem);
		fileMenu.add(settingItem);
		fileMenu.addSeparator();
		fileMenu.add(newItem);
		fileMenu.add(newTemplatedItem);
		fileMenu.add(openItem);
		fileMenu.add(recentFilesMenu);
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
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		undoItem.setAction(new AbstractAction("Undo") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().getTextArea().undoLastAction();;
		    }
		});
		undoItem.setAccelerator(KeyStroke.getKeyStroke('Z', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		redoItem.setAction(new AbstractAction("Redo") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().getTextArea().redoLastAction();;
		    }
		});
		redoItem.setAccelerator(KeyStroke.getKeyStroke('Y', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		copyItem.setAction(new AbstractAction("Copy") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().getTextArea().copy();
		    }
		});
		copyItem.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		cutItem.setAction(new AbstractAction("Cut") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().getTextArea().cut();
		    }
		});
		cutItem.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		pasteItem.setAction(new AbstractAction("Paste") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
			    CobblerWindow.getWindow().getTextArea().paste();
		    }
		});
		pasteItem.setAccelerator(KeyStroke.getKeyStroke('V', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		// add edit menu items to menu 
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		editMenu.addSeparator();
		editMenu.add(selectAllItem);
		editMenu.addSeparator();
		editMenu.add(copyItem);
		editMenu.add(cutItem);
		editMenu.add(pasteItem);
		
		// add menus to menu bar
		this.add(fileMenu);
		this.add(editMenu);	
	}
	
}
