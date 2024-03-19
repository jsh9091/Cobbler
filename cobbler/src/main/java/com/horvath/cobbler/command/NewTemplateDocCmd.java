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
import java.text.SimpleDateFormat;
import java.util.Date;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.application.Debugger;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Command for generating a new hello world COBOL program. 
 * @author jhorvath 
 */
public final class NewTemplateDocCmd extends CobblerCommand {
	
	private static final String EOL = System.lineSeparator();
	private static final String COMMENT_SPACE = "      ";
	private static final String INDENT_SPACE = "       ";
	public static final String DOCUMENT_NAME = "HelloWorld.cob";
	private String username;
	
	/**
	 * Constructor. 
	 */
	public NewTemplateDocCmd() {
		username = System.getProperty("user.name");
	}

	@Override
	public void perform() throws CobblerException {
		Debugger.printLog("Creating a new hello world docuemnt", this.getClass().getName());
		
		final String contents = buildDocumentContents();
		
		CobblerState state = CobblerState.getInstance();
		state.setData(contents);
		state.setFile(new File("HelloWorld.cob"));
		state.setDirty(false);

		success = true;
	}
	
	/**
	 * Builds the contents of the hello world COBOL program. 
	 * @return String 
	 */
	private String buildDocumentContents() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(COMMENT_SPACE);
		sb.append("* Auto generated hello world file ");
		sb.append(EOL);
		
		sb.append(INDENT_SPACE);
		sb.append("IDENTIFICATION DIVISION. ");
		sb.append(EOL);
		
		sb.append(INDENT_SPACE);
		sb.append("PROGRAM-ID. HELLO-WORLD. ");
		sb.append(EOL);
		
		sb.append(INDENT_SPACE);
		sb.append("AUTHOR. ");
		sb.append(username);
		sb.append(EOL);
		
		sb.append(INDENT_SPACE);
		sb.append("DATE-WRITTEN. ");
		sb.append(getCurrentDate());
		sb.append(EOL);
		
		sb.append(INDENT_SPACE);
		sb.append("ENVIRONMENT DIVISION. ");
		sb.append(EOL);
		
		sb.append(INDENT_SPACE);
		sb.append("DATA DIVISION. ");
		sb.append(EOL);
		
		sb.append(INDENT_SPACE);
		sb.append("WORKING-STORAGE SECTION. ");
		sb.append(EOL);
		
		sb.append(EOL);

		sb.append(INDENT_SPACE);
		sb.append("01 MyName PIC X(30) VALUE \"");
		sb.append(username);
		sb.append(".\".");
		sb.append(EOL);

		sb.append(EOL);

		sb.append(INDENT_SPACE);
		sb.append("DISPLAY \"Hello, \" MyName. ");
		sb.append(EOL);

		sb.append(EOL);

		sb.append(INDENT_SPACE);
		sb.append("STOP RUN. ");
		sb.append(EOL);

		return sb.toString();
	}
	
	/**
	 * Generates the current date in a human readable format. 
	 * @return String 
	 */
	private String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		return sdf.format(date); 
	}

}
