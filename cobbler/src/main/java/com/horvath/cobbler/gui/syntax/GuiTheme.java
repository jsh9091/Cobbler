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

/**
 * Enumeration for available GUI themes. 
 * @author jhorvath 
 */
public enum GuiTheme {
	Dark("/org/fife/ui/rsyntaxtextarea/themes/dark.xml"), 
	Default_Alt("/org/fife/ui/rsyntaxtextarea/themes/default-alt.xml"), 
	Default("/org/fife/ui/rsyntaxtextarea/themes/default.xml"), 
	Druid("/org/fife/ui/rsyntaxtextarea/themes/druid.xml"),
	Eclipse("/org/fife/ui/rsyntaxtextarea/themes/eclipse.xml"),
	Idea("/org/fife/ui/rsyntaxtextarea/themes/idea.xml"),
	Monokai("/org/fife/ui/rsyntaxtextarea/themes/monokai.xml"),
	VS("/org/fife/ui/rsyntaxtextarea/themes/vs.xml");
	
	private final String text;

    /**
     * Constructor. 
     * @param text String 
     */
	private GuiTheme(final String text) {
        this.text = text;
    }
	
	public String[] names() {
		String[] nameArray = new String[GuiTheme.values().length];
	
		int i = 0;
		for (GuiTheme value : GuiTheme.values()) {
			nameArray[i] = value.name();
			i++;
		}
		
		return nameArray;
	}
	
	public static GuiTheme fromString(String text) {
        for (GuiTheme theme : GuiTheme.values()) {
            if (theme.text.equalsIgnoreCase(text)) {
                return theme;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
