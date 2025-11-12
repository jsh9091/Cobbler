/*
 * MIT License
 * 
 * Copyright (c) 2025 Joshua Horvath
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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Class for low level operations for printing page. 
 * @author jhorvath
 */
public class PrintProcessor implements Printable {
	
	private String data;
	
	/**
	 * Constructor. 
	 * @param data String 
	 */
	public PrintProcessor(String data) {
		this.data = data;
	}

	/**
	 * Defines operations for printing the page. 
	 * 
	 * @param graphics Graphics
	 * @param pageFormat PageFormat
	 * @param pageIndex int
	 * @return int
	 */
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

	    if (pageIndex > 0) {
	        return NO_SUCH_PAGE;
	    }

	    Graphics2D graphics2d = (Graphics2D) graphics;
	    int x = (int) pageFormat.getImageableX();
	    int y = (int) pageFormat.getImageableY();
	    graphics2d.translate(x, y); 

	    Font font = new Font("Monospaced", Font.PLAIN, 10);
	    FontMetrics metrics = graphics.getFontMetrics(font);
	    // get the height for updating y-position in writing 
	    int lineHeight = metrics.getHeight();

	    BufferedReader bufReader = new BufferedReader(new StringReader(data));

	    try {
	        String line;
	        x += 50;
	        y += 50;
	        while ((line = bufReader.readLine()) != null) {
	            y += lineHeight;
	            graphics2d.drawString(line, x, y);
	        }
	    } catch (IOException ex)  {
	        throw new PrinterException(ex.getMessage());
	    }

	    return PAGE_EXISTS;
	}

}
