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

import java.util.ArrayList;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import com.horvath.cobbler.command.ReadResourceTextFileCmd;

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

	   final int commentMarkerPosition = 6; 
	   
	   for (int i=offset; i<end; i++) {

	      char c = array[i];
	      
			if ((offset + commentMarkerPosition) < array.length - 1) {
				// parse out comment tokens, either character in column 7
				if (array[offset + commentMarkerPosition] == '*' || array[offset + commentMarkerPosition] == '/') {
					if (i >= offset + commentMarkerPosition && i < end - 1) {
						currentTokenType = Token.COMMENT_MULTILINE;
					} else if (i == end - 1) {
						currentTokenType = Token.COMMENT_EOL;
					}
				}
			}
			
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

	         case Token.COMMENT_KEYWORD:
		            i = end - 1;
		            addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
		            currentTokenType = Token.NULL;
		            break;

	         case Token.COMMENT_MULTILINE:
		            i = end - 1;
		            addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
		            currentTokenType = Token.NULL;
		            break;

	         case Token.COMMENT_MARKUP:
		            i = end - 1;
		            addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
		            currentTokenType = Token.NULL;
		            break;
		            
	         case Token.COMMENT_DOCUMENTATION:
		            i = end - 1;
		            addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
		            // We need to set token type to null so at the bottom we don't add one more token.
		            currentTokenType = Token.NULL;
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
		
		// get data from input files and process tokens
		tokenMap = processOperators(SyntaxUtils.readResouceFile(ReadResourceTextFileCmd.OPERATORS), tokenMap);
		tokenMap = processReservedWords(SyntaxUtils.readResouceFile(ReadResourceTextFileCmd.RESERVED_WORDS), tokenMap);
		tokenMap = processFunctions(SyntaxUtils.readResouceFile(ReadResourceTextFileCmd.INTRINSIC_FUNCTIONS), tokenMap);
		
		return tokenMap;
	}
	
	/**
	 * Takes a list of strings and processes them into operator tokens. 
	 *  
	 * @param list ArrayList<String>
	 * @param tokenMap TokenMap
	 * @return TokenMap
	 */
	private TokenMap processOperators(ArrayList<String> list, TokenMap tokenMap) {
	
		for (String s : list) {			
			tokenMap.put(s, Token.OPERATOR);
		}
		
		return tokenMap;
	}
	
	/**
	 * Takes a list of strings and processes them into reserved word tokens. 
	 * 
	 * @param list ArrayList<String>
	 * @param tokenMap TokenMap
	 * @return TokenMap
	 */
	private TokenMap processReservedWords(ArrayList<String> list, TokenMap tokenMap) {
		
		for (String s : list) {			
			tokenMap.put(s.toLowerCase(), Token.RESERVED_WORD);
			tokenMap.put(s.toUpperCase(), Token.RESERVED_WORD);
			tokenMap.put(SyntaxUtils.toTitleCase(s), Token.RESERVED_WORD);
			
			tokenMap.put(s.toLowerCase() + ".", Token.RESERVED_WORD);
			tokenMap.put(s.toUpperCase() + ".", Token.RESERVED_WORD);
			tokenMap.put(SyntaxUtils.toTitleCase(s) + ".", Token.RESERVED_WORD);
		}
		
		return tokenMap;
	}
	
	/**
	 * Takes a list of strings and processes them into function tokens. 
	 * 
	 * @param list ArrayList<String>
	 * @param tokenMap TokenMap
	 * @return TokenMap
	 */
	private TokenMap processFunctions(ArrayList<String> list, TokenMap tokenMap) {
		
		for (String s : list) {			
			tokenMap.put(s.toLowerCase(), Token.FUNCTION);
			tokenMap.put(s.toUpperCase(), Token.FUNCTION);
			tokenMap.put(SyntaxUtils.toTitleCase(s), Token.FUNCTION);
		}
		
		return tokenMap;
	}

}
