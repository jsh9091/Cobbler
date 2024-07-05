package com.horvath.cobbler.gui;

import java.io.File;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.horvath.cobbler.application.CobblerState;

/**
 * Performs some GUI tests. Tests are limited in scope as to not show 
 * any GUI components visual while unit tests are running.
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
		CobblerWindow.getWindow().getCobMenuBar().newTemplatedItem.doClick();
		
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

}
