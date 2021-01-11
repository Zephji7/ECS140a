import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NRDatalogParser {
	
	
	Scanner tokenStream;
	
	
	// declaration list
	Token currToken;
	ArrayList<Token> tokenBuf = new ArrayList<>();
	boolean valid_flag = true;
	String error_message = "";
	
	public NRDatalogParser(PeekableCharacterStream stream) {
		List<String> kylist = Arrays.asList("AND", "NOT");
		this.tokenStream = new Scanner(stream, kylist);
	}
	
	public boolean parseQuery() {
		this.startParsing();
		// not valid
		if (!this.valid_flag) {
			PrintStream pstream = new PrintStream(System.out);
			this.printError(pstream);
			return false;
		}
		
		return true;
	}
	
	public void printError(PrintStream ostream) {
		ostream.println(this.error_message);
	}
	
	protected void startParsing() {
		this.isQuery();
	}
	
	//EBNF rules
	protected boolean isQuery() {
		// System.out.println("In Query");
		// TODO: need to modify to error
		// if there's no rule in query
		if (!isRule()) {
			syntax_errorHandling("QUERY");
			return false;
		}
		
		// 0 or more rule followed
		boolean repet_flag = isRule();
		while (repet_flag) {
			repet_flag = isRule();
		}
		
		return true;
	}
	
	protected boolean isRule() {
		// System.out.println("In Rule");
		if (!valid_flag) {
			return false;
		}
		boolean option_flag = false;
		// optional ruleHead
		if (isRuleHead()) {
			option_flag = true;
			// optional :=
			this.currToken = this.getNextToken();
			if (this.currToken.getText().equals(":=")) {
				this.consumeNextToken();
				if (!isRuleBody()) {
					syntax_errorHandling("RULE");
					return false;
				}
			}
		}
		
		// 1st require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("\n")) {
			if (option_flag) {
				syntax_errorHandling("RULE");
				return false;
			}
			// System.out.println("NOT Rule");
			return false;
		}
		
		this.consumeNextToken();
		// System.out.println("IS Rule");
		return true;
	}
	
	protected boolean isRuleHead() {
		// System.out.println("In RuleHead");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		if (!isRuleName()) {
			// System.out.println("NOT RuleHead");
			return false;
		}
		// 2nd require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("(")) {
			syntax_errorHandling("RULEHEAD");
			return false;
		}
		this.consumeNextToken();
		// 3rd require
		if (!isHeadVariableList()) {
			syntax_errorHandling("RULEHEAD");
			return false;
		}
		// 4th require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(")")) {
			syntax_errorHandling("RULEHEAD");
			return false;
		}
		
		this.consumeNextToken();
		// System.out.println("IS RuleHead");
		return true;
	}
	
	protected boolean isRuleBody() {
		// System.out.println("In RuleBody");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		if (!isSubGoal()) {
			// System.out.println("NOT RuleBody");
			return false;
		}
		// 0 or more
		this.currToken = this.getNextToken();
		boolean repet_flag = this.currToken.getText().equals("AND");
		while (repet_flag) {
			this.consumeNextToken();
			if (!isSubGoal()) {
				syntax_errorHandling("RULEBODY");
				return false;
			}
			this.currToken = this.getNextToken();
			repet_flag = this.currToken.getText().equals("AND");
		}
		// System.out.println("IS RuleBody");
		return true;
	}
	
	protected boolean isRuleName() {
		// System.out.println("In RuleName");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		this.currToken = this.getNextToken();

		if (this.currToken.getType() == Token.TokenType.IDENTIFIER && this.tokenStream.peekNextToken().getText().equals("(")) {
			// System.out.println("IS RuleName");
			this.consumeNextToken();
			return true;
		}
		
		// System.out.println("NOT RuleName");
		return false;
	}
	
	protected boolean isHeadVariableList() {
		// System.out.println("In HeadVariableList");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		// 1st require
		if (this.currToken.getType() != Token.TokenType.IDENTIFIER) {
			// System.out.println("NOT HeadVariableList");
			return false;
		}
		this.consumeNextToken();
		
		// 0 or more
		this.currToken = this.getNextToken();
		boolean repet_flag = this.currToken.getText().equals(",");
		while (repet_flag) {
			this.consumeNextToken();
			this.currToken = this.getNextToken();
			if (this.currToken.getType() != Token.TokenType.IDENTIFIER) {
				syntax_errorHandling("HEADVARIABLELIST");
				return false;
			}
			this.consumeNextToken();
			this.currToken = this.getNextToken();
			repet_flag = this.currToken.getText().equals(",");
		}
		// System.out.println("IS HeadVariableList");
		return true;
	}
	
	protected boolean isSubGoal() {
		// System.out.println("In SubGoal");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		if (!(isRuleInvocation() || isNegatedRuleInvocation() || isEqualityRelation())) {
			// System.out.println("NOT SubGoal");
			return false;
		}
		
		// System.out.println("IS SubGoal");
		return true;
	}
	
	protected boolean isRuleInvocation() {
		// System.out.println("In RuleInvocation");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		if (!isRuleName()) {
			// System.out.println("NOT RuleInvocation");
			return false;
		}
		// 2nd require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("(")) {
			// System.out.println("NOT RuleInvocation");
			return false;
		}
		this.consumeNextToken();
		// 3rd require
		if (!isBodyVariableList()) {
					syntax_errorHandling("RULEINVOCATION");
					return false;
		}
		// 4th require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals(")")) {
			syntax_errorHandling("RULEINVOCATION");
			return false;
		}
		
		this.consumeNextToken();
		// System.out.println("IS RuleInvocation");
		return true;
	}
	
	protected boolean isNegatedRuleInvocation() {
		// System.out.println("In NegatedRuleInvocation");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		this.currToken = this.getNextToken();
		if (!this.currToken.getText().equals("NOT")) {
			// System.out.println("NOT NegatedRuleInvocation");
			return false;
		}
		this.consumeNextToken();
		// 2nd require
		if (!isRuleInvocation()) {
			syntax_errorHandling("NEGATEDRULEINVOCATION");
			return false;
		}
		
		// System.out.println("IS NegatedRuleInvocation");
		return true;
	}
	
	protected boolean isEqualityRelation() {
		// System.out.println("In EqualityRelation");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		if (!isInequalityRelation()) {
			// System.out.println("NOT EqualityRelation");
			return false;
		}
		
		// 0 or more
		boolean repet_flag = isEQOperator();
		while (repet_flag) {
			if (!isInequalityRelation()) {
				syntax_errorHandling("EQUALITYRELATION");
				return false;
			}
			repet_flag = isEQOperator();
		}
		
		// System.out.println("IS EqualityRelation");
		return true;
	}
	
	protected boolean isBodyVariableList() {
		if (!valid_flag) {
			return false;
		}
		// System.out.println("In BodyVariableList");
		// 1st require
		if (!isInvocationVariable()) {
			// System.out.println("NOT BodyVariableList");
			return false;
		}
		
		// 0 or more
		this.currToken = this.getNextToken();
		boolean repet_flag = this.currToken.getText().equals(",");
		while (repet_flag) {
			this.consumeNextToken();
			if (!isInvocationVariable()) {
				syntax_errorHandling("BODYVARIABLELIST");
				return false;
			}
			this.currToken = this.getNextToken();
			repet_flag = this.currToken.getText().equals(",");
		}
		// System.out.println("IS BodyVariableList");
		return true;
	}
	
	protected boolean isInequalityRelation() {
		if (!valid_flag) {
			return false;
		}
		// System.out.println("In InequalityRelation");
		// 1st require
		if (!isTerm()) {
			// System.out.println("NOT InequalityRelation");
			return false;
		}
		
		// 0 or more
		boolean repet_flag = isIEQOperator();
		while (repet_flag) {
			if (!isTerm()) {
				syntax_errorHandling("INEQUALITYRELATION");
				return false;
			}
			repet_flag = isIEQOperator();
		}
		// System.out.println("IS InequalityRelation");
		return true;		
	}
	
	protected boolean isEQOperator() {
		// System.out.println("In EQOperator");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if (this.currToken.getText().equals("!=") || this.currToken.getText().equals("=")) {
			this.consumeNextToken();
			// System.out.println("IS EQOperator");
			return true;
		}
		// System.out.println("NOT EQOperator");
		return false;
	}
	
	protected boolean isInvocationVariable() {
		// System.out.println("In InvocationVariable");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if ((this.currToken.getType() ==  Token.TokenType.IDENTIFIER) || (this.currToken.getType() ==  Token.TokenType.EMPTY_IDENTIFIER)) {
			this.consumeNextToken();
			// System.out.println("IS InvocationVariable");
			return true;
		}
		// System.out.println("NOT InvocationVariable");
		return false;
	}
	
	protected boolean isTerm() {
		// System.out.println("In Term");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		if (!isSimpleTerm()) {
			// System.out.println("NOT Term");
			return false;
		}
		
		// 0 or more
		boolean repet_flag = isAddOperator();
		while (repet_flag) {
			if (!isSimpleTerm()) {
				syntax_errorHandling("TERM");
				return false;
			}
			repet_flag = isAddOperator();
		}
		// System.out.println("IS Term");
		return true;	
	}
	
	protected boolean isIEQOperator() {
		// System.out.println("In IEQOperator");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if (this.currToken.getText().equals("<") || this.currToken.getText().equals(">") || this.currToken.getText().equals("<=") || this.currToken.getText().equals(">=")) {
			this.consumeNextToken();
			// System.out.println("IS IEQOperator");
			return true;
		}
		// System.out.println("NOT IEQOperator");
		return false;
	}
	
	protected boolean isSimpleTerm() {
		// System.out.println("In SimpleTerm");
		if (!valid_flag) {
			return false;
		}
		// 1st require
		if (!isUnaryExpression()) {
			// System.out.println("NOT SimpleTerm");
			return false;
		}
		
		// 0 or more
		boolean repet_flag = isMultOperator();
		while (repet_flag) {
			if (!isUnaryExpression()) {
				syntax_errorHandling("SIMPLETERM");
				return false;
			}
			repet_flag = isMultOperator();
		}
		// System.out.println("IS SimpleTerm");
		return true;
	}
	
	protected boolean isAddOperator() {
		// System.out.println("In AddOperator");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if (this.currToken.getText().equals("+") || this.currToken.getText().equals("-")) {
			this.consumeNextToken();
			// System.out.println("IS AddOperator");
			return true;
		}
		// System.out.println("NOT AddOperator");
		return false;
	}
	
	protected boolean isUnaryExpression() {
		// System.out.println("In UnaryExpression");
		if (!valid_flag) {
			return false;
		}
		if (isPrimaryExpression()) {
			// System.out.println("IS UnaryExpression");
			return true;
		}
		else if (isUnaryOperator()) {
			if (!isUnaryExpression()) {
				syntax_errorHandling("UNARYEXPRESSION");
				return false;
			}
			// System.out.println("IS UnaryExpression");
			return true;
		}
		// System.out.println("NOT UnaryExpression");
		return false;
	}
	
	protected boolean isMultOperator() {
		// System.out.println("In MultOperator");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if (this.currToken.getText().equals("*") || this.currToken.getText().equals("/") || this.currToken.getText().equals("%")) {
			this.consumeNextToken();
			// System.out.println("IS MultOperator");
			return true;
		}
		// System.out.println("NOT MultOperator");
		return false;
	}
	
	protected boolean isUnaryOperator() {
		// System.out.println("In UnaryOperator");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if (this.currToken.getText().equals("!") || this.currToken.getText().equals("-") || this.currToken.getText().equals("+")) {
			this.consumeNextToken();
			// System.out.println("IS UnaryOperator");
			return true;
		}
		// System.out.println("NOT UnaryOperator");
		return false;
	}
	
	protected boolean isPrimaryExpression() {
		// System.out.println("In PrimaryExpression");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if (this.currToken.getType() == Token.TokenType.IDENTIFIER) {
			this.consumeNextToken();
			// System.out.println("IS PrimaryExpression");
			return true;
		}
		else if (isConstant()) {
			// System.out.println("IS PrimaryExpression");
			return true;
		}
		else if (this.currToken.getText().equals("(")) {
			this.consumeNextToken();
			if (!isEqualityRelation()) {
				syntax_errorHandling("PRIMARYEXPRESSION");
				return false;
			}
			this.currToken = this.getNextToken();
			if (!this.currToken.getText().equals(")")) {
				syntax_errorHandling("PRIMARYEXPRESSION");
				return false;
			}
			
			this.consumeNextToken();
			// System.out.println("IS PrimaryExpression");
			return true;
		}
		// System.out.println("NOT PrimaryExpression");
		return false;
	}
	
	protected boolean isConstant() {
		// System.out.println("In Constant");
		if (!valid_flag) {
			return false;
		}
		this.currToken = this.getNextToken();
		if ((this.currToken.getType() == Token.TokenType.INT_CONSTANT) || (this.currToken.getType() == Token.TokenType.FLOAT_CONSTANT) || (this.currToken.getType() == Token.TokenType.STRING_CONSTANT)) {
			this.consumeNextToken();
			// System.out.println("IS Constant");
			return true;
		}
		// System.out.println("NOT Constant");
		return false;
	}
	
	
	
	
	// helper function
	protected Token getNextToken() {
		if (this.tokenBuf.size() == 0) {
			// buffer from scanner
			this.tokenBuf.add(this.tokenStream.getNextToken());
		}
		return this.tokenBuf.get(0);
	}
	
	
	protected void consumeNextToken() {
		//TODO: uncomment this line 
		//observer.reportConsume();
		// System.out.println(this.currToken.getText());
		this.tokenBuf.remove(0);
	}
	
	private void syntax_errorHandling(String rule) {
		this.error_message = this.error_message + "Syntax Error: In parsing " + rule + " unexpected token \"" + this.currToken.getText() + "\" of type " + 
	this.currToken.getType() + " on line " + this.currToken.getLineNumber() + " @ position " + this.currToken.getCharPosition() + ".";
		this.valid_flag = false;
	}
	
	
	
	public static void main(String args[]) throws FileNotFoundException {
		// No Input file provided
		if (args.length == 0) {
			return;
		}
		PeekableCharacterStream PCFS = new PCFileStream(args[0]);
		NRDatalogParser myParser = new NRDatalogParser(PCFS);
		if (myParser.parseQuery()) {
			System.out.println(args[0] + " is a valid .nrdl program!");
		}
	}
}
