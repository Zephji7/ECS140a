import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class XFormatter implements XParser.Observer{
	
	class varfun {
		private String refid;
		private int scope;
		
		public varfun(String inrefid, int inscope) {
			this.refid = inrefid;
			this.scope = inscope;
		}
		
		public String getrefid() {
			return this.refid;
		}
		
		public int getscope() {
			return this.scope;
		}
	}
	
	
	// indicators
	int varnum = 0;
	int funnum = 0;
	int blockCounter = 0;
	boolean isFirst = false;
	boolean flag_ref = false;
	
	XParser xparser = null;
	
	//format map
	Map<String, Map<String, String>> elementFormat;
	
	// symbol table
	HashMap<String, HashMap<String, LinkedList<varfun>>> symbolTable;
	
	//
	LinkedList<String> parsingScope;
	
	LinkedList<Token> tokenBuf;
	
	
	public XFormatter(formatReader readFormat, XParser theParser) {
		this.xparser = theParser;
		this.xparser.setOberver(this);
		this.elementFormat = readFormat.getformatMap();
		this.parsingScope = new LinkedList<>();
		this.symbolTable = new HashMap<>();
		this.symbolTable.put("FUN", new HashMap<>());
		this.symbolTable.put("VAR", new HashMap<>());
		this.tokenBuf = new LinkedList<>();
		
	}
	
	public void start(String element_type) {
		
		// add current parsing scope to the parsing scope list
		this.parsingScope.add(element_type);
		
		//System.out.println(element_type);
		
		// output all header information
		if (element_type.equals("Program")) {
			System.out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN"
					+ "\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n"
					+ "<head>\n<title>\nX Formatted file</title>\n</head>");
			
			String default_font_color = this.elementFormat.get("DEFAULT").get("FOREGROUND");
			
			System.out.println("<body bgcolor=\""+ this.elementFormat.get("DEFAULT").get("BACKGROUND") 
					+ "\" text=\"" + default_font_color + "\" link=\"" + default_font_color 
					+ "\" vlink=\"" + default_font_color + "\">\n");
			System.out.println("<font face=\"" + this.elementFormat.get("DEFAULT").get("FONT") + "\">");
		}
		
	}
	
	public void not(String element_type) {
		this.parsingScope.removeLast();
	}
	
	public void reportConsume() {
		// if there's identifier left inside the buffer
		// determine if it's the name of a function or variable
		if (!this.tokenBuf.isEmpty()) {
			String idType = "VAR";
			String element_Type = "VARIABLE";
			String idName = this.tokenBuf.getFirst().getText();
			// function name with be follow by a (
			if (this.xparser.currToken.getText().equals("(")) {
				idType = "FUN";
				element_Type = "FUNCTION";
			}
			
			boolean isDeclar = false;
			if ((scopeChecker("FunctionDeclaration") && idType.equals("FUN")) || ((scopeChecker("VariableDeclaration") || (scopeChecker("ParameterBlock") && scopeChecker("FunctionDefinition") && !scopeChecker("Block"))) && idType.equals("VAR"))) {
				addToSymbolTable(idName, idType);
				// print the <a
				System.out.print("<a name=\"" + this.symbolTable.get(idType).get(idName).getFirst().getrefid() + "\"/>");
				isDeclar = true;
			}
			// if this is not a declaration
			if (!isDeclar) {
				if (checkSymbolTable(idName, idType)) {
					System.out.print("<a href=\"#" + this.symbolTable.get(idType).get(idName).getFirst().getrefid() + "\">");
					this.flag_ref = true;
				}
			}
			outputFormat(idName, element_Type);
	
		}
		
		
		this.tokenBuf.add(this.xparser.currToken);
		//
		
		
		if (this.tokenBuf.getFirst().getType() == Token.TokenType.KEYWORD) {
			indentHandler();
			outputFormat(this.tokenBuf.getFirst().getText(), "KEYWORD");
		}
		else if (this.tokenBuf.getFirst().getType() == Token.TokenType.OPERATOR) {
			indentHandler();
			outputFormat(this.tokenBuf.getFirst().getText(), "OPERATOR");
		}
		else if (this.tokenBuf.getFirst().getType() == Token.TokenType.FLOAT_CONSTANT) {
			outputFormat(this.tokenBuf.getFirst().getText(), "FLOAT_CONSTANT");
		}
		else if (this.tokenBuf.getFirst().getType() == Token.TokenType.INT_CONSTANT) {
			outputFormat(this.tokenBuf.getFirst().getText(), "INT_CONSTANT");
		}
		
		else if (this.tokenBuf.getFirst().getType() == Token.TokenType.IDENTIFIER) {
			indentHandler();
			//don't know if it's var or fun yet
			// keep the token in the buffer
		}
	}
	
	
	public void end(String element_type) {
		this.parsingScope.removeLast();
		
		if (element_type.equals("Program")) {
			System.out.println("</font>\n</body>\n</html>");
		}
	}
	
	// program kicker
	public void startFormatting() {
		xparser.startParsing();
	}
	
	
	// helper function
	private void addToSymbolTable(String name, String idtype) {
		// default in var
		String tmprefid = "v" + this.varnum + name;
		this.varnum++;
		// if it's a function name instead
		if (idtype.equals("FUN")) {
			tmprefid = "f" + this.funnum + name;
			this.varnum--;
			this.funnum++;
		}
		boolean isExist = checkSymbolTable(name, idtype);
		
		int declarScope = this.blockCounter;
		
		if(scopeChecker("ParameterBlock") && scopeChecker("FunctionDefinition")) {
			declarScope++;
		}
		
		
		if (isExist) {
			// add it to the front of the existing symbol linkedlist
			this.symbolTable.get(idtype).get(name).addFirst(new varfun(tmprefid, declarScope));
		} else {
			// create new symbol table for a new var/fun
			LinkedList<varfun> tmplist = new LinkedList<>();
			tmplist.addFirst(new varfun(tmprefid, declarScope));
			this.symbolTable.get(idtype).put(name, tmplist);
		}

	}
	
	private boolean checkSymbolTable(String name, String idtype) {
		for (String i : this.symbolTable.get(idtype).keySet()) {
			if (i.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	
	private void outputFormat(String outToken, String element_type) {
		// Default, Variable, Function, Int_Constant, Float_Constant, Keyword, Operator
		//
		// case that need to add a space in the front of the token
		boolean isvalidOperator = (scopeChecker("RelationOperator") || scopeChecker("AddOperator") || scopeChecker("MultOperator") || outToken.equals("="));
		if (outToken.equals("=") || outToken.equals("-") || outToken.equals("+")) {
			System.out.print(" ");
		}
		
		//output the current token
		//
		System.out.print("<font color=\"" + this.elementFormat.get(element_type).get("FOREGROUND") + "\"");
		// none default style
		// background color
		if (!this.elementFormat.get(element_type).get("BACKGROUND").equals("null")) {
			System.out.print(" bgcolor=\"" + this.elementFormat.get(element_type).get("BACKGROUND") + "\"");
		}
		// font
		if (!this.elementFormat.get(element_type).get("FONT").equals("null")) {
			System.out.print(" face=\"" + this.elementFormat.get(element_type).get("FONT") + "\"");
		}
		
		System.out.print(">");
		// add bold mark
		if (!this.elementFormat.get(element_type).get("STYLE").equals("null")) {
			System.out.print("<" + this.elementFormat.get(element_type).get("STYLE") + ">");
		}
		
		if (outToken.equals("<")) {
			System.out.print("&lt;");
		}
		else if (outToken.equals(">")) {
			System.out.print("&gt;");
		} else {
			System.out.print(outToken);
		}
		
		if (!this.elementFormat.get(element_type).get("STYLE").equals("null")) {
			System.out.print("</" + this.elementFormat.get(element_type).get("STYLE") + ">");
		}
		System.out.print("</font>");
		
		//
		// if it's a ref
		if (this.flag_ref) {
			System.out.print("</a>");
			this.flag_ref = false;
		}
		
		// case that need to add a space after the token
		if (isvalidOperator || outToken.equals(",") || (element_type.equals("KEYWORD") && !outToken.equals("main"))) {
			System.out.print(" ");
		}
		// case that need to add a newline character after the token
		if (outToken.equals("{") || outToken.equals("}") || outToken.equals(";")) {
			if (outToken.equals("{")) {
				this.blockCounter++;
			}
			System.out.print("<br />\n");
			this.isFirst = true;
		}
		
		//remove the token from the buffer
		this.tokenBuf.removeFirst();
		
	}

	private void indentHandler() {
		if (this.isFirst) {
			// dedent if it's the } of a block
			// also check if there's var/fun declaration out of scope
			if (this.tokenBuf.getFirst().getText().equals("}")) {
				this.blockCounter--;
				scopeCleaner();
			}
			
			if (this.blockCounter > 0) {
				for (int i = 0; i < blockCounter; i++) {
					// add indentation if inside a block
					System.out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
				}
			}
		}
		// reset the isFirst flag
		this.isFirst = false;
	}

	private boolean scopeChecker(String targetScope) {
		for (String i : this.parsingScope) {
			if (i.equals(targetScope)) {
				return true;
			}
		}
		return false;
	}
	
	private void scopeCleaner() {
		String[] idTypes = {"FUN", "VAR"};
		
		// use a copy to modify
		HashMap<String, HashMap<String, LinkedList<varfun>>> tmpTable = new HashMap<>();
		tmpTable.putAll(this.symbolTable);
		
		// check out of scope
		for (String type : idTypes) {
			// use iterator to avoid concurrent exception
			Iterator<String> keys = this.symbolTable.get(type).keySet().iterator();
			
			while(keys.hasNext()) {
				String theKey = keys.next();
				if (this.symbolTable.get(type).get(theKey).getFirst().getscope() > this.blockCounter) {
					this.symbolTable.get(type).get(theKey).removeFirst();
				}
				if (this.symbolTable.get(type).get(theKey).isEmpty()) {
					keys.remove();
				}
			}
		
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 2) {
			return;
		}
		formatReader myReader = new formatReader(new CSVParser(new PCFileStream(args[0])));
		// keywords list
		List<String> kylist = Arrays.asList("unsigned","char","short","int","long","float",
				"double","while","if","return","void","main");
		PeekableCharacterStream PCFS = new PCFileStream(args[1]);
		Scanner myScanner = new Scanner(PCFS, kylist);
		XParser myParser = new XParser(myScanner);
		XFormatter myFormatter = new XFormatter(myReader, myParser);
		myParser.setOberver(myFormatter);
		
		myFormatter.startFormatting();
	}
}
