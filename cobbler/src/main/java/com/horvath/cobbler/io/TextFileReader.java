/* MIT License
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

package com.horvath.cobbler.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.horvath.cobbler.exception.CobblerException;

/**
 * Performs of reading text data from a given file.
 * @author jhorvath
 */
public final class TextFileReader {
	private File file;

	/**
	 * Constructor 
	 * @param file File 
	 */
	public TextFileReader(File file) {
		this.file = file;
	}

	/**
	 * Performs operations of reading the String data from a file. 
	 * @return String 
	 * @throws CobblerException
	 */
	public String read() throws CobblerException {

		StringBuilder sb = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(this.file.getAbsolutePath()))) {
			stream.forEach(s -> sb.append(s).append(System.getProperty("line.separator")));
		} catch (Exception ex) {
			throw new CobblerException("Problem reading file " + this.file.getName() + "." + ex.getMessage(), ex);
		}
		return sb.toString();
	}
}
