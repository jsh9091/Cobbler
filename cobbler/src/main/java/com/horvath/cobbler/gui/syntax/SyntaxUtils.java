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

import java.util.ArrayList;
import java.util.logging.Level;

import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.command.ReadResourceTextFileCmd;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Utilities class for syntax related functionality.
 * @author jhorvath
 */
public class SyntaxUtils {
	
	// do not allow class to be instantiated 
	private SyntaxUtils() { }

	/**
	 * Reads in a specified file from application resources and returns a list of
	 * strings. This method will always return an array list, even if it is empty in
	 * the case something goes wrong.
	 * 
	 * @param filepath String
	 * @return ArrayList<String>
	 */
	protected static ArrayList<String> readResouceFile(final String filepath) {
		// if reading file fails, make sure we return an empty list
		ArrayList<String> list = new ArrayList<>();
		
		try {
			ReadResourceTextFileCmd cmd = new ReadResourceTextFileCmd(filepath);
			cmd.perform();

			if (cmd.isSuccess()) {
				list = cmd.getResultList();
			}
			
		} catch (CobblerException ex) {
			Debugger.printLog("FAILED to read file: " + filepath, SyntaxUtils.class.getName(), Level.SEVERE);
		}
		
		return list;
	}
	
	/**
	 * Converts a given string of text to title case. 
	 * Example input: a line of text 
	 * Example output: A Line Of Text
	 * @param input String
	 * @return String 
	 */
	protected static String toTitleCase(String input) {
		// start with clean slate of lower case letters
		input = input.toLowerCase();
		StringBuilder titleCase = new StringBuilder(input.length());
		// initially set true to convert first character
		boolean nextTitleCase = true;

		for (char ch : input.toCharArray()) {
			if (Character.isSpaceChar(ch) || ch == '-') {
				nextTitleCase = true;
				
			} else if (nextTitleCase) {
				ch = Character.toTitleCase(ch);
				nextTitleCase = false;
			}

			titleCase.append(ch);
		}
		return titleCase.toString();
	}
}
