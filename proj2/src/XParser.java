import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class XParser {
	
	// Observer use
	public interface Observer {
		public void start(String element_type);
		public void not(String element_type);
		public void reportConsume();
		public void end(String element_type);
	}
	
	class ObserverShell implements Observer {
		public void start(String element_type) {
						
		}
		public void not(String element_type) {
			
		}
		
		public void reportConsume() {
			
		}
		public void end(String element_type) {
			
		}
	}
	
	private Observer observer = new ObserverShell();
	
	public Observer getObserver() {
		return observer;
	}
	
	public void setOberver(Observer obs) {
		observer = obs;
	}
	
	
	
	
	
	Scanner tokenStream;
	
	
	
	// declaration list
	Token currToken;
	ArrayList<Token> tokenBuf = new ArrayList<>();

	// program kicker
	public void startParsing() {
		this.isProgram();
	}
	
	
	// EBNF rules
	private void isProgram() {
		//System.out.println("Start isProgram");
		observer.start("Program");
		boolean flag_isDeclaration = isDeclaration();
		
		// 0 or more Declaration
		while (flag_isDeclaration) {
			flag_isDeclaration = isDeclaration();
		}
		if (!isMainDeclaration()) {
			//TODO: errorHandling
			errorHandling("PROGRAM");
		}
		
		boolean flag_isFunctionDefinition = isFunctionDefinition();
		// 0 or more Declaration
		while (flag_isFunctionDefinition) {
			flag_isFunctionDefinition = isFunctionDefinition();
		}
		
		//System.out.println("End isProgram");
		observer.end("Program");
	}
	
	
	private boolean isDeclaration() {
		//System.out.println("Start isDeclaration");
		observer.start("Declaration");
		if(!isDeclarationType()) {
			//System.out.println("Not isDeclaration");
			
			observer.not("Declaration");
			return false;
		}
		
		if (!(isFunctionDeclaration() || isVariableDeclaration())) {
			errorHandling("DECLARATION");
		}
		
		//System.out.println("End isDeclaration");
		observer.end("Declaration");
		return true;
	}
	
	//TODO: deal with it later
	private boolean isMainDeclaration() {
		//System.out.println("Start isMainDeclaration");
		observer.start("MainDeclaration");
		// 1st require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("void")) {
			
			//System.out.println("Not isMainDeclaration");
			observer.not("MainDeclaration");
			return false;
		}
		this.consumeNextToken();
		// 2nd require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("main")) {
			errorHandling("MAINDECLARATION");
		}
		this.consumeNextToken();
		// 3rd require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("(")) {
			errorHandling("MAINDECLARATION");
		}
		this.consumeNextToken();
		// 4th require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(")")) {
			errorHandling("MAINDECLARATION");
		}
		this.consumeNextToken();
		// 5th require
		if (!isBlock()) {
			errorHandling("MAINDECLARATION");
		}
		
		//System.out.println("End isMainDeclaration");
		
		observer.end("MainDeclaration");
		return true;
	}
	
	
	private boolean isFunctionDefinition() {
		//System.out.println("Start isFunctionDefinition");
		observer.start("FunctionDefinition");
		// 1st require element
		if (!isDeclarationType()) {
			
			//System.out.println("Not isFunctionDefinition");
			observer.not("FunctionDefinition");
			return false;
		}
		// 2nd require element
		if (!isParameterBlock()) {
			errorHandling("FUNCTION_DEFINITION");
		}
		// 3rd require element
		if (!isBlock()) {
			errorHandling("FUNCTION_DEFINITION");
		}
		
		//System.out.println("End isFunctionDefinition");	
		observer.end("FunctionDefinition");
		return true;
	}
	
	
	private boolean isDeclarationType() {
		//System.out.println("Start isDeclarationType");
		observer.start("DeclarationType");
		// 1st require element
		if (!isDataType()) {
			//System.out.println("Not isDeclarationType");
			observer.not("DeclarationType");
			return false;
		}
		// 2nd require element
		this.currToken = this.getNextToken();
		if (this.currToken.getType() != Token.TokenType.IDENTIFIER) {
			errorHandling("DECLARATION_TYPE");
		}
		
		this.consumeNextToken();
		//System.out.println("End isDeclarationType");
		observer.end("DeclarationType");
		return true;
	}
	
	// TODO: double check when to return false
	private boolean isVariableDeclaration() {
		//System.out.println("Start isVariableDeclaration");
		observer.start("VariableDeclaration");
		boolean flag_optional = false;
		// Optional element
		this.currToken = this.getNextToken();
		// 1st require element in optional
		if (this.currToken.getText().equals("=")) {
			// consume the token
			this.consumeNextToken();
			flag_optional = true;
			// 2nd require element in optional
			if (!isConstant()) {
				errorHandling("VARIABLE_DECLARATION");
			}
		}
		// 1st require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(";")) {
			if (flag_optional) {
				errorHandling("VARIABLE_DECLARATION");
			}
			observer.not("VariableDeclaration");
			return false;
		}
		
		this.consumeNextToken();
		//System.out.println("End isVariableDeclaration");
		observer.end("VariableDeclaration");
		return true;
	}
	
	
	private boolean isFunctionDeclaration() {
		//System.out.println("Start isFunctionDeclaration");
		observer.start("FunctionDeclaration");
		// 1st require element
		if (!isParameterBlock()) {
			//System.out.println("Not isFunctionDeclaration");
			observer.not("FunctionDeclaration");
			return false;
		}
		// 2nd require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(";")) {
			errorHandling("FUNCTION_DECLARATION");
		}
		
		this.consumeNextToken();
		//System.out.println("End isFunctionDeclaration");	
		observer.end("FunctionDeclaration");
		return true;
	}
	
	
	private boolean isBlock() {
		//System.out.println("Start isBlock");
		observer.start("Block");
		// 1st require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("{")) {
			//System.out.println("Not isBlock");
			observer.not("Block");
			return false;
		}
		this.consumeNextToken();
		// optional repetition element
		boolean flag_isDeclaration = isDeclaration();
		while (flag_isDeclaration) {
			flag_isDeclaration = isDeclaration();
		}
		// optional element
		if (isStatement()) {
			boolean flag_isStatement = isStatement();
			while (flag_isStatement) {
				flag_isStatement = isStatement();
			}
			boolean flag_isFunctionDefinition = isFunctionDefinition();
			while (flag_isFunctionDefinition) {
				flag_isFunctionDefinition = isFunctionDefinition();
			}
		}
		// 2nd require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("}")) {
			errorHandling("BLOCK");
		}
		
		this.consumeNextToken();
		//System.out.println("End isBlock");
		observer.end("Block");
		return true;
	}
	
	
	private boolean isParameterBlock() {
		//System.out.println("Start isParameterBlock");
		observer.start("ParameterBlock");
		// 1st require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("(")) {
			
			//System.out.println("Not isParameterBlock");
			observer.not("ParameterBlock");
			return false;
		}
		this.consumeNextToken();
		if (isParameter()) {
			boolean flag_repetition = false;
			this.currToken = this.getNextToken();
			if (this.currToken.getText().equals(",")) {
				flag_repetition = true;
				this.consumeNextToken();
			}
			while (flag_repetition) {
				if (!isParameter()) {
					errorHandling("PARAMETER_BLOCK");
				}
				
				this.currToken = this.getNextToken();
				if (this.currToken.getText().equals(",")) {
					flag_repetition = true;
					this.consumeNextToken();
				} else {
					flag_repetition = false;
				}
				
			}
		}
		// 2nd require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(")")) {
			errorHandling("PARAMETER_BLOCK");
		}
		
		this.consumeNextToken();
		//System.out.println("End isParameterBlock");
		observer.end("ParameterBlock");
		return true;
	}
	
	
	private boolean isDataType() {
		//System.out.println("Start isDataType");
		observer.start("DataType");
		if (!(isIntegerType() || isFloatType())) {
			
			//System.out.println("Not isDataType");
			observer.not("DataType");
			return false;
		}
		
		//System.out.println("End isDataType");
		observer.end("DataType");
		return true;
	}
	
	
	private boolean isConstant() {
		//System.out.println("Start isConstant");
		observer.start("Constant");
		this.currToken = this.getNextToken();
		if (!(this.currToken.getType() == Token.TokenType.INT_CONSTANT || this.currToken.getType() == Token.TokenType.FLOAT_CONSTANT)) {
			
			//System.out.println("Not isConstant");
			observer.not("Constant");
			return false;
		}
		
		this.consumeNextToken();
		//System.out.println("End isConstant");
		observer.end("Constant");
		return true;
	}
	
	
	private boolean isStatement() {
		//System.out.println("Start isStatement");
		observer.start("Statement");
		if (!(isAssignment() || isWhileLoop() || isIfStatement() || isReturnStatement())) {
			if (isExpression()) {
				this.currToken = this.getNextToken();
				if (this.currToken.getText().equals(";")) {
					errorHandling("STATEMENT");
				}
				
				this.consumeNextToken();
				//System.out.println("End isStatement");
				observer.end("Statement");
				return true;
			}
			//System.out.println("Not isStatement");
			observer.not("Statement");
			return false;
		}
		//System.out.println("End isStatement");
		observer.end("Statement");
		return true;
	}
	
	
	private boolean isParameter() {
		//System.out.println("Start isParameter");
		observer.start("Parameter");
		// 1st require element
		if (!isDataType()) {
			//System.out.println("Not isParameter");
			observer.not("Parameter");
			return false;
		}
		//2nd require element
		this.currToken = this.getNextToken();
		if (this.currToken.getType() != Token.TokenType.IDENTIFIER) {
			errorHandling("PARAMETER");
		}
		
		this.consumeNextToken();
		//System.out.println("End isParameter");
		observer.end("Parameter");
		return true;
	}
	
	
	private boolean isIntegerType() {
		//System.out.println("Start isIntegerType");
		observer.start("IntegerType");
		boolean flag_optional = false;
		this.currToken = this.getNextToken();
		// optional element
		if (this.currToken.getText().equals("unsigned")) {
			this.consumeNextToken();
			flag_optional = true;
		}
		// 1st require element
		this.currToken = this.getNextToken();
		if (!(this.currToken.getText().equals("char") || this.currToken.getText().equals("short") || 
				this.currToken.getText().equals("int")  || this.currToken.getText().equals("long"))) {
			if (flag_optional) {
				errorHandling("INTEGERTYPE");
			}
			//System.out.println("Not isIntegerType");
			observer.not("IntegerType");
			return false;
		}
		
		this.consumeNextToken();
		//System.out.println("End isIntegerType");
		observer.end("IntegerType");
		return true;
	}
	
	
	private boolean isFloatType() {
		//System.out.println("Start isFloatType");
		observer.start("FloatType");
		// 1st require element
		this.currToken = this.getNextToken();
		if (!(this.currToken.getText().equals("float") || this.currToken.getText().equals("double"))) {
			
			//System.out.println("Not isFloatType");
			observer.not("FloatType");
			return false;
		}
		
		this.consumeNextToken();
		//System.out.println("End isFloatType");
		observer.end("FloatType");
		return true;
	}
	
	
	private boolean isAssignment() {	
		//System.out.println("Start isAssignment");
		observer.start("Assignment");
		// 1st require element
		this.currToken = this.getNextToken();
		if (this.currToken.getType() != Token.TokenType.IDENTIFIER) {
			//System.out.println("Not isAssignment");
			observer.not("Assignment");
			return false;
		}
		this.consumeNextToken();
		// 2nd require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("=")) {
			errorHandling("ASSIGNMENT");
		}
		this.consumeNextToken();
		
		// TODO: peek one more
		// optional repetition
		boolean flag_repetition = false;
		while (flag_repetition) {
			// 1st require
			// And peek one more to distinguish from Expression
			this.currToken = this.getNextToken();
			if (this.currToken.getType() != Token.TokenType.IDENTIFIER || !this.tokenStream.peekNextToken().getText().equals("=")) {
				flag_repetition = false;
				break;
			}
	
			this.consumeNextToken();
			this.currToken = this.getNextToken();
			if (!this.currToken.getText().equals("=")) {
				errorHandling("ASSIGNMENT");
			}
			this.consumeNextToken();
		}		
		
		// 3rd require element
		if (!isExpression()) {
			errorHandling("ASSIGNMENT");
		}
		//4th require element
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(";")) {
			errorHandling("ASSIGNMENT");
		}
		
		this.consumeNextToken();
		//System.out.println("End isAssignment");
		observer.end("Assignment");
		return true;
	}
	
	
	private boolean isWhileLoop() {
		//System.out.println("Start isWhileLoop");
		observer.start("WhileLoop");
		// 1st require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("while")) {
			
			//System.out.println("Not isWhileLoop");
			observer.not("WhileLoop");
			return false;
		}
		this.consumeNextToken();
		// 2nd require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("(")) {
			errorHandling("WHILELOOP");
		}
		this.consumeNextToken();
		// 3rd require
		if (!isExpression()) {
			errorHandling("WHILELOOP");
		}
		// 4th require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(")")) {
			errorHandling("WHILELOOP");
		}
		this.consumeNextToken();
		// 5th require
		if (!isBlock()) {
			errorHandling("WHILELOOP");
		}
		
		//System.out.println("End isWhileLoop");
		observer.end("WhileLoop");
		return true;
	}
	
	
	private boolean isIfStatement() {
		//System.out.println("Start isIfStatement");
		observer.start("IfStatement");
		// 1st require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("if")) {
			
			//System.out.println("Not isIfStatement");
			observer.not("IfStatement");
			return false;
		}
		this.consumeNextToken();
		// 2nd require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("(")) {
			errorHandling("IFSTATEMENT");
		}
		this.consumeNextToken();
		// 3rd require
		if (!isExpression()) {
			errorHandling("IFSTATEMENT");
		}
		// 4th require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(")")) {
			errorHandling("IFSTATEMENT");
		}
		this.consumeNextToken();
		// 5th require
		if (!isBlock()) {
			errorHandling("IFSTATEMENT");
		}
		
		//System.out.println("End isIfStatement");
		observer.end("IfStatement");
		return true;
	}
	
	
	private boolean isReturnStatement() {
		//System.out.println("Start isReturnStatement");
		observer.start("ReturnStatement");
		// 1st require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("return")) {
			
			//System.out.println("Not isReturnStatement");
			observer.not("ReturnStatement");
			return false;
		}
		this.consumeNextToken();
		// 2nd require
		if (!isExpression()) {
			errorHandling("RETURNSTATEMENT");
		}
		// 3rd require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(";")) {
			errorHandling("RETURNSTATEMENT");
		}
		
		this.consumeNextToken();
		//System.out.println("End isReturnStatement");
		observer.end("ReturnStatement");
		return true;
	}
	
	
	private boolean isExpression() {
		//System.out.println("Start isExpression");
		observer.start("Expression");
		// 1st require
		if (!isSimpleExpression()) {
			//System.out.println("Not isExpression");
			observer.not("Expression");
			return false;
		}
		// optional
		if (isRelationOperator()) {
			if (!isSimpleExpression()) {
				errorHandling("EXPRESSION");
			}
		}
		
		//System.out.println("End isExpression");
		observer.end("Expression");
		return true;
	}
	
	
	private boolean isSimpleExpression() {
		//System.out.println("Start isSimpleExpression");
		observer.start("SimpleExpression");
		// 1st require
		if (!isTerm()) {
			
			//System.out.println("Not isSimpleExpression");
			observer.not("SimpleExpression");
			return false;
		}
		// optional repetition
		boolean flag_repetition = isAddOperator();
		while (flag_repetition) {
			if (!isTerm()) {
				errorHandling("SIMPLEEXPRESSION");
			}
			flag_repetition = isAddOperator();
		}
		
		//System.out.println("End isSimpleExpression");
		observer.end("SimpleExpression");
		return true;
	}
	
	
	private boolean isTerm() {
		//System.out.println("Start isTerm");
		observer.start("Term");
		// 1st require
		if (!isFactor()) {
			
			//System.out.println("Not isTerm");
			observer.not("Term");
			return false;
		}
		// optional repetition
		boolean flag_repetition = isMultOperator();
		while (flag_repetition) {
			if (!isFactor()) {
				errorHandling("TERM");
			}
			flag_repetition = isMultOperator();
		}
		
		//System.out.println("End isTerm");
		observer.end("Term");
		return true;
	}
	
	
	private boolean isFactor() {
		//System.out.println("Start isFactor");
		observer.start("Factor");
		// 1st case
		if (isConstant()) {
			
			//System.out.println("End isFactor");
			observer.end("Factor");
			return true;
		}
		this.currToken = this.getNextToken();
		// 2nd case
		if (this.currToken.getText().equals("(")) {
			this.consumeNextToken();
			if (!isExpression()) {
				errorHandling("FACTOR");
			}
			this.currToken = this.getNextToken();
			if (!this.currToken.getText().equals(")")) {
				errorHandling("FACTOR");
			}
			
			this.consumeNextToken();
			//System.out.println("End isFactor");
			observer.end("Factor");
			return true;
		}
		// 3rd case
		else if (this.currToken.getType() == Token.TokenType.IDENTIFIER) {
			this.consumeNextToken();
			// optional
			this.currToken = this.getNextToken();
			if (this.currToken.getText().equals("(")) {
				this.consumeNextToken();
				// optional
				if (isExpression()) {
					// optional repetition
					boolean flag_repetition = true;
					while (flag_repetition) {
						this.currToken = this.getNextToken();
						if (!this.currToken.getText().equals(",")) {
							flag_repetition = false;
							break;
						}
						this.consumeNextToken();
						if (!isExpression()) {
							errorHandling("FACTOR");
						}
					}
				}
				this.currToken = this.getNextToken();
				if (!this.currToken.getText().equals(")")) {
					errorHandling("FACTOR");
				}
				this.consumeNextToken();
			}
			
			//System.out.println("End isFactor");
			observer.end("Factor");
			return true;
		}
		
		//System.out.println("Not isFactor");
		observer.not("Factor");
		return false;
	}
	
	
	private boolean isRelationOperator() {
		//System.out.println("Start isRelationOperator");
		observer.start("RelationOperator");
		this.currToken = this.getNextToken();
		String[] relationOperator = {"==", "<", ">", "<=", ">=", "!="};
		for (int i = 0; i < relationOperator.length; i++) {
			if (this.currToken.getText().equals(relationOperator[i])) {
				
				this.consumeNextToken();
				//System.out.println("End isRelationOperator");
				observer.end("RelationOperator");
				return true;
			}
		}
		
		//System.out.println("Not isRelationOperator");
		observer.not("RelationOperator");
		return false;
	}
	
	
	private boolean isAddOperator() {
		//System.out.println("Start isAddOperator");
		observer.start("AddOperator");
		this.currToken = this.getNextToken();
		if (this.currToken.getText().equals("+") || this.currToken.getText().equals("-")) {
			
			this.consumeNextToken();
			//System.out.println("End isAddOperator");
			observer.end("AddOperator");
			return true;
		}
		
		//System.out.println("Not isAddOperator");
		observer.not("AddOperator");
		return false;
	}
	
	
	private boolean isMultOperator() {
		//System.out.println("Start isMultOperator");
		observer.start("MultOperator");
		this.currToken = this.getNextToken();
		if (this.currToken.getText().equals("*") || this.currToken.getText().equals("/")) {
			
			this.consumeNextToken();
			//System.out.println("End isMultOperator");
			observer.end("MultOperator");
			return true;
		}
		
		//System.out.println("Not isMultOperator");
		observer.not("MultOperator");
		return false;
	}
	
	private void errorHandling(String rule) {
		System.out.println("In parsing " + rule + " unexpected token \"" + this.currToken.getText() + "\" of type " + 
	this.currToken.getType() + " on line " + this.currToken.getLineNumber() + " @ position " + this.currToken.getCharPosition() + ".");
		System.exit(1);
	}
	
	public XParser(Scanner inputStream) {
		this.tokenStream = inputStream;
	}
	
	
	// helper function
	private Token getNextToken() {
		if (this.tokenBuf.size() == 0) {
			// buffer from scanner
			this.tokenBuf.add(this.tokenStream.getNextToken());
		}
		return this.tokenBuf.get(0);
	}
	
	
	private void consumeNextToken() {
		//TODO: uncomment this line 
		observer.reportConsume();
		//System.out.println(this.currToken.getText());
		this.tokenBuf.remove(0);
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		// No Input file provided
		if (args.length == 0) {
			return;
		}
		// keywords list
		List<String> kylist = Arrays.asList("unsigned","char","short","int","long","float",
				"double","while","if","return","void","main");
		PeekableCharacterStream PCFS = new PCFileStream(args[0]);
		Scanner myScanner = new Scanner(PCFS, kylist);
		XParser myParser = new XParser(myScanner);
		myParser.startParsing();
		System.out.println(args[0] + " is a valid .x program!");
	}

}