import java.util.List;
import java.io.FileNotFoundException;
import java.util.ArrayList;

// Other required classes:
// PeekableCharacterStream.java
// PCFileStream.java
// Token.java

public class Scanner {
	
	class nexChar {
		int value;
		int linenum;
		int charpos;
		l1charType l1Type;
		l2charType l2Type;
		l3charType l3Type;
		
		
		public nexChar(int val, l1charType l1, l2charType l2, l3charType l3) {
			this.value = val;
			this.l1Type = l1;
			this.l2Type = l2;
			this.l3Type = l3;
			
		}
	}
	
	// buffer for peeking
	// will only have size 0 or 1
	ArrayList<Token> tokenBuf;
	// char buffer for future construction of the token
	ArrayList<nexChar> charBuf;
	PeekableCharacterStream fileStream;
	List<String> keywordList;
	
	enum l1charType {
		CharacterLiteral("CL"),
		DubQuote("DQ"),
		Backslash("BS"),
		WhiteSpace("WS"),
		Invalid("IV"),
		End("ED");
		
		private String Tp;
		
		public String getType() {
			return this.Tp;
		}
		
		private l1charType(String Tp) {
			this.Tp = Tp;
		}
	}
	enum l2charType {
		Digits("Digits"),
		Alpha("Alpha"),
		Space("Space"),
		Not("Not"),
		Underscore("Underscore"),
		Dot("Dot"),
		Operator("Operator"),
		None("None");
		
		private String Tp;
		
		public String getType() {
			return this.Tp;
		}
		
		private l2charType(String Tp) {
			this.Tp = Tp;
		}
	}
	
	enum l3charType {
		EscapedCharacter("EC"),
		LogicOperator("LO"),
		Negative("NG"),
		None("NA");
		
		private String Tp;
		
		public String getType() {
			return this.Tp;
		}
		
		private l3charType(String Tp) {
			this.Tp = Tp;
		}
	}
	enum tokenType {
		NONE("NA"),
		OPERATOR("OP"),
		IDENTIFIER("ID"),
		INT_CONSTANT("IC"),
		FLOAT_CONSTANT("FC"),
		IF_CONSTANT_INVALID("IFCI"),
		STRING_CONSTANT("SC"),
		STRING_CONSTANT_INCOMPELETE("SCIC"),
		STRING_CONSTANT_INVALID1("SCIV1"),
		STRING_CONSTANT_INVALID2("SCIV2"),
		INVALID("IV");
		
		private String Tp;
		
		public String getType() {
			return this.Tp;
		}
		
		private tokenType(String Tp) {
			this.Tp = Tp;
		}
	}
	
	/*l1charType curr_char_l1type;
	l2charType curr_char_l2type;
	l3charType curr_char_l3type;*/
	tokenType next_token_type;
	Token.TokenType finTokenType;
	tokenType pre_token_type;
	
	// Constructor
	public Scanner(PeekableCharacterStream stream, List<String> keywordlist) {
		this.fileStream = stream;
		this.tokenBuf = new ArrayList<>();
		this.charBuf = new ArrayList<>();
		this.keywordList = keywordlist;
	}
	
	// Returns the next token without consuming it.
	public Token peekNextToken() {
		// if the tokenBuf is empty
		if (tokenBuf.size() == 0) {
			strHandling();
		}
		
		return tokenBuf.get(0);
	}
	
	// Return the next token and consumes it.
	public Token getNextToken() {
		Token tmp;
		if (tokenBuf.size() == 0) {
			strHandling();
		}
		tmp = tokenBuf.get(0);
		tokenBuf.remove(0);
		return tmp;
	}
	
	// ascii num
	// char type categories:
	/* 
	 * - Special considerations: [0-31]
	 * - CharacterLiteral: [32-33] + [35-126]
	 * 		- Digits: [48-57]
	 * 		- Alpha: [65-90] + [97-122]
	 * 		- Operator: [40-45] + [58-62] + [47,123,125] + [60-62,33][61]
	 * 		- Identifier: [95,Alpha][95,Digits,Alpha]
	 * 		- IntConstant: [\45][Digits]
	 * 		- FloatConstant: [\45][Digits][46][Digits]
	 * 		- StringConstant: [34][CharacterLitertal,EscapedCharacter][34]
	 * - WhiteSpace: [32,9,13,10]
	 * - EscapedCharacter: [92][98,110,114,116,92,39,34]
	 * 	
	 * -------------------------------------------------------
	 * - Char hierarchy trees:
	 * 
	 * Primary Groups:
	 * 
	 * Secondary Groups:
	 * - CharacterLiteral
	 * 		- Digits
	 * 			- IntConstant
	 * 				- FloatConstant
	 * 
	 * 		- Alpha
	 * 		- [45](-)
	 * 			- IntConstant
	 * 				- FloatConstant
	 * 		- Operator
	 * 		
	 * 
	 */
	
