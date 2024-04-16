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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import com.horvath.cobbler.application.CobblerApplication;
import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.gui.action.OpenRecentAction;
import com.horvath.cobbler.gui.action.ShutdownAction;
import com.horvath.cobbler.gui.syntax.CobSyntaxTextArea;
import com.horvath.cobbler.gui.syntax.GuiTheme;

/**
 * Class that defines the main application window. 
 * @author jhorvath
 */
public final class CobblerWindow extends JFrame implements SearchListener {

	private static final long serialVersionUID = 1L;
	
	private static CobblerWindow window = null;
	
	private CobblerMenuBar cobMenuBar;
	private JPanel docNamePanel;
	private JLabel docNameLabel;
	private CobSyntaxTextArea textArea;
	private RTextScrollPane scrollpane;
	
	private FindDialog findDialog;
	private ReplaceDialog replaceDialog;
	private StatusBar statusBar;
	
	public static final String APP_ICON = "Cobber-icon.png";
	
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
		cobMenuBar = new CobblerMenuBar();
		
		docNamePanel = new JPanel();
		docNameLabel = new JLabel(" ");

		textArea = new CobSyntaxTextArea(20, 60);
		scrollpane = new RTextScrollPane(textArea);
		statusBar = new StatusBar();
		
		URL url = this.getClass().getClassLoader().getResource(APP_ICON);
		ImageIcon icon = new ImageIcon(url);
		
