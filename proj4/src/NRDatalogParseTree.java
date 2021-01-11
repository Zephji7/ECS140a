import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;


public class NRDatalogParseTree extends NRDatalogParser{
	
	// data members
	Stack<node> nodeStack = new Stack<node>();
	String printTree = "";
	int depthCounter = 0;
	
	// node class
	class node {
		String ruleType;
		node parent;
		ArrayList<node> children;
		ArrayList<Token> tokenList;
		
		public node(String rtype) {
			this.ruleType = rtype;
			this.children = new ArrayList<>();
			this.tokenList = new ArrayList<>();
		}
	}
	
	
	// Main Interfaces
	// Constructor
	public NRDatalogParseTree(PeekableCharacterStream stream) {
		super(stream);
	}
	
	// parse the file
	public boolean parseQuery() {
		return this.isQuery();
	}
	
	// print errors
	public void printError(PrintStream ostream) {
		super.printError(ostream);
	}
	
	// print out parse tree
	public void outputParseTree(PrintStream ostream) {
		this.printNode(this.nodeStack.peek());
		ostream.println(printTree);
	}
	
	
	//
	// Overrided functions
	protected boolean isQuery() {
		node Query = new node("QUERY");
		this.nodeStack.push(Query);
		boolean ReturnVal = super.isQuery();
	
		return ReturnVal;
	}
	
	protected boolean isRule() {
		// create node
		node Rule = new node("RULE");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(Rule);
		// mark top as parent
		Rule.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(Rule);
		// call the super method
		boolean ReturnVal = super.isRule();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}
		
