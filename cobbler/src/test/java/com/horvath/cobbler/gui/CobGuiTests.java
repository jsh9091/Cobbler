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

		CobblerWindow.getWindow().setVisible(false);
		CobblerWindow.getWindow().getTextArea().setText(CobblerState.getInstance().getData());
		CobblerWindow.getWindow().getTextArea().discardAllEdits();
		CobblerWindow.getWindow().setDocumentName(" ");
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
	}
	
	@Test
	public void gui_selectCopyPaste_textareaupdated() {
		CobblerWindow window = CobblerWindow.getWindow();
		CobSyntaxTextArea textarea = window.getTextArea();
		
		// text area is clear 
		Assert.assertTrue(textarea.getText().trim().isEmpty());
		
		final String text = "Some text. ";
		textarea.setText(text);
		Assert.assertTrue(textarea.getText().equals(text));
		
		// select the text
		window.getCobMenuBar().selectAllItem.doClick();
		
		// copy the text
		window.getCobMenuBar().copyItem.doClick();

		// paste the text
		window.getCobMenuBar().pasteItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text));
		
		// paste the text
		window.getCobMenuBar().pasteItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text + text));
		
		// select the text
		window.getCobMenuBar().selectAllItem.doClick();
		
		// cut the text
		window.getCobMenuBar().cutItem.doClick();
				
		// text area is clear 
		Assert.assertTrue(textarea.getText().trim().isEmpty());

		// paste the text
		window.getCobMenuBar().pasteItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text + text));
	}
	
	@Test
	public void gui_undoRedo_textAreaUpdated() {
		CobblerWindow window = CobblerWindow.getWindow();
		CobSyntaxTextArea textarea = window.getTextArea();
		
		// text area is clear 
		Assert.assertTrue(textarea.getText().trim().isEmpty());
		
		Assert.assertFalse(window.getCobMenuBar().undoItem.isEnabled());
		Assert.assertFalse(window.getCobMenuBar().redoItem.isEnabled());
		
		Assert.assertFalse(textarea.canUndo());
		Assert.assertFalse(textarea.canRedo());
		
		final String text = "Some text. ";
		textarea.setText(text);
		// select, cut, and paste the text to ensure listeners fire as expected inside unit test
		window.getCobMenuBar().selectAllItem.doClick();
		window.getCobMenuBar().cutItem.doClick();
		window.getCobMenuBar().pasteItem.doClick();
		Assert.assertTrue(textarea.getText().equals(text));
		
		Assert.assertTrue(textarea.canUndo());
		Assert.assertFalse(textarea.canRedo());
		
		Assert.assertTrue(window.getCobMenuBar().undoItem.isEnabled());
		Assert.assertFalse(window.getCobMenuBar().redoItem.isEnabled());
		
		// undo the paste
		window.getCobMenuBar().undoItem.doClick();
		
		// the text area should now be empty with the undo action applied
		Assert.assertTrue(textarea.getText().isEmpty());
		// the re-do menu item should now be available 
		Assert.assertTrue(window.getCobMenuBar().redoItem.isEnabled());
		
		// re-do the paste
		window.getCobMenuBar().redoItem.doClick();
		
		Assert.assertTrue(textarea.getText().equals(text));
	}

}