	// token type categories:
	
	// return the category of the next character in the filestream
	private l1charType l1CharCheck(int char_ascii) {
		
		int[] white_space = {9, 10, 13, 32};
		
		// reach the end of the filestream
		if (char_ascii == -1) {
			return l1charType.End;
		
		}
		
		// WhiteSpace beside Space(32)
		else if (arrayCheck(char_ascii, white_space)) {
				return l1charType.WhiteSpace;
		}
		
		// under the characterliteral + " + \ realm
		else if (char_ascii >= 32 && char_ascii <= 126) {
			if (char_ascii == 34) {
				return l1charType.DubQuote;
			}
			else if (char_ascii == 92) {
				return l1charType.Backslash;
			}
			return l1charType.CharacterLiteral;
		}
		
		// if the char_ascii falls outside from -1~126
		return l1charType.Invalid;
	}
		
	private l2charType l2CharCheck(int char_ascii) {
		
		int[] operator = {40, 41, 42, 43, 44, 45, 47, 59, 60, 61, 62, 123, 125};
		
		if (char_ascii == 32) {
			return l2charType.Space;
		}
		else if (char_ascii == 33) {
			return l2charType.Not;
		}
		else if (char_ascii == 46) {
			return l2charType.Dot;
		}
		else if (char_ascii == 95) {
			return l2charType.Underscore;
		}
		else if (char_ascii >= 48 && char_ascii <= 57) {
			return l2charType.Digits;
		}
		else if ((char_ascii >= 65 && char_ascii <= 90) || (char_ascii >= 97 && char_ascii <= 122)) {
			return l2charType.Alpha;
		}
		else if (arrayCheck(char_ascii, operator)) {
			return l2charType.Operator;
		}
		return l2charType.None;
	}