		return ReturnVal;
	}
	
	protected boolean isRuleHead() {
		node RuleHead = new node("RULEHEAD");
		this.nodeStack.peek().children.add(RuleHead);
		RuleHead.parent = this.nodeStack.peek();
		this.nodeStack.push(RuleHead);
		boolean ReturnVal = super.isRuleHead();
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}
		
		return ReturnVal;
	}
	
	protected boolean isRuleBody() {
		node RuleBody = new node("RULE_BODY");
		this.nodeStack.peek().children.add(RuleBody);
		RuleBody.parent = this.nodeStack.peek();
		this.nodeStack.push(RuleBody);
		boolean ReturnVal = super.isRuleBody();
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}
		
		return ReturnVal;
	}
	
	protected boolean isRuleName() {
		node RuleName = new node("RULE_NAME");
		this.nodeStack.peek().children.add(RuleName);
		RuleName.parent = this.nodeStack.peek();
		this.nodeStack.push(RuleName);
		boolean ReturnVal = super.isRuleName();
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}
		
		return ReturnVal;
	}
	
	protected boolean isHeadVariableList() {
		// create node
		node HeadVariableList = new node("HEAD_VARIABLE_LIST");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(HeadVariableList);
		// mark top as parent
		HeadVariableList.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(HeadVariableList);
		// call the super method
		boolean ReturnVal = super.isHeadVariableList();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isSubGoal() {
		// create node
		node SubGoal = new node("SUB_GOAL");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(SubGoal);
		// mark top as parent
		SubGoal.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(SubGoal);
		// call the super method
		boolean ReturnVal = super.isSubGoal();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}
	
		
		return ReturnVal;
	}
	
	protected boolean isRuleInvocation() {
		// create node
		node RuleInvocation = new node("RULE_INVOCATION");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(RuleInvocation);
		// mark top as parent
		RuleInvocation.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(RuleInvocation);
		// call the super method
		boolean ReturnVal = super.isRuleInvocation();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isNegatedRuleInvocation() {
		// create node
		node NegatedRuleInvocation = new node("NEGATED_RULE_INVOCATION");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(NegatedRuleInvocation);
		// mark top as parent
		NegatedRuleInvocation.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(NegatedRuleInvocation);
		// call the super method
		boolean ReturnVal = super.isNegatedRuleInvocation();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isEqualityRelation() {
		// create node
		node EqualityRelation = new node("EQUALITY_RELATION");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(EqualityRelation);
		// mark top as parent
		EqualityRelation.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(EqualityRelation);
		// call the super method
		boolean ReturnVal = super.isEqualityRelation();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isBodyVariableList() {
		// create node
		node BodyVariableList = new node("BODY_VARIABLE_LIST");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(BodyVariableList);
		// mark top as parent
		BodyVariableList.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(BodyVariableList);
		// call the super method
		boolean ReturnVal = super.isBodyVariableList();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isInequalityRelation() {
		// create node
		node InequalityRelation = new node("INEQUALITY_RELATION");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(InequalityRelation);
		// mark top as parent
		InequalityRelation.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(InequalityRelation);
		// call the super method
		boolean ReturnVal = super.isInequalityRelation();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isEQOperator() {
		// create node
		node EQOperator = new node("EQ_OPERATOR");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(EQOperator);
		// mark top as parent
		EQOperator.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(EQOperator);
		// call the super method
		boolean ReturnVal = super.isEQOperator();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isInvocationVariable() {
		// create node
		node InvocationVariable = new node("INVOCATION_VARIABLE");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(InvocationVariable);
		// mark top as parent
		InvocationVariable.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(InvocationVariable);
		// call the super method
		boolean ReturnVal = super.isInvocationVariable();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isTerm() {
		// create node
		node Term = new node("TERM");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(Term);
		// mark top as parent
		Term.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(Term);
		// call the super method
		boolean ReturnVal = super.isTerm();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isIEQOperator() {
		// create node
		node IEQOperator = new node("IEQ_OPERATOR");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(IEQOperator);
		// mark top as parent
		IEQOperator.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(IEQOperator);
		// call the super method
		boolean ReturnVal = super.isIEQOperator();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isSimpleTerm() {
		// create node
		node SimpleTerm = new node("SIMPLE_TERM");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(SimpleTerm);
		// mark top as parent
		SimpleTerm.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(SimpleTerm);
		// call the super method
		boolean ReturnVal = super.isSimpleTerm();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isAddOperator() {
		// create node
		node AddOperator = new node("ADD_OPERATOR");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(AddOperator);
		// mark top as parent
		AddOperator.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(AddOperator);
		// call the super method
		boolean ReturnVal = super.isAddOperator();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isUnaryExpression() {
		// create node
		node UnaryExpression = new node("UNARY_EXPRESSION");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(UnaryExpression);
		// mark top as parent
		UnaryExpression.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(UnaryExpression);
		// call the super method
		boolean ReturnVal = super.isUnaryExpression();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isMultOperator() {
		// create node
		node MultOperator = new node("MULT_OPERATOR");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(MultOperator);
		// mark top as parent
		MultOperator.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(MultOperator);
		// call the super method
		boolean ReturnVal = super.isMultOperator();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isUnaryOperator() {
		// create node
		node UnaryOperator = new node("UNARY_OPERATOR");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(UnaryOperator);
		// mark top as parent
		UnaryOperator.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(UnaryOperator);
		// call the super method
		boolean ReturnVal = super.isUnaryOperator();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isPrimaryExpression() {
		// create node
		node PrimaryExpression = new node("PRIMARY_EXPRESSION");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(PrimaryExpression);
		// mark top as parent
		PrimaryExpression.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(PrimaryExpression);
		// call the super method
		boolean ReturnVal = super.isPrimaryExpression();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected boolean isConstant() {
		// create node
		node Constant = new node("CONSTANT");
		// add to as child of top of stack
		this.nodeStack.peek().children.add(Constant);
		// mark top as parent
		Constant.parent = this.nodeStack.peek();
		// push node to the top of the stack
		this.nodeStack.push(Constant);
		// call the super method
		boolean ReturnVal = super.isConstant();
		// pop the node from the stack after super method has called
		this.nodeStack.pop();
		// remove the node from it's parent childlist if the super rule failed
		if (!ReturnVal) {
			this.nodeStack.peek().children.remove(this.nodeStack.peek().children.size()-1);
		}

		
		return ReturnVal;
	}
	
	protected void consumeNextToken() {
		// if consume a token, create a token node and add it to current parent node at the top of the stack
		node tokenNode = new node("TOKEN_NODE");
		tokenNode.tokenList.add(super.tokenBuf.get(0));
		this.nodeStack.peek().children.add(tokenNode);
		super.consumeNextToken();
	}
	
	// helper functions
	// indent the print tree
	protected void indentHelper() {
		for (int i = 0; i < this.depthCounter; i++) {
			this.printTree += "    ";
		}
	}
	
	protected void printNode(node currNode) {
		this.indentHelper();
		// if this is a token node
		if (currNode.ruleType.equals("TOKEN_NODE")) {
			this.printTree += currNode.tokenList.get(0).getText() + "\n";
			return;
		}
		
		this.printTree += "(" + currNode.ruleType + "\n";
		this.depthCounter++;
		for (int i = 0; i < currNode.children.size(); i++) {
			printNode(currNode.children.get(i));
		}
		this.depthCounter--;
		this.indentHelper();
		this.printTree += ")\n";
		return;
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		// No Input file provided
		if (args.length == 0) {
			return;
		}
		PeekableCharacterStream PCFS = new PCFileStream(args[0]);
		NRDatalogParseTree myParserTree = new NRDatalogParseTree(PCFS);
		myParserTree.isQuery();
		PrintStream ps = new PrintStream(System.out);
		myParserTree.outputParseTree(ps);
	}
	
}
