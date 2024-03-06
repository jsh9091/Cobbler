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

package com.horvath.cobbler.gui.syntax;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

public class CobolTokenMaker extends AbstractTokenMaker {
	protected Segment s;

	protected int start; // Just for states.
	protected int offsetShift; // As parser always starts at 0, but our line doesn't.

	int currentTokenStart;
	int currentTokenType;

	/**
	 * Returns a list of tokens representing the given text.
	 * Copied from RSyntaxTextArea tutorial. 
	 *
	 * @param text The text to break into tokens.
	 * @param startTokenType The token with which to start tokenizing.
	 * @param startOffset The offset at which the line of tokens begins.
	 * @return A linked list of tokens representing <code>text</code>.
	 * @author bobbylight
	 */
	public Token getTokenList(Segment text, int startTokenType, int startOffset) {

	   resetTokenList();

	   char[] array = text.array;
	   int offset = text.offset;
	   int count = text.count;
	   int end = offset + count;

	   // Token starting offsets are always of the form:
	   // 'startOffset + (currentTokenStart-offset)', but since startOffset and
	   // offset are constant, tokens' starting positions become:
	   // 'newStartOffset+currentTokenStart'.
	   int newStartOffset = startOffset - offset;

	   currentTokenStart = offset;
	   currentTokenType  = startTokenType;

	   for (int i=offset; i<end; i++) {

	      char c = array[i];

	      switch (currentTokenType) {

	         case Token.NULL:

	            currentTokenStart = i;   // Starting a new token here.

	            switch (c) {

	               case ' ':
	               case '\t':
	                  currentTokenType = Token.WHITESPACE;
	                  break;

	               case '"':
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               case '#':
	                  currentTokenType = Token.COMMENT_EOL;
	                  break;

	               default:
	                  if (RSyntaxUtilities.isDigit(c)) {
	                     currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
	                     break;
	                  }
	                  else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
	                     currentTokenType = Token.IDENTIFIER;
	                     break;
	                  }
	                  
	                  // Anything not currently handled - mark as an identifier
	                  currentTokenType = Token.IDENTIFIER;
	                  break;

	            } // End of switch (c).

	            break;

	         case Token.WHITESPACE:

	            switch (c) {

	               case ' ':
	               case '\t':
	                  break;   // Still whitespace.

	               case '"':
	                  addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               case '#':
	                  addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.COMMENT_EOL;
	                  break;

	               default:   // Add the whitespace token and start anew.

	                  addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;

	                  if (RSyntaxUtilities.isDigit(c)) {
	                     currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
	                     break;
	                  }
	                  else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
	                     currentTokenType = Token.IDENTIFIER;
	                     break;
	                  }

	                  // Anything not currently handled - mark as identifier
	                  currentTokenType = Token.IDENTIFIER;

	            } // End of switch (c).

	            break;

	         default: // Should never happen
	         case Token.IDENTIFIER:

	            switch (c) {

	               case ' ':
	               case '\t':
	                  addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.WHITESPACE;
	                  break;

	               case '"':
	                  addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               default:
	                  if (RSyntaxUtilities.isLetterOrDigit(c) || c=='/' || c=='_') {
	                     break;   // Still an identifier of some type.
	                  }
	                  // Otherwise, we're still an identifier (?).

	            } // End of switch (c).

	            break;

	         case Token.LITERAL_NUMBER_DECIMAL_INT:

	            switch (c) {

	               case ' ':
	               case '\t':
	                  addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.WHITESPACE;
	                  break;

	               case '"':
	                  addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               default:

	                  if (RSyntaxUtilities.isDigit(c)) {
	                     break;   // Still a literal number.
	                  }

	                  // Otherwise, remember this was a number and start over.
	                  addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
	                  i--;
	                  currentTokenType = Token.NULL;

	            } // End of switch (c).

	            break;

	         case Token.COMMENT_EOL:
	            i = end - 1;
	            addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
	            // We need to set token type to null so at the bottom we don't add one more token.
	            currentTokenType = Token.NULL;
	            break;

	         case Token.LITERAL_STRING_DOUBLE_QUOTE:
	            if (c=='"') {
	               addToken(text, currentTokenStart,i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset+currentTokenStart);
	               currentTokenType = Token.NULL;
	            }
	            break;

	      } // End of switch (currentTokenType).

	   } // End of for (int i=offset; i<end; i++).

	   switch (currentTokenType) {

	      // Remember what token type to begin the next line with.
	      case Token.LITERAL_STRING_DOUBLE_QUOTE:
	         addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
	         break;

	      // Do nothing if everything was okay.
	      case Token.NULL:
	         addNullToken();
	         break;

	      // All other token types don't continue to the next line...
	      default:
	         addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
	         addNullToken();

	   }

	   // Return the first token in our linked list.
	   return firstToken;

	}

	@Override
	public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
		// This assumes all keywords, etc. were parsed as "identifiers."
		if (tokenType == Token.IDENTIFIER) {
			int value = wordsToHighlight.get(segment, start, end);
			if (value != -1) {
				tokenType = value;
			}
		}
		super.addToken(segment, start, end, tokenType, startOffset);
	}

	@Override
	public TokenMap getWordsToHighlight() {

		TokenMap tokenMap = new TokenMap();

		tokenMap.put("-", Token.RESERVED_WORD);
		tokenMap.put("*", Token.RESERVED_WORD);
		tokenMap.put("/", Token.RESERVED_WORD);
		tokenMap.put("**", Token.RESERVED_WORD);
		tokenMap.put(">", Token.RESERVED_WORD);
		tokenMap.put("<", Token.RESERVED_WORD);
		tokenMap.put("=", Token.RESERVED_WORD);
		tokenMap.put("==", Token.RESERVED_WORD);
		tokenMap.put(">=", Token.RESERVED_WORD);
		tokenMap.put("<=", Token.RESERVED_WORD);
		tokenMap.put("<>", Token.RESERVED_WORD);
		tokenMap.put("*>", Token.RESERVED_WORD);
		tokenMap.put(">>", Token.RESERVED_WORD);
		tokenMap.put("ACCEPT", Token.RESERVED_WORD);
		tokenMap.put("ACCESS", Token.RESERVED_WORD);
		tokenMap.put("ADD", Token.RESERVED_WORD);
		tokenMap.put("ADDRESS", Token.RESERVED_WORD);
		tokenMap.put("ADVANCING", Token.RESERVED_WORD);
		tokenMap.put("AFTER", Token.RESERVED_WORD);
		tokenMap.put("ALL", Token.RESERVED_WORD);
		tokenMap.put("ALLOWING", Token.RESERVED_WORD);
		tokenMap.put("ALPHABET", Token.RESERVED_WORD);
		tokenMap.put("ALPHABETIC", Token.RESERVED_WORD);
		tokenMap.put("ALPHABETIC--LOWER", Token.RESERVED_WORD);
		tokenMap.put("ALPHABETIC--UPPER", Token.RESERVED_WORD);
		tokenMap.put("ALPHANUMERIC", Token.RESERVED_WORD);
		tokenMap.put("ALPHANUMERIC--EDITED", Token.RESERVED_WORD);
		tokenMap.put("ALSO", Token.RESERVED_WORD);
		tokenMap.put("ALTER", Token.RESERVED_WORD);
		tokenMap.put("ALTERNATE", Token.RESERVED_WORD);
		tokenMap.put("AND", Token.RESERVED_WORD);
		tokenMap.put("ANY", Token.RESERVED_WORD);
		tokenMap.put("APPLY", Token.RESERVED_WORD);
		tokenMap.put("ARE", Token.RESERVED_WORD);
		tokenMap.put("AREA", Token.RESERVED_WORD);
		tokenMap.put("AREAS", Token.RESERVED_WORD);
		tokenMap.put("ASCENDING", Token.RESERVED_WORD);
		tokenMap.put("ASSIGN", Token.RESERVED_WORD);
		tokenMap.put("AT", Token.RESERVED_WORD);
		tokenMap.put("AUTHOR", Token.RESERVED_WORD);
		tokenMap.put("AUTO [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("AUTOMATIC", Token.RESERVED_WORD);
		tokenMap.put("AUTOTERMINATE", Token.RESERVED_WORD);
		tokenMap.put("BACKGROUND-COLOR [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("BATCH", Token.RESERVED_WORD);
		tokenMap.put("BEFORE", Token.RESERVED_WORD);
		tokenMap.put("BEGINNING", Token.RESERVED_WORD);
		tokenMap.put("BELL [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("BINARY", Token.RESERVED_WORD);
		tokenMap.put("BINARY-CHAR [200X]", Token.RESERVED_WORD);
		tokenMap.put("BINARY-DOUBLE [200X]", Token.RESERVED_WORD);
		tokenMap.put("BINARY-LONG [200X]", Token.RESERVED_WORD);
		tokenMap.put("BINARY-SHORT [200X]", Token.RESERVED_WORD);
		tokenMap.put("BIT", Token.RESERVED_WORD);
		tokenMap.put("BITS", Token.RESERVED_WORD);
		tokenMap.put("BLANK", Token.RESERVED_WORD);
		tokenMap.put("BLINK [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("BLINKING", Token.RESERVED_WORD);
		tokenMap.put("BLOCK", Token.RESERVED_WORD);
		tokenMap.put("BOLD", Token.RESERVED_WORD);
		tokenMap.put("BOOLEAN", Token.RESERVED_WORD);
		tokenMap.put("BOTTOM", Token.RESERVED_WORD);
		tokenMap.put("BY", Token.RESERVED_WORD);
		tokenMap.put("CALL", Token.RESERVED_WORD);
		tokenMap.put("CANCEL", Token.RESERVED_WORD);
		tokenMap.put("CD", Token.RESERVED_WORD);
		tokenMap.put("CF", Token.RESERVED_WORD);
		tokenMap.put("CH", Token.RESERVED_WORD);
		tokenMap.put("CHANGED", Token.RESERVED_WORD);
		tokenMap.put("CHARACTER", Token.RESERVED_WORD);
		tokenMap.put("CHARACTERS", Token.RESERVED_WORD);
		tokenMap.put("CLASS", Token.RESERVED_WORD);
		tokenMap.put("CLOCK-UNITS", Token.RESERVED_WORD);
		tokenMap.put("CLOSE", Token.RESERVED_WORD);
		tokenMap.put("COBOL", Token.RESERVED_WORD);
		tokenMap.put("CODE", Token.RESERVED_WORD);
		tokenMap.put("CODE-SET", Token.RESERVED_WORD);
		tokenMap.put("COL [200X]", Token.RESERVED_WORD);
		tokenMap.put("COLLATING", Token.RESERVED_WORD);
		tokenMap.put("COLUMN", Token.RESERVED_WORD);
		tokenMap.put("COMMA", Token.RESERVED_WORD);
		tokenMap.put("COMMIT", Token.RESERVED_WORD);
		tokenMap.put("COMMON", Token.RESERVED_WORD);
		tokenMap.put("COMMUNICATION", Token.RESERVED_WORD);
		tokenMap.put("COMP", Token.RESERVED_WORD);
		tokenMap.put("COMP-1", Token.RESERVED_WORD);
		tokenMap.put("COMP-2", Token.RESERVED_WORD);
		tokenMap.put("COMP-3", Token.RESERVED_WORD);
		tokenMap.put("COMP-4", Token.RESERVED_WORD);
		tokenMap.put("COMP-5", Token.RESERVED_WORD);
		tokenMap.put("COMP-6", Token.RESERVED_WORD);
		tokenMap.put("COMP-X", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL-1", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL-2", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL-3", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL-4", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL-5", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL-6", Token.RESERVED_WORD);
		tokenMap.put("COMPUTATIONAL-X", Token.RESERVED_WORD);
		tokenMap.put("COMPUTE", Token.RESERVED_WORD);
		tokenMap.put("CONCURRENT", Token.RESERVED_WORD);
		tokenMap.put("CONFIGURATION", Token.RESERVED_WORD);
		tokenMap.put("CONNECT", Token.RESERVED_WORD);
		tokenMap.put("CONTAIN", Token.RESERVED_WORD);
		tokenMap.put("CONTAINS", Token.RESERVED_WORD);
		tokenMap.put("CONTENT", Token.RESERVED_WORD);
		tokenMap.put("CONTINUE", Token.RESERVED_WORD);
		tokenMap.put("CONTROL", Token.RESERVED_WORD);
		tokenMap.put("CONTROLS", Token.RESERVED_WORD);
		tokenMap.put("CONVERSION", Token.RESERVED_WORD);
		tokenMap.put("CONVERTING", Token.RESERVED_WORD);
		tokenMap.put("COPY", Token.RESERVED_WORD);
		tokenMap.put("CORE-INDEX", Token.RESERVED_WORD);
		tokenMap.put("CORR", Token.RESERVED_WORD);
		tokenMap.put("CORRESPONDING", Token.RESERVED_WORD);
		tokenMap.put("COUNT", Token.RESERVED_WORD);
		tokenMap.put("CRT", Token.RESERVED_WORD);
		tokenMap.put("CURRENCY", Token.RESERVED_WORD);
		tokenMap.put("CURRENT", Token.RESERVED_WORD);
		tokenMap.put("CURSOR", Token.RESERVED_WORD);
		tokenMap.put("DATA", Token.RESERVED_WORD);
		tokenMap.put("DATE", Token.RESERVED_WORD);
		tokenMap.put("DATE-COMPILED", Token.RESERVED_WORD);
		tokenMap.put("DATE-WRITTEN", Token.RESERVED_WORD);
		tokenMap.put("DAY", Token.RESERVED_WORD);
		tokenMap.put("DAY-OF-WEEK", Token.RESERVED_WORD);
		tokenMap.put("DB", Token.RESERVED_WORD);
		tokenMap.put("DB-ACCESS-CONTROL-KEY", Token.RESERVED_WORD);
		tokenMap.put("DB-CONDITION", Token.RESERVED_WORD);
		tokenMap.put("DB-CURRENT-RECORD-ID", Token.RESERVED_WORD);
		tokenMap.put("DB-CURRENT-RECORD-NAME", Token.RESERVED_WORD);
		tokenMap.put("DB-EXCEPTION", Token.RESERVED_WORD);
		tokenMap.put("DB-KEY", Token.RESERVED_WORD);
		tokenMap.put("DB-RECORD-NAME", Token.RESERVED_WORD);
		tokenMap.put("DB-SET-NAME", Token.RESERVED_WORD);
		tokenMap.put("DB-STATUS", Token.RESERVED_WORD);
		tokenMap.put("DB-UWA", Token.RESERVED_WORD);
		tokenMap.put("DBCS", Token.RESERVED_WORD);
		tokenMap.put("DBKEY", Token.RESERVED_WORD);
		tokenMap.put("DE", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-CONTENTS", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-ITEM", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-LENGTH", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-LINE", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-NAME", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-NUMERIC-CONTENTS", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SIZE", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-START", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SUB", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SUB-1", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SUB-2", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SUB-3", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SUB-ITEM", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SUB-N", Token.RESERVED_WORD);
		tokenMap.put("DEBUG-SUB-NUM", Token.RESERVED_WORD);
		tokenMap.put("DEBUGGING", Token.RESERVED_WORD);
		tokenMap.put("DECIMAL-POINT", Token.RESERVED_WORD);
		tokenMap.put("DECLARATIVES", Token.RESERVED_WORD);
		tokenMap.put("DEFAULT", Token.RESERVED_WORD);
		tokenMap.put("DELETE", Token.RESERVED_WORD);
		tokenMap.put("DELIMITED", Token.RESERVED_WORD);
		tokenMap.put("DELIMITER", Token.RESERVED_WORD);
		tokenMap.put("DEPENDENCY", Token.RESERVED_WORD);
		tokenMap.put("DEPENDING", Token.RESERVED_WORD);
		tokenMap.put("DESCENDING", Token.RESERVED_WORD);
		tokenMap.put("DESCRIPTOR", Token.RESERVED_WORD);
		tokenMap.put("DESTINATION", Token.RESERVED_WORD);
		tokenMap.put("DETAIL", Token.RESERVED_WORD);
		tokenMap.put("DICTIONARY", Token.RESERVED_WORD);
		tokenMap.put("DISABLE", Token.RESERVED_WORD);
		tokenMap.put("DISCONNECT", Token.RESERVED_WORD);
		tokenMap.put("DISP", Token.RESERVED_WORD);
		tokenMap.put("DISPLAY", Token.RESERVED_WORD);
		tokenMap.put("DISPLAY-1", Token.RESERVED_WORD);
		tokenMap.put("DISPLAY-6", Token.RESERVED_WORD);
		tokenMap.put("DISPLAY-7", Token.RESERVED_WORD);
		tokenMap.put("DISPLAY-9", Token.RESERVED_WORD);
		tokenMap.put("DIVIDE", Token.RESERVED_WORD);
		tokenMap.put("DIVISION", Token.RESERVED_WORD);
		tokenMap.put("DOES", Token.RESERVED_WORD);
		tokenMap.put("DOWN", Token.RESERVED_WORD);
		tokenMap.put("DUPLICATE", Token.RESERVED_WORD);
		tokenMap.put("DUPLICATES", Token.RESERVED_WORD);
		tokenMap.put("ECHO", Token.RESERVED_WORD);
		tokenMap.put("EDITING", Token.RESERVED_WORD);
		tokenMap.put("EGI", Token.RESERVED_WORD);
		tokenMap.put("EJECT", Token.RESERVED_WORD);
		tokenMap.put("ELSE", Token.RESERVED_WORD);
		tokenMap.put("EMI", Token.RESERVED_WORD);
		tokenMap.put("EMPTY", Token.RESERVED_WORD);
		tokenMap.put("ENABLE", Token.RESERVED_WORD);
		tokenMap.put("END", Token.RESERVED_WORD);
		tokenMap.put("END-ACCEPT", Token.RESERVED_WORD);
		tokenMap.put("END-ADD", Token.RESERVED_WORD);
		tokenMap.put("END-CALL", Token.RESERVED_WORD);
		tokenMap.put("END-COMMIT", Token.RESERVED_WORD);
		tokenMap.put("END-COMPUTE", Token.RESERVED_WORD);
		tokenMap.put("END-CONNECT", Token.RESERVED_WORD);
		tokenMap.put("END-DELETE", Token.RESERVED_WORD);
		tokenMap.put("END-DISCONNECT", Token.RESERVED_WORD);
		tokenMap.put("END-DIVIDE", Token.RESERVED_WORD);
		tokenMap.put("END-ERASE", Token.RESERVED_WORD);
		tokenMap.put("END-EVALUATE", Token.RESERVED_WORD);
		tokenMap.put("END-FETCH", Token.RESERVED_WORD);
		tokenMap.put("END-FIND", Token.RESERVED_WORD);
		tokenMap.put("END-FINISH", Token.RESERVED_WORD);
		tokenMap.put("END-FREE", Token.RESERVED_WORD);
		tokenMap.put("END-GET", Token.RESERVED_WORD);
		tokenMap.put("END-IF", Token.RESERVED_WORD);
		tokenMap.put("END-KEEP", Token.RESERVED_WORD);
		tokenMap.put("END-MODIFY", Token.RESERVED_WORD);
		tokenMap.put("END-MULTIPLY", Token.RESERVED_WORD);
		tokenMap.put("END-OF-PAGE", Token.RESERVED_WORD);
		tokenMap.put("END-PERFORM", Token.RESERVED_WORD);
		tokenMap.put("END-READ", Token.RESERVED_WORD);
		tokenMap.put("END-READY", Token.RESERVED_WORD);
		tokenMap.put("END-RECEIVE", Token.RESERVED_WORD);
		tokenMap.put("END-RECONNECT", Token.RESERVED_WORD);
		tokenMap.put("END-RETURN", Token.RESERVED_WORD);
		tokenMap.put("END-REWRITE", Token.RESERVED_WORD);
		tokenMap.put("END-ROLLBACK", Token.RESERVED_WORD);
		tokenMap.put("END-SEARCH", Token.RESERVED_WORD);
		tokenMap.put("END-START", Token.RESERVED_WORD);
		tokenMap.put("END-STORE", Token.RESERVED_WORD);
		tokenMap.put("END-STRING", Token.RESERVED_WORD);
		tokenMap.put("END-SUBTRACT", Token.RESERVED_WORD);
		tokenMap.put("END-UNSTRING", Token.RESERVED_WORD);
		tokenMap.put("END-WRITE", Token.RESERVED_WORD);
		tokenMap.put("ENDING", Token.RESERVED_WORD);
		tokenMap.put("ENTER", Token.RESERVED_WORD);
		tokenMap.put("ENTRY", Token.RESERVED_WORD);
		tokenMap.put("ENVIRONMENT", Token.RESERVED_WORD);
		tokenMap.put("EOL [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("EOP", Token.RESERVED_WORD);
		tokenMap.put("EOS [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("EQUAL", Token.RESERVED_WORD);
		tokenMap.put("EQUALS", Token.RESERVED_WORD);
		tokenMap.put("ERASE [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("ERROR", Token.RESERVED_WORD);
		tokenMap.put("ESI", Token.RESERVED_WORD);
		tokenMap.put("EVALUATE", Token.RESERVED_WORD);
		tokenMap.put("EVERY", Token.RESERVED_WORD);
		tokenMap.put("EXAMINE", Token.RESERVED_WORD);
		tokenMap.put("EXCEEDS", Token.RESERVED_WORD);
		tokenMap.put("EXCEPTION", Token.RESERVED_WORD);
		tokenMap.put("EXCLUSIVE", Token.RESERVED_WORD);
		tokenMap.put("EXHIBIT", Token.RESERVED_WORD);
		tokenMap.put("EXIT", Token.RESERVED_WORD);
		tokenMap.put("EXOR", Token.RESERVED_WORD);
		tokenMap.put("EXTEND", Token.RESERVED_WORD);
		tokenMap.put("EXTERNAL", Token.RESERVED_WORD);
		tokenMap.put("FAILURE", Token.RESERVED_WORD);
		tokenMap.put("FALSE", Token.RESERVED_WORD);
		tokenMap.put("FD", Token.RESERVED_WORD);
		tokenMap.put("FETCH", Token.RESERVED_WORD);
		tokenMap.put("FILE", Token.RESERVED_WORD);
		tokenMap.put("FILE-CONTROL", Token.RESERVED_WORD);
		tokenMap.put("FILLER", Token.RESERVED_WORD);
		tokenMap.put("FINAL", Token.RESERVED_WORD);
		tokenMap.put("FIND", Token.RESERVED_WORD);
		tokenMap.put("FINISH", Token.RESERVED_WORD);
		tokenMap.put("FIRST", Token.RESERVED_WORD);
		tokenMap.put("FLOAT-EXTENDED [200X]", Token.RESERVED_WORD);
		tokenMap.put("FLOAT-LONG [200X]", Token.RESERVED_WORD);
		tokenMap.put("FLOAT-SHORT [200X]", Token.RESERVED_WORD);
		tokenMap.put("FOOTING", Token.RESERVED_WORD);
		tokenMap.put("FOR", Token.RESERVED_WORD);
		tokenMap.put("FOREGROUND-COLOR [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("FREE", Token.RESERVED_WORD);
		tokenMap.put("FROM", Token.RESERVED_WORD);
		tokenMap.put("FULL [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("FUNCTION", Token.RESERVED_WORD);
		tokenMap.put("GENERATE", Token.RESERVED_WORD);
		tokenMap.put("GET", Token.RESERVED_WORD);
		tokenMap.put("GIVING", Token.RESERVED_WORD);
		tokenMap.put("GLOBAL", Token.RESERVED_WORD);
		tokenMap.put("GO", Token.RESERVED_WORD);
		tokenMap.put("GOBACK", Token.RESERVED_WORD);
		tokenMap.put("GREATER", Token.RESERVED_WORD);
		tokenMap.put("GROUP", Token.RESERVED_WORD);
		tokenMap.put("HEADING", Token.RESERVED_WORD);
		tokenMap.put("HIGH-VALUE", Token.RESERVED_WORD);
		tokenMap.put("HIGH-VALUES", Token.RESERVED_WORD);
		tokenMap.put("HIGHLIGHT [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("I-O", Token.RESERVED_WORD);
		tokenMap.put("I-O-CONTROL", Token.RESERVED_WORD);
		tokenMap.put("ID", Token.RESERVED_WORD);
		tokenMap.put("IDENT", Token.RESERVED_WORD);
		tokenMap.put("IDENTIFICATION", Token.RESERVED_WORD);
		tokenMap.put("IF", Token.RESERVED_WORD);
		tokenMap.put("IN", Token.RESERVED_WORD);
		tokenMap.put("INCLUDING", Token.RESERVED_WORD);
		tokenMap.put("INDEX", Token.RESERVED_WORD);
		tokenMap.put("INDEXED", Token.RESERVED_WORD);
		tokenMap.put("INDICATE", Token.RESERVED_WORD);
		tokenMap.put("INITIAL", Token.RESERVED_WORD);
		tokenMap.put("INITIALIZE", Token.RESERVED_WORD);
		tokenMap.put("INITIATE", Token.RESERVED_WORD);
		tokenMap.put("INPUT", Token.RESERVED_WORD);
		tokenMap.put("INPUT-OUTPUT", Token.RESERVED_WORD);
		tokenMap.put("INSPECT", Token.RESERVED_WORD);
		tokenMap.put("INSTALLATION", Token.RESERVED_WORD);
		tokenMap.put("INTO", Token.RESERVED_WORD);
		tokenMap.put("INVALID", Token.RESERVED_WORD);
		tokenMap.put("IS", Token.RESERVED_WORD);
		tokenMap.put("JUST", Token.RESERVED_WORD);
		tokenMap.put("JUSTIFIED", Token.RESERVED_WORD);
		tokenMap.put("KANJI", Token.RESERVED_WORD);
		tokenMap.put("KEEP", Token.RESERVED_WORD);
		tokenMap.put("KEY", Token.RESERVED_WORD);
		tokenMap.put("LABEL", Token.RESERVED_WORD);
		tokenMap.put("LAST", Token.RESERVED_WORD);
		tokenMap.put("LD", Token.RESERVED_WORD);
		tokenMap.put("LEADING", Token.RESERVED_WORD);
		tokenMap.put("LEFT", Token.RESERVED_WORD);
		tokenMap.put("LENGTH", Token.RESERVED_WORD);
		tokenMap.put("LESS", Token.RESERVED_WORD);
		tokenMap.put("LIMIT", Token.RESERVED_WORD);
		tokenMap.put("LIMITS", Token.RESERVED_WORD);
		tokenMap.put("LINAGE", Token.RESERVED_WORD);
		tokenMap.put("LINAGE-COUNTER", Token.RESERVED_WORD);
		tokenMap.put("LINE", Token.RESERVED_WORD);
		tokenMap.put("LINE-COUNTER", Token.RESERVED_WORD);
		tokenMap.put("LINES", Token.RESERVED_WORD);
		tokenMap.put("LINKAGE", Token.RESERVED_WORD);
		tokenMap.put("LOCALLY", Token.RESERVED_WORD);
		tokenMap.put("LOCK", Token.RESERVED_WORD);
		tokenMap.put("LOCK-HOLDING", Token.RESERVED_WORD);
		tokenMap.put("LOW-VALUE", Token.RESERVED_WORD);
		tokenMap.put("LOW-VALUES", Token.RESERVED_WORD);
		tokenMap.put("LOWLIGHT [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("MANUAL", Token.RESERVED_WORD);
		tokenMap.put("MATCH", Token.RESERVED_WORD);
		tokenMap.put("MATCHES", Token.RESERVED_WORD);
		tokenMap.put("MEMBER", Token.RESERVED_WORD);
		tokenMap.put("MEMBERSHIP", Token.RESERVED_WORD);
		tokenMap.put("MEMORY", Token.RESERVED_WORD);
		tokenMap.put("MERGE", Token.RESERVED_WORD);
		tokenMap.put("MESSAGE", Token.RESERVED_WORD);
		tokenMap.put("MODE", Token.RESERVED_WORD);
		tokenMap.put("MODIFY", Token.RESERVED_WORD);
		tokenMap.put("MODULES", Token.RESERVED_WORD);
		tokenMap.put("MOVE", Token.RESERVED_WORD);
		tokenMap.put("MULTIPLE", Token.RESERVED_WORD);
		tokenMap.put("MULTIPLY", Token.RESERVED_WORD);
		tokenMap.put("NAMED", Token.RESERVED_WORD);
		tokenMap.put("NATIVE", Token.RESERVED_WORD);
		tokenMap.put("NEGATIVE", Token.RESERVED_WORD);
		tokenMap.put("NEXT", Token.RESERVED_WORD);
		tokenMap.put("NO", Token.RESERVED_WORD);
		tokenMap.put("NON-NULL", Token.RESERVED_WORD);
		tokenMap.put("NOT", Token.RESERVED_WORD);
		tokenMap.put("NOTE", Token.RESERVED_WORD);
		tokenMap.put("NULL", Token.RESERVED_WORD);
		tokenMap.put("NUMBER", Token.RESERVED_WORD);
		tokenMap.put("NUMERIC", Token.RESERVED_WORD);
		tokenMap.put("NUMERIC-EDITED", Token.RESERVED_WORD);
		tokenMap.put("OBJECT-COMPUTER", Token.RESERVED_WORD);
		tokenMap.put("OCCURS", Token.RESERVED_WORD);
		tokenMap.put("OF", Token.RESERVED_WORD);
		tokenMap.put("OFF", Token.RESERVED_WORD);
		tokenMap.put("OFFSET", Token.RESERVED_WORD);
		tokenMap.put("OMITTED", Token.RESERVED_WORD);
		tokenMap.put("ON", Token.RESERVED_WORD);
		tokenMap.put("ONLY", Token.RESERVED_WORD);
		tokenMap.put("OPEN", Token.RESERVED_WORD);
		tokenMap.put("OPTIONAL", Token.RESERVED_WORD);
		tokenMap.put("OPTIONS [200X]", Token.RESERVED_WORD);
		tokenMap.put("OR", Token.RESERVED_WORD);
		tokenMap.put("ORDER", Token.RESERVED_WORD);
		tokenMap.put("OTHERWISE", Token.RESERVED_WORD);
		tokenMap.put("PACKED-DECIMAL", Token.RESERVED_WORD);
		tokenMap.put("PADDING", Token.RESERVED_WORD);
		tokenMap.put("PAGE", Token.RESERVED_WORD);
		tokenMap.put("PAGE-COUNTER", Token.RESERVED_WORD);
		tokenMap.put("PASSWORD", Token.RESERVED_WORD);
		tokenMap.put("PERFORM", Token.RESERVED_WORD);
		tokenMap.put("PF", Token.RESERVED_WORD);
		tokenMap.put("PH", Token.RESERVED_WORD);
		tokenMap.put("PIC", Token.RESERVED_WORD);
		tokenMap.put("PICTURE", Token.RESERVED_WORD);
		tokenMap.put("PLUS", Token.RESERVED_WORD);
		tokenMap.put("POINTER", Token.RESERVED_WORD);
		tokenMap.put("POSITION", Token.RESERVED_WORD);
		tokenMap.put("POSITIONING", Token.RESERVED_WORD);
		tokenMap.put("POSITIVE", Token.RESERVED_WORD);
		tokenMap.put("PREVIOUS", Token.RESERVED_WORD);
		tokenMap.put("PRINTING", Token.RESERVED_WORD);
		tokenMap.put("PRIOR", Token.RESERVED_WORD);
		tokenMap.put("PROCEDURE", Token.RESERVED_WORD);
		tokenMap.put("PROCEDURES", Token.RESERVED_WORD);
		tokenMap.put("PROCEED", Token.RESERVED_WORD);
		tokenMap.put("PROGRAM", Token.RESERVED_WORD);
		tokenMap.put("PROGRAM-ID", Token.RESERVED_WORD);
		tokenMap.put("PROTECTED", Token.RESERVED_WORD);
		tokenMap.put("PURGE", Token.RESERVED_WORD);
		tokenMap.put("QUEUE", Token.RESERVED_WORD);
		tokenMap.put("QUOTE", Token.RESERVED_WORD);
		tokenMap.put("QUOTES", Token.RESERVED_WORD);
		tokenMap.put("RANDOM", Token.RESERVED_WORD);
		tokenMap.put("RD", Token.RESERVED_WORD);
		tokenMap.put("READ", Token.RESERVED_WORD);
		tokenMap.put("READERS", Token.RESERVED_WORD);
		tokenMap.put("READY", Token.RESERVED_WORD);
		tokenMap.put("REALM", Token.RESERVED_WORD);
		tokenMap.put("REALMS", Token.RESERVED_WORD);
		tokenMap.put("RECEIVE", Token.RESERVED_WORD);
		tokenMap.put("RECONNECT", Token.RESERVED_WORD);
		tokenMap.put("RECORD", Token.RESERVED_WORD);
		tokenMap.put("RECORD-NAME", Token.RESERVED_WORD);
		tokenMap.put("RECORD-OVERFLOW", Token.RESERVED_WORD);
		tokenMap.put("RECORDING", Token.RESERVED_WORD);
		tokenMap.put("RECORDS", Token.RESERVED_WORD);
		tokenMap.put("REDEFINES", Token.RESERVED_WORD);
		tokenMap.put("REEL", Token.RESERVED_WORD);
		tokenMap.put("REFERENCE", Token.RESERVED_WORD);
		tokenMap.put("REFERENCE-MODIFIER", Token.RESERVED_WORD);
		tokenMap.put("REFERENCES", Token.RESERVED_WORD);
		tokenMap.put("REGARDLESS", Token.RESERVED_WORD);
		tokenMap.put("RELATIVE", Token.RESERVED_WORD);
		tokenMap.put("RELEASE", Token.RESERVED_WORD);
		tokenMap.put("RELOAD", Token.RESERVED_WORD);
		tokenMap.put("REMAINDER", Token.RESERVED_WORD);
		tokenMap.put("REMARKS", Token.RESERVED_WORD);
		tokenMap.put("REMOVAL", Token.RESERVED_WORD);
		tokenMap.put("RENAMES", Token.RESERVED_WORD);
		tokenMap.put("REORG-CRITERIA", Token.RESERVED_WORD);
		tokenMap.put("REPLACE", Token.RESERVED_WORD);
		tokenMap.put("REPLACING", Token.RESERVED_WORD);
		tokenMap.put("REPORT", Token.RESERVED_WORD);
		tokenMap.put("REPORTING", Token.RESERVED_WORD);
		tokenMap.put("REPORTS", Token.RESERVED_WORD);
		tokenMap.put("REQUIRED [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("RERUN", Token.RESERVED_WORD);
		tokenMap.put("RESERVE", Token.RESERVED_WORD);
		tokenMap.put("RESET", Token.RESERVED_WORD);
		tokenMap.put("RETAINING", Token.RESERVED_WORD);
		tokenMap.put("RETRIEVAL", Token.RESERVED_WORD);
		tokenMap.put("RETURN", Token.RESERVED_WORD);
		tokenMap.put("RETURN-CODE [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("RETURNING", Token.RESERVED_WORD);
		tokenMap.put("REVERSE-VIDEO [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("REVERSED", Token.RESERVED_WORD);
		tokenMap.put("REWIND", Token.RESERVED_WORD);
		tokenMap.put("REWRITE", Token.RESERVED_WORD);
		tokenMap.put("RF", Token.RESERVED_WORD);
		tokenMap.put("RH", Token.RESERVED_WORD);
		tokenMap.put("RIGHT", Token.RESERVED_WORD);
		tokenMap.put("RMS-CURRENT-FILENAME", Token.RESERVED_WORD);
		tokenMap.put("RMS-CURRENT-STS", Token.RESERVED_WORD);
		tokenMap.put("RMS-CURRENT-STV", Token.RESERVED_WORD);
		tokenMap.put("RMS-FILENAME", Token.RESERVED_WORD);
		tokenMap.put("RMS-STS", Token.RESERVED_WORD);
		tokenMap.put("RMS-STV", Token.RESERVED_WORD);
		tokenMap.put("ROLLBACK", Token.RESERVED_WORD);
		tokenMap.put("ROUNDED", Token.RESERVED_WORD);
		tokenMap.put("RUN", Token.RESERVED_WORD);
		tokenMap.put("SAME", Token.RESERVED_WORD);
		tokenMap.put("SCREEN [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("SD", Token.RESERVED_WORD);
		tokenMap.put("SEARCH", Token.RESERVED_WORD);
		tokenMap.put("SECTION", Token.RESERVED_WORD);
		tokenMap.put("SECURE [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("SECURITY", Token.RESERVED_WORD);
		tokenMap.put("SEGMENT", Token.RESERVED_WORD);
		tokenMap.put("SEGMENT-LIMIT", Token.RESERVED_WORD);
		tokenMap.put("SELECT", Token.RESERVED_WORD);
		tokenMap.put("SEND", Token.RESERVED_WORD);
		tokenMap.put("SENTENCE", Token.RESERVED_WORD);
		tokenMap.put("SEPARATE", Token.RESERVED_WORD);
		tokenMap.put("SEQUENCE", Token.RESERVED_WORD);
		tokenMap.put("SEQUENCE-NUMBER", Token.RESERVED_WORD);
		tokenMap.put("SEQUENTIAL", Token.RESERVED_WORD);
		tokenMap.put("SERVICE", Token.RESERVED_WORD);
		tokenMap.put("SET", Token.RESERVED_WORD);
		tokenMap.put("SETS", Token.RESERVED_WORD);
		tokenMap.put("SIGN", Token.RESERVED_WORD);
		tokenMap.put("SIGNED [200X]", Token.RESERVED_WORD);
		tokenMap.put("SIZE", Token.RESERVED_WORD);
		tokenMap.put("SKIP1", Token.RESERVED_WORD);
		tokenMap.put("SKIP2", Token.RESERVED_WORD);
		tokenMap.put("SKIP3", Token.RESERVED_WORD);
		tokenMap.put("SORT", Token.RESERVED_WORD);
		tokenMap.put("SORT-MERGE", Token.RESERVED_WORD);
		tokenMap.put("SOURCE", Token.RESERVED_WORD);
		tokenMap.put("SOURCE-COMPUTER", Token.RESERVED_WORD);
		tokenMap.put("SPACE", Token.RESERVED_WORD);
		tokenMap.put("SPACES", Token.RESERVED_WORD);
		tokenMap.put("SPECIAL-NAMES", Token.RESERVED_WORD);
		tokenMap.put("STANDARD", Token.RESERVED_WORD);
		tokenMap.put("STANDARD-1", Token.RESERVED_WORD);
		tokenMap.put("STANDARD-2", Token.RESERVED_WORD);
		tokenMap.put("START", Token.RESERVED_WORD);
		tokenMap.put("STATUS", Token.RESERVED_WORD);
		tokenMap.put("STOP", Token.RESERVED_WORD);
		tokenMap.put("STORE", Token.RESERVED_WORD);
		tokenMap.put("STREAM", Token.RESERVED_WORD);
		tokenMap.put("STRING", Token.RESERVED_WORD);
		tokenMap.put("SUB-QUEUE-1", Token.RESERVED_WORD);
		tokenMap.put("SUB-QUEUE-2", Token.RESERVED_WORD);
		tokenMap.put("SUB-QUEUE-3", Token.RESERVED_WORD);
		tokenMap.put("SUB-SCHEMA", Token.RESERVED_WORD);
		tokenMap.put("SUBTRACT", Token.RESERVED_WORD);
		tokenMap.put("SUCCESS", Token.RESERVED_WORD);
		tokenMap.put("SUM", Token.RESERVED_WORD);
		tokenMap.put("SUPPRESS", Token.RESERVED_WORD);
		tokenMap.put("SYMBOLIC", Token.RESERVED_WORD);
		tokenMap.put("SYNC", Token.RESERVED_WORD);
		tokenMap.put("SYNCHRONIZED", Token.RESERVED_WORD);
		tokenMap.put("TABLE", Token.RESERVED_WORD);
		tokenMap.put("TALLYING", Token.RESERVED_WORD);
		tokenMap.put("TAPE", Token.RESERVED_WORD);
		tokenMap.put("TENANT", Token.RESERVED_WORD);
		tokenMap.put("TERMINAL", Token.RESERVED_WORD);
		tokenMap.put("TERMINATE", Token.RESERVED_WORD);
		tokenMap.put("TEST", Token.RESERVED_WORD);
		tokenMap.put("TEXT", Token.RESERVED_WORD);
		tokenMap.put("THAN", Token.RESERVED_WORD);
		tokenMap.put("THEN", Token.RESERVED_WORD);
		tokenMap.put("THROUGH", Token.RESERVED_WORD);
		tokenMap.put("THRU", Token.RESERVED_WORD);
		tokenMap.put("TIME", Token.RESERVED_WORD);
		tokenMap.put("TIMES", Token.RESERVED_WORD);
		tokenMap.put("TO", Token.RESERVED_WORD);
		tokenMap.put("TOP", Token.RESERVED_WORD);
		tokenMap.put("TRACE", Token.RESERVED_WORD);
		tokenMap.put("TRAILING", Token.RESERVED_WORD);
		tokenMap.put("TRANSFORM", Token.RESERVED_WORD);
		tokenMap.put("TRUE", Token.RESERVED_WORD);
		tokenMap.put("TYPE", Token.RESERVED_WORD);
		tokenMap.put("UNDERLINE [XOPEN]", Token.RESERVED_WORD);
		tokenMap.put("UNDERLINED", Token.RESERVED_WORD);
		tokenMap.put("UNEQUAL", Token.RESERVED_WORD);
		tokenMap.put("UNIT", Token.RESERVED_WORD);
		tokenMap.put("UNLOCK", Token.RESERVED_WORD);
		tokenMap.put("UNSIGNED [200X]", Token.RESERVED_WORD);
		tokenMap.put("UNSTRING", Token.RESERVED_WORD);
		tokenMap.put("UNTIL", Token.RESERVED_WORD);
		tokenMap.put("UP", Token.RESERVED_WORD);
		tokenMap.put("UPDATE", Token.RESERVED_WORD);
		tokenMap.put("UPDATERS", Token.RESERVED_WORD);
		tokenMap.put("UPON", Token.RESERVED_WORD);
		tokenMap.put("USAGE", Token.RESERVED_WORD);
		tokenMap.put("USAGE-MODE", Token.RESERVED_WORD);
		tokenMap.put("USE", Token.RESERVED_WORD);
		tokenMap.put("USING", Token.RESERVED_WORD);
		tokenMap.put("VALUE", Token.RESERVED_WORD);
		tokenMap.put("VALUES", Token.RESERVED_WORD);
		tokenMap.put("VARYING", Token.RESERVED_WORD);
		tokenMap.put("VFU-CHANNEL", Token.RESERVED_WORD);
		tokenMap.put("WAIT", Token.RESERVED_WORD);
		tokenMap.put("WHEN", Token.RESERVED_WORD);
		tokenMap.put("WHERE", Token.RESERVED_WORD);
		tokenMap.put("WITH", Token.RESERVED_WORD);
		tokenMap.put("WITHIN", Token.RESERVED_WORD);
		tokenMap.put("WORDS", Token.RESERVED_WORD);
		tokenMap.put("WORKING-STORAGE", Token.RESERVED_WORD);
		tokenMap.put("WRITE", Token.RESERVED_WORD);
		tokenMap.put("WRITERS", Token.RESERVED_WORD);
		tokenMap.put("ZERO", Token.RESERVED_WORD);
		tokenMap.put("ZEROES", Token.RESERVED_WORD);
		tokenMap.put("ZEROS", Token.RESERVED_WORD);
		tokenMap.put("PROGRAM-ID", Token.RESERVED_WORD);
		tokenMap.put("AUTHOR", Token.RESERVED_WORD);
		tokenMap.put("DATE-WRITTEN", Token.RESERVED_WORD);
		tokenMap.put("STOP RUN", Token.RESERVED_WORD);
		tokenMap.put("ENVIRONMENT DIVISION", Token.RESERVED_WORD);
		tokenMap.put("DATA DIVISION", Token.RESERVED_WORD);
		tokenMap.put("WORKING-STORAGE SECTION", Token.RESERVED_WORD);
		tokenMap.put("PROCEDURE DIVISION", Token.RESERVED_WORD);

		tokenMap.put("ACOS", Token.FUNCTION);
		tokenMap.put("ADD-DURATION", Token.FUNCTION);
		tokenMap.put("ANNUITY", Token.FUNCTION);
		tokenMap.put("ASIN", Token.FUNCTION);
		tokenMap.put("ATAN", Token.FUNCTION);
		tokenMap.put("CHAR", Token.FUNCTION);
		tokenMap.put("CONVERT-DATE-TIME", Token.FUNCTION);
		tokenMap.put("COS", Token.FUNCTION);
		tokenMap.put("CURRENT-DATE", Token.FUNCTION);
		tokenMap.put("DATE-OF-INTEGER", Token.FUNCTION);
		tokenMap.put("DAY-OF-INTEGER", Token.FUNCTION);
		tokenMap.put("DAY-TO-YYYYDDD", Token.FUNCTION);
		tokenMap.put("EXTRACT-DATE-TIME", Token.FUNCTION);
		tokenMap.put("DATE-TO-YYYYMMDD", Token.FUNCTION);
		tokenMap.put("DISPLAY-OF", Token.FUNCTION);
		tokenMap.put("FACTORIAL", Token.FUNCTION);
		tokenMap.put("FIND-DURATION", Token.FUNCTION);
		tokenMap.put("INTEGER", Token.FUNCTION);
		tokenMap.put("INTEGER-OF-DATE", Token.FUNCTION);
		tokenMap.put("INTEGER-OF-DAY", Token.FUNCTION);
		tokenMap.put("INTEGER-PART", Token.FUNCTION);
		tokenMap.put("LENGTH", Token.FUNCTION);
		tokenMap.put("LOCALE-DATE", Token.FUNCTION);
		tokenMap.put("LOCALE-TIME", Token.FUNCTION);
		tokenMap.put("LOG", Token.FUNCTION);
		tokenMap.put("LOG10", Token.FUNCTION);
		tokenMap.put("LOWER-CASE", Token.FUNCTION);
		tokenMap.put("MAX", Token.FUNCTION);
		tokenMap.put("MEAN", Token.FUNCTION);
		tokenMap.put("MEDIAN", Token.FUNCTION);
		tokenMap.put("MIDRANGE", Token.FUNCTION);
		tokenMap.put("MIN", Token.FUNCTION);
		tokenMap.put("MOD", Token.FUNCTION);
		tokenMap.put("NATIONAL-OF", Token.FUNCTION);
		tokenMap.put("NUMVAL", Token.FUNCTION);
		tokenMap.put("NUMVAL-C", Token.FUNCTION);
		tokenMap.put("ORD", Token.FUNCTION);
		tokenMap.put("ORD-MAX", Token.FUNCTION);
		tokenMap.put("ORD-MIN", Token.FUNCTION);
		tokenMap.put("PRESENT-VALUE", Token.FUNCTION);
		tokenMap.put("RANDOM", Token.FUNCTION);
		tokenMap.put("RANGE", Token.FUNCTION);
		tokenMap.put("REM", Token.FUNCTION);
		tokenMap.put("REVERSE", Token.FUNCTION);
		tokenMap.put("SIN", Token.FUNCTION);
		tokenMap.put("SQRT", Token.FUNCTION);
		tokenMap.put("STANDARD-DEVIATION", Token.FUNCTION);
		tokenMap.put("SUBTRACT-DURATION", Token.FUNCTION);
		tokenMap.put("SUM", Token.FUNCTION);
		tokenMap.put("TAN", Token.FUNCTION);
		tokenMap.put("TEST-DATE-TIME", Token.FUNCTION);
		tokenMap.put("TRIM", Token.FUNCTION);
		tokenMap.put("TRIML", Token.FUNCTION);
		tokenMap.put("TRIMR", Token.FUNCTION);
		tokenMap.put("UPPER-CASE", Token.FUNCTION);
		tokenMap.put("VARIANCE", Token.FUNCTION);
		tokenMap.put("UTF8STRING", Token.FUNCTION);
		tokenMap.put("WHEN-COMPILED", Token.FUNCTION);
		tokenMap.put("YEAR-TO-YYYY", Token.FUNCTION);

		return tokenMap;
	}

}
