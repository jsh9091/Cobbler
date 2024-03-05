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

import java.io.File;
import java.util.logging.Level;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;
import com.horvath.cobbler.io.TextFileWriter;

/**
 * Command for saving a file to disk. 
 * @author jhorvath 
 */
public class SaveFileCmd extends CobblerCommand {
	
	private File file;
	
	public static final String ERROR_FILE_IS_NULL = "The file must not be null.";
	public static final String ERROR_UNKOWN_LOAD_PROBLEM = "There was an unexpected problem loading the file.";
	
	/**
	 * Constructor. 
	 * 
	 * @param file File 
	 */
	public SaveFileCmd(File file) {
		this.file = file;
	}

	@Override
	public void perform() throws CobblerException {
		
		this.success = false;
		
		if (file == null) {
			this.message = ERROR_FILE_IS_NULL;
			Debugger.printLog(this.message, this.getClass().getName(), Level.WARNING);
			return;
		}
		
		Debugger.printLog("Saving file " + file.getName(), this.getClass().getName());
		
		CobblerState state = CobblerState.getInstance();
		
		String data = state.getData();
		try {
			TextFileWriter writer = new TextFileWriter(data, file);
			writer.write();
			
			// update file in the state 
			state.setFile(this.file);
			// clear the dirty flag
			state.setDirty(false);
			this.success = true;
			
		} catch (CobblerException ex) {
			throw new CobblerException(ERROR_UNKOWN_LOAD_PROBLEM);
		}
	}

}
