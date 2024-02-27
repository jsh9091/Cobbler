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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.horvath.cobbler.exception.CobblerException;

/**
 * Performs of writing given string data to a given file.
 * @author jhorvath
 */
public class TextFileWriter {
	
	private File file;
	private String data;

	/**
	 * Constructor.
	 * @param data String
	 * @param file File 
	 */
	public TextFileWriter(String data, File file) {
		this.data = data;
		this.file = file;
	}

	/**
	 * Performs operations of writing the String data to a file. 
	 * @throws CobblerException
	 */
	public void write() throws CobblerException {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(this.file.getAbsolutePath()))) {
			writer.write(this.data);
			
		} catch (IOException ex) {
			throw new CobblerException("Unable to write data to file.", ex);
		}
	}

}
