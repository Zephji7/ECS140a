import java.util.ArrayList;
import java.util.Stack;


public class NRDatalogExecutionTree extends NRDatalogParseTree {
	
	// data members
	Stack<exenode> exenodeStack = new Stack<exenode>();
	String printTree = "";
	int depthCounter = 0;
	
	// node class
	class exenode {
		String ruleType;
		Token nodeToken;
		node parent;
		ArrayList<Object> children;
		
		public exenode(String rtype) {
			this.ruleType = rtype;
			this.children = new ArrayList<>();
		}
	}
	
	
	// Constructor for the execution tree
	public NRDatalogExecutionTree(PeekableCharacterStream stream) {
		super(stream);
	}
	
	
	// Parses a query and returns true if valid query
	public boolean parseQuery() {
		super.parseQuery();
		this.exeTreeBuilder(super.nodeStack.get(0));
	}
	// Prints out the error if one has occurred
	public void printError(PrintStream ostream);
	// Outputs a tree of how execution would occur for queries
	public void outputExecutionTree(PrintStream ostream);
	// Sets the verbosity setting for execution
	public void setVerbose(boolean verb);
	// Sets the number of threads during execution
	public void setThreadCount(int threadcount);
	// Sets the path to the data files
	public void setDataPath(String datapath);
	// Executes the parsed query
	public boolean executeQuery();
	
	private void exeTreeBuilder(node currNode) {
		if (currNode.ruleType.equals("QUERY") || currNode.ruleType.equals("RULE") || currNode.ruleType.equals("RULE_HEAD") || currNode.ruleType.equals("RULE_INVOCATION") || currNode.ruleType.equals("EQUALITY_RELATION") || currNode.ruleType.equals("TERM")) {
			exenode parenNode = new exenode(currNode.ruleType);
			this.exenodeStack.push(parenNode);
		}
		
		if (currNode.ruleType.equals("TOKEN_NODE")) {
			// if parent is rule name
			if (currNode.parent.ruleType.equals("RULE_NAME")) {
				this.exenodeStack.peek().nodeToken = currNode.tokenList.get(0);
			}
			
			// if parent is invocation_variable
			if (currNode.parent.ruleType.equals("INVOCATION_VARIABLE")) {
				this.exenodeStack.peek().children.add(currNode);
			}
			
			// if parent is eq/ieq operator
			if (currNode.parent.ruleType.equals("EQ_OPERATOR") || currNode.parent.ruleType.equals("IEQ_OPERATOR")) {
				if (this.exenodeStack.peek().ruleType.equals("TERM")) {
					if (this.exenodeStack.peek().children.get(0) instanceof node) {
						node tmpnode = (node)this.exenodeStack.peek().children.get(0);
					} else {
						exenode tmpnode = (exenode)this.exenodeStack.peek().children.get(0);
					}
					
					this.exenodeStack.pop();
					this.exenodeStack.peek().children.add(tmpnode);
					
				}
				this.exenodeStack.peek().nodeToken = currNode.tokenList.get(0);
			}
			// if parent is primary_expression
			if (currNode.parent.ruleType.equals("INVOCATION_VARIABLE")) {
				this.exenodeStack.peek().children.add(currNode);
			}
			
		}
		
		for (int i = 0; i < currNode.children.size(); i++) {
			
		}
		
		
		
	}
	
}