	private l3charType l3CharCheck(int char_ascii) {
		int[] logicOperator = {60, 61, 62, 33};
		int[] escapeCharacter = {98, 110, 114, 116, 92, 39, 34};
		if (arrayCheck(char_ascii, logicOperator)) {
			return l3charType.LogicOperator;
		}
		else if (arrayCheck(char_ascii, escapeCharacter)) {
			return l3charType.EscapedCharacter;
		}
		else if (char_ascii == 45) {
			return l3charType.Negative;
		}
		return l3charType.None;
	}

	
	private boolean charBufHandling() {
		l1charType nexCharl1Type;
		l2charType nexCharl2Type;
		l3charType nexCharl3Type;
		// peek next char
		int nextChar = this.fileStream.peekNextChar();
		
		// categories the char
		nexCharl1Type = l1CharCheck(nextChar);
		nexCharl2Type = l2CharCheck(nextChar);
		nexCharl3Type = l3CharCheck(nextChar);

		
		// construct a nexChar class to store the information of the next char
		nexChar theChar = new nexChar(nextChar, nexCharl1Type, nexCharl2Type, nexCharl3Type);
	
		// check if the charBuf<nexChar> buffer of the next token is empty
			// Yes---> getNextChar(), add it into the charBuf<nexChar> buffer
		if (charBuf.size() == 0) {
			// consume the next char either way if it's the char buffer is empty
			this.fileStream.getNextChar();
			// update the nexchar linenum and position
			theChar.linenum = this.fileStream.getLinenum();
			theChar.charpos = this.fileStream.getCharpos();
			
			
			// Reach the end of the filestream
			if (nexCharl1Type == l1charType.End) {
				this.next_token_type = tokenType.NONE;
				charBuf.add(theChar);
				return true;
			}
			// skip whitespace
			else if ((nexCharl1Type == l1charType.WhiteSpace) || (nexCharl2Type == l2charType.Space)) {
				return false;
			}
			// start with "
			else if (nexCharl1Type == l1charType.DubQuote) {
				this.next_token_type = tokenType.STRING_CONSTANT_INCOMPELETE;
			}
			// start with _ or Alpha
			else if ((nexCharl2Type == l2charType.Alpha) || (nexCharl2Type == l2charType.Underscore)){
				this.next_token_type = tokenType.IDENTIFIER;
			}
			// start with operator
			else if (nexCharl2Type == l2charType.Operator) {
				this.next_token_type = tokenType.OPERATOR;
			}
			// start with digits
			else if (nexCharl2Type == l2charType.Digits) {
				this.next_token_type = tokenType.INT_CONSTANT;
			}
			// start with Not
			else if (nexCharl2Type == l2charType.Not) {
				// consider Not as operator temperarily
				this.next_token_type = tokenType.OPERATOR;
			}
			// start with invalid
			else {
				this.next_token_type = tokenType.INVALID;
				charBuf.add(theChar);
				return true;
			}
			charBuf.add(theChar);
			return false;
		}
		// If char buffer is NOT empty---> Compares its' type to see if it matches
		else {
			// when reach the end of the filestream
			if (nexCharl1Type == l1charType.End) {
				
				return true;
			}
			// start with identifier
			else if (this.next_token_type == tokenType.IDENTIFIER) {
				if ((nexCharl2Type == l2charType.Underscore) || (nexCharl2Type == l2charType.Digits) || (nexCharl2Type == l2charType.Alpha)) {
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
					return false;
				} else {
					// stop adding char to the buffer if next char does not follow the token rules
					return true;
				}
			}
			
			// start with operator
			else if ((this.next_token_type == tokenType.OPERATOR) && (charBuf.size() == 1)) {
				if ((charBuf.get(0).l3Type == l3charType.Negative) && (this.pre_token_type != tokenType.IDENTIFIER) && (nexCharl2Type == l2charType.Digits)) {
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
					this.next_token_type = tokenType.INT_CONSTANT;
					return false;
				}
				
				// logicOperator follow by =
				else if ((charBuf.get(0).l3Type == l3charType.LogicOperator) && (nextChar == 61)) {
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
				}
				else if (charBuf.get(0).l2Type == l2charType.Not) {
					// reverse the token type to invalid if not is not considered as logical operator
					this.next_token_type = tokenType.INVALID;
				}
				return true;
			}
			
			// start with "
			else if (this.next_token_type == tokenType.STRING_CONSTANT_INCOMPELETE) {
				// when encounter a \ or Invalid char
				if ((nexCharl1Type == l1charType.Backslash) || (nexCharl1Type == l1charType.Invalid)) {
					// set it to STRING_CONSTANT_INVALID temperarily
					this.next_token_type = tokenType.STRING_CONSTANT_INVALID2;
				}
				else if ((nexCharl1Type == l1charType.CharacterLiteral) || (nexCharl2Type == l2charType.Space) || (nexCharl1Type == l1charType.DubQuote)) {

					// non-Escaped "
					if (nexCharl1Type == l1charType.DubQuote) {
						this.next_token_type = tokenType.STRING_CONSTANT;
					}
				}
				// consume next char
				this.fileStream.getNextChar();
				// update the nexchar linenum and position
				theChar.linenum = this.fileStream.getLinenum();
				theChar.charpos = this.fileStream.getCharpos();
				charBuf.add(theChar);
				return false;
			}
			
			// STRING_CONSTANT final check
			else if (this.next_token_type == tokenType.STRING_CONSTANT) {
				if ((nexCharl2Type == l2charType.Underscore) || (nexCharl2Type == l2charType.Alpha)) {
					this.next_token_type = tokenType.STRING_CONSTANT_INVALID1;
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
					return false;
				}
				// remove the double quotes
				charBuf.remove(0);
				charBuf.remove(charBuf.size()-1);
				return true;
			}
			
			// STRING_CONSTANT_INVALID2
			else if (this.next_token_type == tokenType.STRING_CONSTANT_INVALID1) {
				if ((nexCharl2Type == l2charType.Digits) || (nexCharl2Type == l2charType.Underscore) || (nexCharl2Type == l2charType.Alpha)) {
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
					return false;
				}
				return true;
			}
			
			
			// STRING_CONSTANT_INVALID2
			else if (this.next_token_type == tokenType.STRING_CONSTANT_INVALID2) {
				// if the previous char is a \ and can turn into an escaped character after combined with nex char
				if ((charBuf.get(charBuf.size()-1).l1Type == l1charType.Backslash) && (nexCharl3Type == l3charType.EscapedCharacter)) {
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
					// change the token type back to STRING_CONSTANT
					this.next_token_type = tokenType.STRING_CONSTANT_INCOMPELETE;
				}
				else if (nexCharl1Type == l1charType.DubQuote) {
					return true;
				}
			}
			
			// IntConstant
			else if (this.next_token_type == tokenType.INT_CONSTANT) {
				if ((nexCharl2Type == l2charType.Digits) || (nexCharl2Type == l2charType.Dot)) {
					if (nexCharl2Type == l2charType.Dot) {
						this.next_token_type = tokenType.FLOAT_CONSTANT;
					}
				}
				// become Invalid
				else if ((nexCharl2Type == l2charType.Underscore) || (nexCharl2Type == l2charType.Alpha)) {
					this.next_token_type = tokenType.IF_CONSTANT_INVALID;
				}
				else {
					return true;
				}
				// consume next char
				this.fileStream.getNextChar();
				// update the nexchar linenum and position
				theChar.linenum = this.fileStream.getLinenum();
				theChar.charpos = this.fileStream.getCharpos();
				charBuf.add(theChar);
			}
			
			// FloatConstant
			else if (this.next_token_type == tokenType.FLOAT_CONSTANT) {
				if ((nexCharl2Type == l2charType.Digits) || (nexCharl2Type == l2charType.Underscore) || (nexCharl2Type == l2charType.Alpha)) {
					if ((nexCharl2Type == l2charType.Underscore) || (nexCharl2Type == l2charType.Alpha)) {
						this.next_token_type = tokenType.IF_CONSTANT_INVALID;
					}
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
					return false;
				}
				return true;
			}
			
			// IF_CONSTANT_INVALID
			else if (this.next_token_type == tokenType.IF_CONSTANT_INVALID) {
				if ((nexCharl2Type == l2charType.Digits) || (nexCharl2Type == l2charType.Underscore) || (nexCharl2Type == l2charType.Alpha)) {
					// consume next char
					this.fileStream.getNextChar();
					// update the nexchar linenum and position
					theChar.linenum = this.fileStream.getLinenum();
					theChar.charpos = this.fileStream.getCharpos();
					charBuf.add(theChar);
					return false;
				}
				return true;
			}
			
			else if (nexCharl1Type == l1charType.End) {
				return true;
			}
			
		}
			
		return false;
	}

