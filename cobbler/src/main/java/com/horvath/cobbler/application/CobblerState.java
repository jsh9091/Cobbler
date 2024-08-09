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

package com.horvath.cobbler.application;

import java.io.File;
import java.util.ArrayList;

import com.horvath.cobbler.gui.syntax.GuiTheme;

/**
 * The application state.
 * Contains the working set of data for the application. 
 * @author jhorvath
 */
public final class CobblerState {

	private static CobblerState instance = null;
	
	private File file;
	private String data;
	private boolean dirty;
	private GuiTheme currentTheme;
	private ArrayList<String> recentFilesList;
	public static final int MAX_RECENT_FILES = 5;
	private boolean spellcheckOn;
	private boolean showInvisibleCharacters; 
	
	/**
	 * Constructor. 
	 */
	private CobblerState() {
		Debugger.printLog("Initializing state", this.getClass().getName());
		this.recentFilesList = new ArrayList<>();
	}
	
	/**
	 * Returns an instance of the state. 
	 * @return CobblerState
	 */
	public static CobblerState getInstance() {
		if (instance == null) {
			instance = new CobblerState();
		}
		return instance;
	}
	
	/**
	 * Updates the collection of recently opened files. 
	 * @param filepath String
	 */
	public void updateRecentFiles(String filepath) {
		
		if (recentFilesList.isEmpty()) {
			recentFilesList.add(filepath);
			return;
		}
		
		// if we already have this one, but it is not the last one opened
		if (recentFilesList.contains(filepath) && !recentFilesList.get(0).equals(filepath)) {
			// move from middle or end of list
			recentFilesList.remove(filepath);
			// to the beginning of list
			recentFilesList.add(0, filepath);
			
		} else if (!recentFilesList.contains(filepath)) {
			// we don't already have this, add it to the start
			recentFilesList.add(0, filepath);
		}
		
		// if recent files collection is greater than max, then truncate 
		while (recentFilesList.size() > MAX_RECENT_FILES) {
			recentFilesList.remove(recentFilesList.size() - 1);
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public GuiTheme getCurrentTheme() {
		if (currentTheme == null) {
			currentTheme = GuiTheme.Default;
		}
		return currentTheme;
	}

	public void setCurrentTheme(GuiTheme currentTheme) {
		this.currentTheme = currentTheme;
	}	

	public ArrayList<String> getRecentFilesList() {
		return recentFilesList;
	}

	public boolean isSpellcheckOn() {
		return spellcheckOn;
	}

	public void setSpellcheckOn(boolean spellcheckOn) {
		this.spellcheckOn = spellcheckOn;
	}

	public boolean isShowInvisibleCharacters() {
		return showInvisibleCharacters;
	}

	public void setShowInvisibleCharacters(boolean showInvisibleCharacters) {
		this.showInvisibleCharacters = showInvisibleCharacters;
	}

	@Override
	public String toString() {
		return "CobblerState [file=" + file + ", dirty=" + dirty + "]";
	}
	
}
