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

package com.horvath.cobbler.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Command for reading text files located within application resources. Needed
 * because application will run as a runnable jar so file objects will not work.
 * 
 * @author jhorvath
 */
public final class ReadResourceTextFileCmd extends CobblerCommand {

	private String path;
	private ArrayList<String> resultList;

	public static final String BAD_FILEPATH = "The file path must not be null or empty.";

	public static final String INTRINSIC_FUNCTIONS = "/resources/intrinsic-functions.txt"; // TODO change location
	public static final String RESERVED_WORDS = "/resources/reserved-words.txt"; // TODO change location
	public static final String OPERATORS = "/resources/operators.txt"; // TODO change location

	/**
	 * Constructor.
	 * 
	 * @param path
	 */
	public ReadResourceTextFileCmd(String path) {
		this.path = path;
	}

	@Override
	public void perform() throws CobblerException {
		this.success = false;

		if (this.path == null || this.path.isEmpty()) {
			throw new CobblerException(BAD_FILEPATH);
		}

		Debugger.printLog("Reading resoucer file: " + this.path, this.getClass().getName());

		resultList = new ArrayList<>();

		InputStream is = getFileAsIOStream(this.path);
		loadFileContent(is);

		this.success = true;
	}

	/**
	 * Creates the input stream for reading the file.
	 * 
	 * @param filepath String
	 * @return InputStream
	 */
	private InputStream getFileAsIOStream(final String filepath) {
		InputStream ioStream = ReadResourceTextFileCmd.class.getResourceAsStream(filepath);

		if (ioStream == null) {
			throw new IllegalArgumentException(filepath + " is not found");
		}
		return ioStream;
	}

	/**
	 * Performs operations of actually reading the file and populating the return
	 * list.
	 * 
	 * @param is InputStream
	 * @throws CobblerException
	 */
	private void loadFileContent(InputStream is) throws CobblerException {
		try (InputStreamReader isr = new InputStreamReader(is); 
				BufferedReader br = new BufferedReader(isr);) {

			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) {
					resultList.add(line);
				}
			}
			is.close();
		} catch (IOException ex) {
			throw new CobblerException(ex.getMessage(), ex);
		}
	}

	public ArrayList<String> getResultList() {
		return resultList;
	}

}