	private void strHandling() {
		String tmptext = "";
		int linenum;
		int start_char_pos;
		boolean stop_flag = false;
		while (!stop_flag) {
			stop_flag = charBufHandling();
		}
		if (this.next_token_type != tokenType.NONE) {
			// create the string from the charBuf
			for (int i = 0; i < charBuf.size(); i++) {
				tmptext += (char)charBuf.get(i).value;
			}
		}
		linenum = charBuf.get(0).linenum;
		start_char_pos = charBuf.get(0).charpos;
		
		
		
		// finalize tokenType
		// check keyword
		if (this.next_token_type == tokenType.IDENTIFIER) {
			boolean kyflag = false;
			for (int i = 0; i < this.keywordList.size(); i++) {
				if (tmptext == this.keywordList.get(i)) {
					kyflag = true;
				}
			}
			if (kyflag) {
				this.finTokenType = Token.TokenType.KEYWORD;
			} else {
				this.finTokenType = Token.TokenType.IDENTIFIER;
			}
		}
		else if (this.next_token_type == tokenType.OPERATOR) {
			this.finTokenType = Token.TokenType.OPERATOR;
		}
		else if (this.next_token_type == tokenType.INT_CONSTANT) {
			this.finTokenType = Token.TokenType.INT_CONSTANT;
		}
		else if (this.next_token_type == tokenType.FLOAT_CONSTANT) {
			this.finTokenType = Token.TokenType.FLOAT_CONSTANT;
		}
		else if (this.next_token_type == tokenType.STRING_CONSTANT) {
			this.finTokenType = Token.TokenType.STRING_CONSTANT;
		}
		else if(this.next_token_type == tokenType.NONE) {
			this.finTokenType = Token.TokenType.NONE;
		} else {
			this.finTokenType = Token.TokenType.INVALID;
		}
		
		// clear the charBuf
		charBuf.clear();
		this.pre_token_type = this.next_token_type;
		tokenBuf.add(new Token(tmptext, this.finTokenType, linenum, start_char_pos));
	}
	
	
	// helper function
	// check if an element is inside an array
	public static boolean arrayCheck(int targetElement, int[] targetArray) {
		for (int i : targetArray) {
			if (i == targetElement) {
				return true;
			}
		}
		return false;
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		List<String> kylist = new ArrayList<>();
		// No Input file provided
		if (args.length == 0) {
			return;
		}
		PeekableCharacterStream PCFS = new PCFileStream(args[0]);
		Scanner myScanner = new Scanner(PCFS, kylist);

		Token myToken = myScanner.peekNextToken();
		while (myToken.getType() != Token.TokenType.NONE) {
			myToken = myScanner.getNextToken();
			
			//TODO: use format
			System.out.println("@   " +  myToken.getLineNumber() +  ",   " + myToken.getCharPosition() + 
					"     " + myToken.getType() + "    \"" + myToken.getText() + "\"");
			
		}
		
	}
	
}
