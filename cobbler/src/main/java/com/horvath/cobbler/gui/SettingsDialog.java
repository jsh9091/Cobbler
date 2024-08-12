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
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.command.LoadSettingsCmd;
import com.horvath.cobbler.gui.action.SaveSettingsAction;

/**
 * Dialog for application settings. 
 * @author jhorvath
 */
public final class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JLabel themeMenuLabel;
	private JComboBox<String> themeMenu;
	private JLabel maxNumRecentFilesMenuLabel;
	private Integer[] maxNumRecentFilesOptions;
	private JComboBox<Integer> maxNumRecentFilesMenu;
	private JCheckBox clearRecentCheckBox;
	private JCheckBox spellcheckOnCheckBox;
	private JCheckBox showInvisibleCharactersCheckBox;
	private Integer[] addLineIncMenuOptions;
	private JLabel addLineIncMenuLabel;
	private JComboBox<Integer> addLineIncMenu;
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
		CobblerState state = CobblerState.getInstance();
		
		themeMenuLabel = new JLabel();
		themeMenu = new JComboBox<String>(state.getCurrentTheme().names());
		
		maxNumRecentFilesOptions = new Integer[LoadSettingsCmd.MAX_SUPPORTED_RECENT_FILES];
		maxNumRecentFilesMenuLabel = new JLabel();
		for (int i = 0; i < LoadSettingsCmd.MAX_SUPPORTED_RECENT_FILES; i++) {
			maxNumRecentFilesOptions[i] = i + 1;
		}
		maxNumRecentFilesMenu = new JComboBox<>(maxNumRecentFilesOptions);
		
		clearRecentCheckBox = new JCheckBox("Clear Recent menu", false);
		spellcheckOnCheckBox = new JCheckBox("Spell Checker On", state.isSpellcheckOn());
		showInvisibleCharactersCheckBox = new JCheckBox("Show Invisible Characters", state.isShowInvisibleCharacters());
		
		addLineIncMenuLabel = new JLabel();
		final int stateAddInc = state.getAddLineIncrementValue();
		addLineIncMenuOptions = LoadSettingsCmd.ADD_LINE_NUM_INCREMENT_OPTIONS;
		// if the current state value is not in our collection, but is in valid range
		if ((!Arrays.stream(addLineIncMenuOptions).anyMatch(new Integer(stateAddInc)::equals)) 
				&& LoadSettingsCmd.addLineIncrementValueInValidRange(stateAddInc)) {
			// add the value to our collection to display and sort it
			addLineIncMenuOptions = new Integer[addLineIncMenuOptions.length + 1];
			for (int i = 0; i < LoadSettingsCmd.ADD_LINE_NUM_INCREMENT_OPTIONS.length; i++) {
				addLineIncMenuOptions[i] = LoadSettingsCmd.ADD_LINE_NUM_INCREMENT_OPTIONS[i];
			}
			addLineIncMenuOptions[addLineIncMenuOptions.length - 1] = new Integer(stateAddInc);
			Arrays.sort(addLineIncMenuOptions);
		}
		addLineIncMenu = new JComboBox<Integer>(addLineIncMenuOptions);
		
		saveSettingsBtn = new JButton();
	}
	
	/**
	 * Configure the components. 
	 */
	private void configureComponents() {
		CobblerState state = CobblerState.getInstance();
		
		/* dialog */ 
		setTitle("Settings");
		setSize(290, 260);
		setResizable(false);
		setLocationRelativeTo(CobblerWindow.getWindow());
		
		/* dialog components */
		// theme menu
		themeMenuLabel.setText("Select Theme:");
		themeMenu.setSelectedItem(CobblerState.getInstance().getCurrentTheme().name());
		
		// menu to control the maximum number of recent files to track
		maxNumRecentFilesMenuLabel.setText("Num of Recent Files:");
		// verify that state value is legitimate  
		if (Arrays.stream(maxNumRecentFilesOptions).anyMatch(new Integer(state.getMaxNumOfRecentFiles())::equals)) {
			maxNumRecentFilesMenu.setSelectedItem(state.getMaxNumOfRecentFiles());
		} else {
			maxNumRecentFilesMenu.setSelectedItem(LoadSettingsCmd.DEFAULT_RECENT_FILES);
		}
		
		addLineIncMenuLabel.setText("Add Line Number Increment:");
		addLineIncMenu.setSelectedItem(state.getAddLineIncrementValue());
		
		saveSettingsBtn.setAction(new SaveSettingsAction(this));
		saveSettingsBtn.setText("Save");
	}
	
	/**
	 * Lays out the dialog GUI. 
	 */
	private void layoutComponents() {
		
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		int yPos = 0;

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = yPos;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(themeMenuLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = yPos++;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(15, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(themeMenu, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = yPos;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(maxNumRecentFilesMenuLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = yPos++;;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(maxNumRecentFilesMenu, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = yPos;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(addLineIncMenuLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = yPos++;;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 10, 10, 10);
		gbc.anchor = GridBagConstraints.WEST;
		this.add(addLineIncMenu, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = yPos++;
		gbc.gridwidth = 2;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(clearRecentCheckBox, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = yPos++;
		gbc.gridwidth = 2;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(spellcheckOnCheckBox, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = yPos++;
		gbc.gridwidth = 2;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.anchor = GridBagConstraints.CENTER;
		this.add(showInvisibleCharactersCheckBox, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = yPos++;
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

	public JCheckBox getSpellcheckOnCheckBox() {
		return spellcheckOnCheckBox;
	}

	public JCheckBox getShowEndOfLinesCheckBox() {
		return showInvisibleCharactersCheckBox;
	}

	public JComboBox<Integer> getMaxNumRecentFilesMenu() {
		return maxNumRecentFilesMenu;
	}

	public JComboBox<Integer> getAddLineIncMenu() {
		return addLineIncMenu;
	}
	
}
