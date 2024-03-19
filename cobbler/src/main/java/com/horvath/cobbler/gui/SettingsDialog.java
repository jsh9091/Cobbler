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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.gui.action.SaveSettingsAction;

/**
 * Dialog for application settings. 
 * @author jhorvath
 */
public final class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JLabel themeMenuLabel;
	private JComboBox<String> themeMenu;
	private JCheckBox clearRecentCheckBox;
	private JButton saveSettingsBtn;
	
	/**
	 * Constructor. 
	 */
	public SettingsDialog() {
		super();

		initializeComponents();
		configureComponents();
		layoutComponents();
	}
	
	/**
	 * Initializes components. 
	 */
	private void initializeComponents() {
		themeMenuLabel = new JLabel();
		themeMenu = new JComboBox<String>(CobblerState.getInstance().getCurrentTheme().names());
		clearRecentCheckBox = new JCheckBox("Clear Recent menu", false);  
		saveSettingsBtn = new JButton();
	}
	
	/**
	 * Configure the components. 
	 */
	private void configureComponents() {
		// dialog 
		setTitle("Settings");
		setSize(250, 160);
		setResizable(false);
		setLocationRelativeTo(CobblerWindow.getWindow());
		
		// dialog components 
		themeMenuLabel.setText("Select Theme:");
		themeMenu.setSelectedItem(CobblerState.getInstance().getCurrentTheme().name());
		saveSettingsBtn.setAction(new SaveSettingsAction(this));
		saveSettingsBtn.setText("Save");
	}
	
	/**
	 * Lays out the dialog GUI. 
	 */
	private void layoutComponents() {
		
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(themeMenuLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(15, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(themeMenu, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(clearRecentCheckBox, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(10, 10, 20, 10);
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(saveSettingsBtn, gbc);
	}

	public JComboBox<String> getThemeMenu() {
		return themeMenu;
	}

	public JCheckBox getClearRecentCheckBox() {
		return clearRecentCheckBox;
	}
	
}