		setIconImage(icon.getImage());
	}
	
	/**
	 * Initializes the main window. 
	 */
	private void configureComponents() {
		
		setTitle(CobblerApplication.APP_NAME);

		setLayout(new GridBagLayout());
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		    	new ShutdownAction().shutdownApplication();
		    }
		});
		// action listener above is in charge of shutting down application
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		updateUndoRedoMenuitems();
		updateTextAreaTheme();
		updateRecentFilesMenu();
	}
	
	/**
	 * Layout method for helper layout panel. 
	 */
	private void layoutNamePanel() {
		docNamePanel.setLayout(new GridBagLayout());
		docNamePanel.add(docNameLabel);
		docNamePanel.setSize(300, 15);
		docNamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}
	
	/**
	 * Performs layout operations of the GUI components within the panel.
	 */
	private void layoutComponents() {
		
		layoutNamePanel();

		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.NORTH;
		add(cobMenuBar, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		add(docNamePanel, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weighty = 1.0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.SOUTH;
		add(scrollpane, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weighty = 0.0;
		gbc.weightx = 0.0;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.anchor = GridBagConstraints.LINE_END;
		add(statusBar, gbc);
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
	 * Returns a scaled version of the application appropriate for display in a JOptionPane.
	 * @return ImageIcon 
	 */
	public ImageIcon getAppIcon() {
		URL url =  this.getClass().getClassLoader().getResource(APP_ICON);
		ImageIcon icon = new ImageIcon(url);
		// scale the image so it looks good
		Image image = icon.getImage();
		Image scaledImage = image.getScaledInstance(60, 60, Image.SCALE_DEFAULT);
		return new ImageIcon(scaledImage);
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
			JOptionPane.showMessageDialog(this, message, title, level, getAppIcon());
			
		} else {
			// for non-information levels, show default icons
			JOptionPane.showMessageDialog(this, message, title, level);
		}
	}
	
	/**
	 * Checks the state for unsaved changes and, if there are, asks user to confirm
	 * abandoning changes. Returns true if the user chose to keep the unsaved
	 * changes, otherwise returns false.
	 * 
	 * @return boolean
	 */
	public static boolean checkForDirtyState() {

		boolean stop = false;

		// are there unsaved changes?
		if (CobblerState.getInstance().isDirty()) {
			// ask user to confirm abandoning unsaved changes
			int result = JOptionPane.showConfirmDialog(getWindow(),
					"There are unsaved changes, are you sure you want to lose these changes?", "Confirmation",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			// the user chose to stop the operation and continue with the unsaved changes
			if (result == JOptionPane.NO_OPTION) {
				stop = true;
			}
		}

		return stop;
	}

	/**
	 * Updates the enabled / disabled status of the undo and re-do menu items. 
	 */
	public void updateUndoRedoMenuitems() {
		cobMenuBar.undoItem.setEnabled(textArea.canUndo());
		cobMenuBar.redoItem.setEnabled(textArea.canRedo());
	}
	
	/**
	 * Updates the visual theme displayed in the text editor. 
	 */
	public void updateTextAreaTheme() {
		// get theme location string from state - should always have a value
		GuiTheme selectedTheme = CobblerState.getInstance().getCurrentTheme();

		try {
			Theme theme = Theme.load(getClass().getResourceAsStream(selectedTheme.toString()));
			theme.apply(textArea);
		} catch (IOException ioe) {
			Debugger.printLog("Error updating theme.", this.getClass().getName(), Level.SEVERE);
		}
	}
	
	/**
	 * Updates the recent files menu. 
	 */
	public void updateRecentFilesMenu() {
		cobMenuBar.recentFilesMenu.removeAll();
		for (String recentFile : CobblerState.getInstance().getRecentFilesList()) {
			JMenuItem item = new JMenuItem();
			item.addActionListener(new OpenRecentAction(recentFile));
			item.setText(recentFile);
			cobMenuBar.recentFilesMenu.add(item);
		}
	}
	
	@Override
	public String getSelectedText() {
		return textArea.getSelectedText();
	}

	/**
	 * Listens for events from our search dialogs and actually does the dirty
	 * work.
	 * @param se SearchEvent
	 * @author Robert Futrell
	 * @author jhorvath
	 */
	@Override
	public void searchEvent(SearchEvent se) {

		SearchEvent.Type type = se.getType();
		SearchContext context = se.getSearchContext();
		SearchResult result;

		switch (type) {
		case MARK_ALL:
			result = SearchEngine.markAll(textArea, context);
			break;
		case FIND:
			result = SearchEngine.find(textArea, context);
			if (!result.wasFound() || result.isWrapped()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			break;
		case REPLACE:
			result = SearchEngine.replace(textArea, context);
			if (!result.wasFound() || result.isWrapped()) {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}
			break;
		case REPLACE_ALL:
			result = SearchEngine.replaceAll(textArea, context);
			simpleMessagePopup("Replace Result", result.getCount() + " occurrences replaced.");
			break;
		default: 
			result = null;
		}

		String text = "";
		if (result.wasFound()) {
			text = "Text found; occurrences marked: " + result.getMarkedCount();
		} else if (type == SearchEvent.Type.MARK_ALL) {
			if (result.getMarkedCount() > 0) {
				text = "Occurrences marked: " + result.getMarkedCount();
			}
		} else {
			text = "Text not found";
		}
		statusBar.updateText(text);
	}
	
	public void setDocumentName(String name) {
		docNameLabel.setText(name);
	}
	
	public CobblerMenuBar getCobMenuBar() {
		return this.cobMenuBar;
	}

	public CobSyntaxTextArea getTextArea() {
		return textArea;
	}

	public FindDialog getFindDialog() {
		return findDialog;
	}

	public void setFindDialog(FindDialog findDialog) {
		this.findDialog = findDialog;
	}

	public ReplaceDialog getReplaceDialog() {
		return replaceDialog;
	}

	public void setReplaceDialog(ReplaceDialog replaceDialog) {
		this.replaceDialog = replaceDialog;
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	/**
	 * The status bar for this application.
	 */
	public static class StatusBar extends JPanel {

		private static final long serialVersionUID = 1L;
		private JLabel label;

		/**
		 * Constructor. 
		 */
		StatusBar() {
			label = new JLabel("Ready");
			setLayout(new BorderLayout());
			add(label, BorderLayout.LINE_START);
			add(new JLabel());
		}

		/**
		 * Updates displayed text in the status bar. 
		 * @param text String
		 */
		public void updateText(String text) {
			this.label.setText(text);
		}
		
		public void resetBar() {
			this.label.setText("Ready");
		}
	}
}
