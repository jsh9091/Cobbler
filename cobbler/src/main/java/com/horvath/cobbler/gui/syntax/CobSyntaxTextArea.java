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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.AbstractSettingsCmd;
import com.horvath.cobbler.command.ReadResourceTextFileCmd;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.gui.CobblerWindow;

/**
 * RSyntaxTextArea text area for use with COBOL. 
 * @author jhorvath
 */
public final class CobSyntaxTextArea extends RSyntaxTextArea {

	private static final long serialVersionUID = 1L;
	private SpellingParser parser = null;
	
	/**
	 * Constructor. 
	 */
	public CobSyntaxTextArea(int rows, int cols) {
		super(rows, cols);
		
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		final String style = "text/COBOL";
		atmf.putMapping(style, "com.horvath.cobbler.gui.syntax.CobolTokenMaker");
		setSyntaxEditingStyle(style);
		setCodeFoldingEnabled(false);
		
		updateShowInvisibleCharacters();
		
		// typing auto-complete 
		CompletionProvider provider = createCompletionProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		ac.install(this);
		
		try {
			AbstractSettingsCmd.setupSettingsFolderAndFile();
			File zip = new File(AbstractSettingsCmd.APP_DICTIONARY);
			if (zip.exists()) {
				parser = SpellingParser.createEnglishSpellingParser(zip, true);
				enableDisableSpellchecker();
			}
		} catch (IOException | CobblerException ex) {
			Debugger.printLog("There was a problem setting spell checker: " 
					+ ex.getMessage(), CobSyntaxTextArea.class.getName(), Level.WARNING);
		}

		// initializing listeners must come after setting code style
		initListeners();
	}
	
	@Override
	protected JPopupMenu createPopupMenu() {
		JPopupMenu menu = super.createPopupMenu();
		if (menu.getComponentCount() > 0) {
			
			JMenuItem item = (JMenuItem)menu.getComponent(menu.getComponentCount() - 1);
			if (item.getText().toLowerCase().contains("folding")) {
				menu.remove(item);
				// remove the separator bar
				menu.remove(menu.getComponentCount() - 1);
			}
		}
		return menu;
    }
	
	/**
	 * Listeners for changes within the text area. 
	 * Sets the state dirty and updates state data to reflect text area contents.
	 */
	private void initListeners() {
		getDocument().addDocumentListener(new DocumentListener() {

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
				CobblerState state = CobblerState.getInstance();
				state.setDirty(true);
				String text = getText();
				state.setData(text);
				CobblerWindow.getWindow().updateUndoRedoMenuitems();
				CobblerWindow.getWindow().updateDocumentNameDisplay(state.getFile().getName());
			}
		});
	}

	/**
	 * Creates the auto-complete provider. 
	 * @return CompletionProvider
	 */
	private CompletionProvider createCompletionProvider() {
		DefaultCompletionProvider provider = new DefaultCompletionProvider();
		
		// read Cobol reserved words from resource file and populate provider
		ArrayList<String> list = SyntaxUtils.readResouceFile(ReadResourceTextFileCmd.RESERVED_WORDS);
		populateProvider(provider, list);
		list.clear();
		
		// read Cobol functions from resource file and populate provider
		list = SyntaxUtils.readResouceFile(ReadResourceTextFileCmd.INTRINSIC_FUNCTIONS);
		populateProvider(provider, list);
		
		return provider;
	}
	
	/**
	 * Populates the auto-complete provider with data from the strings in the given list. 
	 * @param provider DefaultCompletionProvider
	 * @param list ArrayList<String>
	 */
	private void populateProvider(DefaultCompletionProvider provider, ArrayList<String> list) {
		for (String s : list) {			
			provider.addCompletion(new BasicCompletion(provider, s.toLowerCase()));
			provider.addCompletion(new BasicCompletion(provider, s.toUpperCase()));
			provider.addCompletion(new BasicCompletion(provider, SyntaxUtils.toTitleCase(s)));
		}
	}
	
	/**
	 * Enables or disables the spell checker based on state value.
	 */
	public void enableDisableSpellchecker() {
		if (CobblerState.getInstance().isSpellcheckOn() && parser != null) {
			this.addParser(parser);
		} else {
			this.removeParser(parser);
		}
	}
	
	/**
	 * Updates if the invisible characters should be displayed or not.
	 */
	public void updateShowInvisibleCharacters() {
		setEOLMarkersVisible(CobblerState.getInstance().isShowInvisibleCharacters());
		setWhitespaceVisible(CobblerState.getInstance().isShowInvisibleCharacters());
	}
}
