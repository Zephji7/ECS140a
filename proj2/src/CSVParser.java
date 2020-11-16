import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class CSVParser {
	
	class theToken {
		String Token;
		int linenum;
		int pos;
		boolean isEmpty;
		
		public theToken(String Tk, int ln, int ps, boolean ie) {
			this.Token = Tk;
			this.linenum = ln;
			this.pos = ps;
			this.isEmpty = ie;
		}
	}
	
	ArrayList<theToken> headerList;
	ArrayList<theToken> tokenBuf;
	ArrayList<Map<String,String>> lineBuf;
	PeekableCharacterStream fileStream;
	String nexToken;
	int tokenPos;
	int tokenLine;
	// one time flag for header
	boolean emptycellInline = false;
	
	// Constructor
	public CSVParser(PeekableCharacterStream stream) {
		// Initialization
		this.headerList = new ArrayList<>();
		this.tokenBuf = new ArrayList<>();
		this.lineBuf = new ArrayList<>();
		this.fileStream = stream;
		
		// Read in the header of the file
		getHeader();
	}
	
	public Map<String,String> peekNextRow() {
		if (this.lineBuf.size() == 0) {
			getMap();
		}
		return this.lineBuf.get(0);
	}
	
	public Map<String,String> getNextRow() {
		
		Map<String,String> tmpMap = new HashMap<>();
		
		if (this.lineBuf.size() == 0) {
			getMap();
		}
		tmpMap = this.lineBuf.get(0);
		this.lineBuf.remove(0);
		return tmpMap;
	}
	
	
	private int getToken() {
		int[] endChar = {10, 44};
		// whitespace char
		int[] whiteSpaces = {9, 13, 32};
		ArrayList<Integer> charBuf = new ArrayList<>();
		String theToken = "";
		boolean quote_flag = false;
		
		while (true) {
			int nexChar = this.fileStream.peekNextChar();
			
			
			// check validity
			if (!validityCheck(this.fileStream.peekNextChar())) {
				throw new IllegalArgumentException("Invalid input stream detected");
			}

			// 1st char
			if (charBuf.size() == 0) {
				// Reaches the end of fileStream
				if (nexChar == -1) {
					return -1;
				}
				
				// start with a whiteSpace
				else if (arrayCheck(nexChar, whiteSpaces)) {
					// consume next char to acquire the information for exception
					this.fileStream.getNextChar();
					throw new IllegalArgumentException("White space on line " + this.fileStream.getLinenum() +
							" @ position " + this.fileStream.getCharpos() + " must be quoted.");
				}
				
				// start with a New line(empty line)
				else if (nexChar == 10) {
					this.tokenLine = this.fileStream.getLinenum();
					this.tokenPos = this.fileStream.getCharpos();
					this.emptycellInline = true;
					return 2;
				}
				
				// start with a ,
				else if (nexChar == 44) {
					this.fileStream.getNextChar();
					this.tokenLine = this.fileStream.getLinenum();
					this.tokenPos = this.fileStream.getCharpos();
					this.emptycellInline = true;
					return 1;
				}
				
				// start with a "
				else if (nexChar == 34) {
					quote_flag = true;
				}
				// Normal cases and " case
				this.fileStream.getNextChar();
				this.tokenLine = this.fileStream.getLinenum();
				this.tokenPos = this.fileStream.getCharpos();
				charBuf.add(nexChar);
			}
			// 2nd to last char
			else {
				// when started with a "
				if (quote_flag) {
					// reaches the end of file without closing the "
					if (nexChar == -1) {
						throw new IllegalArgumentException("Unexpected end of stream during quoted element.");
					}
					// consume any valid char
					this.fileStream.getNextChar();
					charBuf.add(nexChar);
					// if next char is the closing "
					if (nexChar == 34) {
						// peek one more to check validity
						if (!arrayCheck(this.fileStream.peekNextChar(), endChar)) {
							// consume next char to acquire the information for exception
							this.fileStream.getNextChar();
							throw new IllegalArgumentException("Unexpected character on line " + 
									this.fileStream.getLinenum() + " @ position " + 
									this.fileStream.getCharpos() + ".");
						}
						// consume next ,
						if (nexChar == 44) {
							this.fileStream.getNextChar();
						}
						// remove the double quotes
						//TODO: don't remove double quotes
						charBuf.remove(0);
						charBuf.remove(charBuf.size()-1);
						
						break;
					}
				}
				// 
				else {
					// unquoted whiteSpace
					if (arrayCheck(nexChar, whiteSpaces)) {
						// consume next char to acquire the information for exception
						this.fileStream.getNextChar();
						throw new IllegalArgumentException("White space on line " + this.fileStream.getLinenum() +
								" @ position " + this.fileStream.getCharpos() + " must be quoted.");
					}
					// end of cell or line
					else if (arrayCheck(nexChar, endChar)) {
						// consume next ,
						if (nexChar == 44) {
							this.fileStream.getNextChar();
						}
						break;
					}
					// Any remaining valid char
					else {
						this.fileStream.getNextChar();
						charBuf.add(nexChar);
					}
				}
			}
			
		}
		// construct the token in string format
		for (int i = 0; i < charBuf.size(); i++) {
			theToken += (char)charBuf.get(i).intValue();
		}
		// save the Token value
		this.nexToken = theToken;
		
		return 0;
	}
	
	private void getRawLine() {
		// getToken
		// case 0: Normal token
		// case -1: End of fileStream
		// case 1: single null cell
		// case 2: null row
		
		// reset the tokenBuf
		this.tokenBuf.clear();
		
		// while not reaches the end of the line
		while (true) {
			int TokenCase = getToken();
			
			if (TokenCase == 0) {
				this.tokenBuf.add(new theToken(nexToken, tokenLine, tokenPos, false));
			}
			else if (TokenCase == 1) {
				this.tokenBuf.add(new theToken("null", tokenLine, tokenPos, true));
			}
			else if (TokenCase == 2) {
				// check if the Header exist
				if (this.headerList.size() == 0) {
					throw new IllegalArgumentException("Cannot construct a header line with null");
				}
				for (int i = 0; i < this.headerList.size(); i++) {
					this.tokenBuf.add(new theToken(nexToken, tokenLine, tokenPos, true));
				}
			}
			else if (TokenCase == -1) {
				return;
			}
			
			if (this.fileStream.peekNextChar() == 10) {
				// consume new line char
				this.fileStream.getNextChar();
				break;
			}
		}
		return;
	}
	
	private void getHeader() {
		// read the 1st line of the file into the tokenBuf
		getRawLine();
		// check null
		if (this.emptycellInline) {
			// find the location of the 1st empty cell
			for (int i = 0; i < this.tokenBuf.size(); i++) {
				if (this.tokenBuf.get(i).isEmpty) {
					throw new IllegalArgumentException("Empty header column on line " + this.tokenBuf.get(i).linenum +
							" @ position " + this.tokenBuf.get(i).pos + ".");
				}
			}
		}
		int[] dupCheckResult = duplicateCheck(this.tokenBuf);
		
		if (dupCheckResult[0] == 1) {
			throw new IllegalArgumentException("Duplicate header column on line " + dupCheckResult[1] + " @ position " + 
					dupCheckResult[2] + ".");
		}
		for (int i = 0; i < this.tokenBuf.size(); i++) {
			this.headerList.add(this.tokenBuf.get(i));
		}
		this.tokenBuf.clear();
	}
	
	private void getMap() {
		
		Map<String,String> tmpMap = new HashMap<String,String>();
		
		getRawLine();
		// More column than the header has
		if (this.tokenBuf.size() > this.headerList.size()) {
			throw new IllegalArgumentException("Additional column on line " + this.tokenBuf.get(this.headerList.size()).linenum + 
					" @ position " + this.tokenBuf.get(this.headerList.size()).pos);
		}
		else if (this.tokenBuf.size() < this.headerList.size()) {
			for (int i = this.tokenBuf.size(); i < this.headerList.size(); i++) {
				// fulfill missing column
				this.tokenBuf.add(new theToken("null", tokenLine, tokenPos, false));
			}
		}
		
		// construct the map
		for (int i = 0; i < this.headerList.size(); i++) {
			tmpMap.put(this.headerList.get(i).Token, this.tokenBuf.get(i).Token);
		}
		this.lineBuf.add(tmpMap);
	}
	
	
	// helper function
	public static boolean arrayCheck(int targetElement, int[] targetArray) {
		for (int i : targetArray) {
			if (i == targetElement) {
				return true;
			}
		}
		return false;
	}
	
	private boolean validityCheck(int target) {
		int[] specialCases = {-1, 9, 10, 13};
		// check if the char is in the acceptable range within ascii table
		if (arrayCheck(target, specialCases) || (target >= 32 && target <= 126)) {
			return true;
		}
		return false;
	}
	
	public int[] duplicateCheck(ArrayList<theToken> targetList) {
		int[] res = {0, 0, 0};
		for (int i = 0; i < targetList.size()-1; i++) {
			for (int j = i+1; j < targetList.size(); j++) {
				if (targetList.get(i).Token.equals(targetList.get(j).Token)) {
					res[0] = 1;
					res[1] = targetList.get(j).linenum;
					res[2] = targetList.get(j).pos;
				}
			}
		}
		
		return res;
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		
		PeekableCharacterStream PCFS = new PCFileStream(args[0]);
		CSVParser myParser = new CSVParser(PCFS);
		Map<String,String> myMap;
		while (myParser.fileStream.peekNextChar() != -1) {
			myMap = myParser.getNextRow();
			System.out.println(myMap);
		}
				
	}
	
}
