package com.horvath.cobbler.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public final class CobblerMenuBar extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	
	JMenu fileMenu;
	JMenuItem aboutItem;
	JMenuItem openItem;
	JMenuItem closeItem;
	JMenuItem saveItem;
	JMenuItem saveAsItem;
	JMenuItem quitItem;
	
	JMenu editMenu;
	JMenuItem copyItem;
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
		aboutItem = new JMenuItem("About..."); 
		openItem = new JMenuItem("Open...");
		closeItem = new JMenuItem("Close");
		saveItem = new JMenuItem("Save");
		saveAsItem = new JMenuItem("Save As...");
		quitItem = new JMenuItem();
		
		editMenu = new JMenu("Edit");
		copyItem = new JMenuItem("Copy");
		pasteItem = new JMenuItem("Paste");
	}
	
	private void configureComponents() {
		
		aboutItem.setAction(new AbstractAction("About Cobbler") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				CobblerWindow.getWindow().simpleMessagePopup("About Cobbler", "Cobbler is a simple text editor.");
		    }
		});
		
		quitItem.setAction(new AbstractAction("Quit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
		    }
		});
		
		fileMenu.add(aboutItem);
		fileMenu.addSeparator();
		fileMenu.add(openItem);
		fileMenu.add(closeItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);
		fileMenu.addSeparator();
		fileMenu.add(quitItem);
		
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		
		this.add(fileMenu);
		this.add(editMenu);	
	}
}
