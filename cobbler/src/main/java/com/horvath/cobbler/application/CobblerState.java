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
	
	/**
	 * Constructor. 
	 */
	private CobblerState() {
		Debugger.printLog("Initializing state", this.getClass().getName());
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

	@Override
	public String toString() {
		return "CobblerState [file=" + file + ", dirty=" + dirty + "]";
	}
	
}
