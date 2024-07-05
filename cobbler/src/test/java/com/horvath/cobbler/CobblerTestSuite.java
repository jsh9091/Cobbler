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

package com.horvath.cobbler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.horvath.cobbler.command.AddLineNumbersCmdTest;
import com.horvath.cobbler.command.CheckLineNumberStateCmdTest;
import com.horvath.cobbler.command.LoadFileCmdTest;
import com.horvath.cobbler.command.LoadSettingsCmdTest;
import com.horvath.cobbler.command.NewEmptyDocumentCmdTest;
import com.horvath.cobbler.command.NewTemplateDocCmdTest;
import com.horvath.cobbler.command.ReadResourceTextFileCmdTest;
import com.horvath.cobbler.command.RemoveLineNumbersCmdTest;
import com.horvath.cobbler.command.SaveFileCmdTest;
import com.horvath.cobbler.command.SaveSettingsCmdTest;
import com.horvath.cobbler.gui.CobGuiTests;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	LoadFileCmdTest.class,
	NewEmptyDocumentCmdTest.class,
	SaveFileCmdTest.class,
	SaveSettingsCmdTest.class,
	LoadSettingsCmdTest.class,
	NewTemplateDocCmdTest.class,
	ReadResourceTextFileCmdTest.class,
	CheckLineNumberStateCmdTest.class,
	AddLineNumbersCmdTest.class,
	RemoveLineNumbersCmdTest.class,
	CobGuiTests.class
})

public class CobblerTestSuite { }
