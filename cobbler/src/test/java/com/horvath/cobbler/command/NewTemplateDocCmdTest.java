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

package com.horvath.cobbler.command;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.horvath.cobbler.application.CobblerState;
import com.horvath.cobbler.exception.CobblerException;

/**
 * Tests operations of the NewTemplateDocCmd class.
 * @author jhorvath 
 */
public class NewTemplateDocCmdTest {
	
	@Test
	public void perform_oldDataInState_StatePopulated() {
		

		CobblerState state = CobblerState.getInstance();

		// set data in state		
		state.setFile(new File("fakeFile.cob"));
		state.setData("some text");
		state.setDirty(true);
		
		try {
			NewTemplateDocCmd cmd = new NewTemplateDocCmd();
			cmd.perform();
			
			Assert.assertTrue(cmd.isSuccess());
			
			Assert.assertFalse(state.isDirty());
			Assert.assertEquals(NewTemplateDocCmd.DOCUMENT_NAME, state.getFile().getName());
			
			final String actual = state.getData();
			
			Assert.assertNotNull(actual);
			Assert.assertFalse(actual.isEmpty());
			Assert.assertTrue(actual.contains(System.getProperty("user.name")));
			
			final String thisYear = new SimpleDateFormat("yyyy").format(new Date());
			Assert.assertTrue(actual.contains(thisYear));
			
		} catch (CobblerException ex) {
			// should not get here
			Assert.fail();
		}
	}

}
