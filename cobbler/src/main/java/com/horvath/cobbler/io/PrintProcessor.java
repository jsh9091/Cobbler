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

/**
 * Class for low level operations for printing page. 
 * @author jhorvath
 */
public class PrintProcessor implements Printable {
	
	private String[] data; // array of lines to print
	int[] pageBreaks; // array of page break line positions
	
	/**
	 * Constructor. 
	 * @param data String 
	 */
	public PrintProcessor(String data) {
		this.data = data.split("\\R");
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
		
		final int margin = 50;

		Font font = new Font("Serif", Font.PLAIN, 10);
		FontMetrics metrics = graphics.getFontMetrics(font);
		int lineHeight = metrics.getHeight();

		if (pageBreaks == null) {
			int linesPerPage = (int) (pageFormat.getImageableHeight() / lineHeight) - 7;
			int numBreaks = (data.length - 1) / linesPerPage;
			pageBreaks = new int[numBreaks];
			for (int b = 0; b < numBreaks; b++) {
				pageBreaks[b] = (b + 1) * linesPerPage;
			}
		}

		if (pageIndex > pageBreaks.length) {
			return NO_SUCH_PAGE;
		}

		/*
		 * User (0,0) is typically outside the image-able area, so we must translate by
		 * the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		/*
		 * Draw each line that is on this page. Increment 'y' position by lineHeight for
		 * each line.
		 */
		int y = margin;
		int start = (pageIndex == 0) ? 0 : pageBreaks[pageIndex - 1];
		int end = (pageIndex == pageBreaks.length) ? data.length : pageBreaks[pageIndex];
		for (int line = start; line < end; line++) {
			y += lineHeight;
			graphics.drawString(data[line], margin, y);
		}

		/* tell the caller that this page is part of the printed document */
		return PAGE_EXISTS;
	}

}
