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

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.command.AbstractLineNumberCmd;
import com.horvath.cobbler.gui.syntax.CobSyntaxTextArea;

/**
 * Performs some GUI tests. Tests are limited in scope as to not show 
 * any GUI components visually while unit tests are running.
 * @author jhorvath
 */
public class CobGuiTests {
	
	@Before
    public void setUp() {
    	setupTeardownHelper();
    }
	
    @After
    public void tearDown() {
    	setupTeardownHelper();
    }

    /**
     * Helper method for setting up and cleaning up window and state for tests.
     */
    private void setupTeardownHelper() {
        CobblerState state = CobblerState.getInstance();
        state.setData("");
        state.setDirty(false);
        state.setFile(new File(""));

        CobblerWindow window = CobblerWindow.getWindow();
        window.setVisible(false);
        window.getTextArea().setText(CobblerState.getInstance().getData());
        window.getTextArea().discardAllEdits();
        window.setDocumentName(" ");
    }
    
	@Test
	public void gui_newFile_guiCleared() {
		final String oldName = "OLD File.cob";
		CobblerState.getInstance().setFile(new File(oldName));
		CobblerWindow window = CobblerWindow.getWindow();
		window.setDocumentName(oldName);
		
		Assert.assertEquals(oldName, window.getDocumentName());
		
		// perform action to be tested
		CobblerWindow.getWindow().getCobMenuBar().newItem.doClick();
		
		Assert.assertTrue(window.getDocumentName().trim().isEmpty());
		Assert.assertTrue(window.getTextArea().getText().isEmpty());
		Assert.assertFalse(window.getTextArea().canUndo());
	}
	
	@Test
	public void gui_newDocTemplate_helloWorldProgramSetInGui() {
		CobblerWindow window = CobblerWindow.getWindow();
		
		// perform action to be tested
		window.getCobMenuBar().newTemplatedItem.doClick();
		
		Assert.assertTrue(CobblerState.getInstance().isDirty());
		Assert.assertTrue(window.getDocumentName().toLowerCase().contains("hello"));
		Assert.assertTrue(window.getDocumentName().toLowerCase().contains(".cob"));
		
		final String userName = System.getProperty("user.name");
		final String actual = window.getTextArea().getText();
		
		// perform spot checks
		Assert.assertTrue(actual.contains(userName));
		Assert.assertTrue(actual.contains("PROGRAM-ID. HELLO-WORLD."));
		Assert.assertTrue(actual.contains("DISPLAY \"Hello, \""));
		Assert.assertTrue(actual.contains("STOP RUN"));
		
		Assert.assertTrue(actual.length() > 350);
	}
	
	@Test
	public void gui_selectCopyPaste_textareaupdated() {
		CobblerWindow window = CobblerWindow.getWindow();
		CobSyntaxTextArea textarea = window.getTextArea();
		CobblerMenuBar menubar = window.getCobMenuBar();
		
		// text area is clear 
		Assert.assertTrue(textarea.getText().trim().isEmpty());
		
		final String text = "Some text. ";
		textarea.setText(text);
		Assert.assertTrue(textarea.getText().equals(text));
		
		// select the text
		menubar.selectAllItem.doClick();
		
		// copy the text
		menubar.copyItem.doClick();

		// paste the text
		menubar.pasteItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text));
		
		// paste the text
		menubar.pasteItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text + text));
		
		// select the text
		menubar.selectAllItem.doClick();
		
		// cut the text
		menubar.cutItem.doClick();
				
		// text area is clear 
		Assert.assertTrue(textarea.getText().trim().isEmpty());

		// paste the text
		menubar.pasteItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text + text));
	}
	
	@Test
	public void gui_undoRedo_textAreaUpdated() {
		CobblerWindow window = CobblerWindow.getWindow();
		CobSyntaxTextArea textarea = window.getTextArea();
		CobblerMenuBar menubar = window.getCobMenuBar();
		
		// text area is clear 
		Assert.assertTrue(textarea.getText().trim().isEmpty());
		
		Assert.assertFalse(menubar.undoItem.isEnabled());
		Assert.assertFalse(menubar.redoItem.isEnabled());
		
		Assert.assertFalse(textarea.canUndo());
		Assert.assertFalse(textarea.canRedo());
		
		final String text = "Some text. ";
		textarea.setText(text);
		// select, cut, and paste the text to ensure listeners fire as expected inside unit test
		menubar.selectAllItem.doClick();
		menubar.cutItem.doClick();
		menubar.pasteItem.doClick();
		Assert.assertTrue(textarea.getText().equals(text));
		
		Assert.assertTrue(textarea.canUndo());
		Assert.assertFalse(textarea.canRedo());
		
		Assert.assertTrue(menubar.undoItem.isEnabled());
		Assert.assertFalse(menubar.redoItem.isEnabled());
		
		// undo the paste
		menubar.undoItem.doClick();
		
		// the text area should now be empty with the undo action applied
		Assert.assertTrue(textarea.getText().isEmpty());
		// the re-do menu item should now be available 
		Assert.assertTrue(menubar.redoItem.isEnabled());
		
		// re-do the paste
		menubar.redoItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text));
	}
	
	@Test
	public void gui_addLineNumbs_lineNumsAdded() {
		CobblerWindow window = CobblerWindow.getWindow();
		CobSyntaxTextArea textarea = window.getTextArea();
		CobblerMenuBar menubar = window.getCobMenuBar();
		
		// pre-populate text area with Cobol code with no line numbers
		menubar.newTemplatedItem.doClick();
		Assert.assertTrue(textarea.getText().length() > 350);
		
		// click the menu item we are here to test
		menubar.addLineNumsItem.doClick();

		// collect our processed actual data
		String[] lines = AbstractLineNumberCmd.splitStringOnNewlines(textarea.getText());
		
		// the first six characters of every line should be a digit
		for (String line : lines) {
			char[] chars = line.toCharArray();
			Assert.assertTrue(Character.isDigit(chars[0]));
			Assert.assertTrue(Character.isDigit(chars[1]));
			Assert.assertTrue(Character.isDigit(chars[2]));
			Assert.assertTrue(Character.isDigit(chars[3]));
			Assert.assertTrue(Character.isDigit(chars[4]));
			Assert.assertTrue(Character.isDigit(chars[5]));
		}
	}

}
