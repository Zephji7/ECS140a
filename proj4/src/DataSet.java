import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DataSet {
	
	Scanner tokenStream;
	
	HashSet<ArrayList<Object>> dataSet = new HashSet<>();
	ArrayList<String> varName = new ArrayList<>();
	HashMap<String, Integer> nameToIndex = new HashMap<>();

	public DataSet(PeekableCharacterStream stream) {
		List<String> kylist = Arrays.asList();
		this.tokenStream = new Scanner(stream, kylist);
		
		this.getHeader();
		this.getAllData();
	}
	
	public DataSet(ArrayList<String> columnName, HashSet<ArrayList<Object>> newDataSet) {
		this.varName = columnName;
		this.dataSet = newDataSet;
		
		this.mapNameToIndex();
	}
	
	public interface dataFilter {
		public boolean keepOrNot(ArrayList<Object> theData);
	}
	
	// Data Preparation
	//
	// get the header
	private void getHeader() {
		Token tmp = this.tokenStream.peekNextToken();
		while (!((tmp.getText().equals("\n")) || (tmp.getType() == Token.TokenType.NONE))) {
			tmp = this.tokenStream.getNextToken();
			if (tmp.getType() == Token.TokenType.OPERATOR) {
				continue;
			}
			else if (tmp.getType() == Token.TokenType.STRING_CONSTANT) {
				this.varName.add(tmp.getText());
				this.nameToIndex.put(tmp.getText(), this.varName.size()-1);
			}
			else {
				System.out.println("Unexpected Data Type \"" + tmp.getText() + "\" of type " + 
						tmp.getType() + " on line " + tmp.getLineNumber() + " @ position " + tmp.getCharPosition() + ".");
				System.exit(1);
			}
		}
		
	}
	
	private void mapNameToIndex() {
		for (int i = 0; i < this.varName.size(); i++) {
			this.nameToIndex.put(this.varName.get(i), i);
		}
	}
	
	// get a line of data
	private void getNextData() {
		ArrayList<Object> currData = new ArrayList<>();
		Token tmp = this.tokenStream.peekNextToken();
		while (!((tmp.getText().equals("\n")) || (tmp.getType() == Token.TokenType.NONE))) {
			tmp = this.tokenStream.getNextToken();
			if (tmp.getType() == Token.TokenType.OPERATOR) {
				continue;
			}
			
			// String type
			else if (tmp.getType() == Token.TokenType.STRING_CONSTANT) {
				String stringType = new String(tmp.getText());
				currData.add(stringType);
			}
			
			// Float type
			else if (tmp.getType() == Token.TokenType.FLOAT_CONSTANT) {
				Float floatType = Float.valueOf(tmp.getText());
				currData.add(floatType);
			}			
			
			// Integer type
			else if (tmp.getType() == Token.TokenType.INT_CONSTANT) {
				Integer intType = Integer.valueOf(tmp.getText());
				currData.add(intType);
			}
			
			// boolean type
			else if (tmp.getType() == Token.TokenType.IDENTIFIER) {
				if (tmp.getText().equals("true") || tmp.getText().equals("false")) {
					Boolean booType = Boolean.valueOf(tmp.getText());
					currData.add(booType);
				}
			}
			
			else if (tmp.getType() == Token.TokenType.NONE) {
				return;
			}
			
			else {
				System.out.println("Unexpected Data Type \"" + tmp.getText() + "\" of type " + 
					tmp.getType() + " on line " + tmp.getLineNumber() + " @ position " + tmp.getCharPosition() + ".");
			System.exit(1);
			}
			
			// tmp = this.tokenStream.peekNextToken();
			
		}

		
		this.dataSet.add(currData);
		
	}

	// get all the data from a csv file
	private void getAllData() {
		Token tmp = this.tokenStream.peekNextToken();
		while (tmp.getType() != Token.TokenType.NONE) {
			getNextData();
			tmp = this.tokenStream.peekNextToken();
		}
		
	}
	
	// print the csv data
	public void printData() {
		for (int i = 0; i < this.varName.size(); i++) {
			System.out.print(this.varName.get(i) + "    ");
		}
		System.out.print("\n");
		for (ArrayList<Object> obj : this.dataSet) {
			for (int i = 0; i < obj.size(); i++) {
				System.out.print(obj.get(i).toString() + "      ");
			}
			System.out.print("\n");
		}
	}
	
	
	// Operation features
	//
	// a). Appending new data
	private void appendData(ArrayList<Object> newData) {
		this.dataSet.add(newData);
	}
	
	// b). Filtering data
	public DataSet filterData(dataFilter filter) {
		HashSet<ArrayList<Object>> newData = this.dataSet; 
		for (ArrayList<Object> obj : newData) {
			if (!filter.keepOrNot(obj)) {
				newData.remove(obj);
			}
		}
		
		DataSet newDataSet = new DataSet(this.varName, newData);
		
		return newDataSet;
	}
	
	// c). Cutting columns
	public DataSet cutColumns(ArrayList<String> header) {
		// Copy current data set to modify
		HashSet<ArrayList<Object>> newData = new HashSet<>(this.dataSet);
		ArrayList<String> newHeader = new ArrayList<>();
		
		// Store columns that needs to cut and needs to remain
		ArrayList<Integer> columns = new ArrayList<>();
		for (int i = 0; i < header.size(); i++) {
			if (header.get(i).equals("_")) {
				columns.add(i);
			}
			else {
				newHeader.add(header.get(i));
			}
		}
		int remove_Counter = 0;
		// when there's _ entry in the header
		while (!columns.isEmpty()) {
			// remove each _ entry inside the newdata
			for (ArrayList<Object> obj : newData) {
				obj.remove((int)columns.get(0) - remove_Counter);
			}
			// pop the first position inside columns
			columns.remove(0);
			remove_Counter++;
		}
		
		// construct new DataSet
		DataSet newDataSet = new DataSet(newHeader, newData);
		
		return newDataSet;
	}
	
	// d). Cartesian Product
	public DataSet cartesianProduct(DataSet otherDataSet) {
		
		// new header and new HashSet
		ArrayList<String> newHeader = new ArrayList<>(this.varName);
		newHeader.addAll(otherDataSet.varName);
		
		HashSet<ArrayList<Object>> newData = new HashSet<>();
		
		for (ArrayList<Object> obj_curr : this.dataSet) {
			for (ArrayList<Object> obj_other : otherDataSet.dataSet) {
				ArrayList<Object> newDataLine = new ArrayList<>(obj_curr);
				newDataLine.addAll(obj_other);
				newData.add(newDataLine);
			}
		}
		
		DataSet newDataSet = new DataSet(newHeader, newData);
		
		return newDataSet;
	}
	
	// e). Natural Join
	public DataSet naturalJoin(DataSet otherDataSet) {
		// find common header
		ArrayList<String> matchVar = new ArrayList<>();
		for (String header_other : otherDataSet.varName) {
			for (String header_curr : this.varName) {
				if (header_curr.equals(header_other)) {
					matchVar.add(header_curr);
				}
			}
		}
		
		// Get the filter table
		HashMap<ArrayList<Object>, ArrayList<ArrayList<Object>>> filterTable = new HashMap<>();
		for (ArrayList<Object> obj_other : otherDataSet.dataSet) {
			for (ArrayList<Object> obj_curr : this.dataSet) {
				// match counter and list to hold the key
				int match_Counter = 0;
				ArrayList<Object> keyList = new ArrayList<>();
				for (String headerName : matchVar) {
					if (obj_curr.get(this.nameToIndex.get(headerName)).equals(obj_other.get(otherDataSet.nameToIndex.get(headerName)))) {
						// add the object to the key list
						keyList.add(obj_curr.get(this.nameToIndex.get(headerName)));
						// increment match counter
						match_Counter++;
					}
				}
				
				// if all matched
				if (match_Counter == matchVar.size()) {
					ArrayList<ArrayList<Object>> matchedList = new ArrayList<>();
					matchedList.add(obj_curr);
					matchedList.add(obj_other);
					filterTable.put(keyList, matchedList);
				}
				
			}
		}
		
		// construct new header
		ArrayList<String> newHeader = new ArrayList<>(this.varName);
		for (String other_var : otherDataSet.varName) {
			int match_Counter = 0;
			for (String match_var : matchVar) {
				if (other_var.equals(match_var)) {
					match_Counter++;
				}
			}
			
			// if the other_var doesn't match to any in matched VarName
			if (match_Counter == 0) {
				newHeader.add(other_var);
			}
			
		}
		
		// construct new Data
		HashSet<ArrayList<Object>> newData = new HashSet<>();
		for (ArrayList<Object> theKey : filterTable.keySet()) {
			ArrayList<Object> newDataLine = filterTable.get(theKey).get(0);
			for (String other_var : otherDataSet.varName) {
				int match_Counter = 0;
				for (String match_var : matchVar) {
					if (other_var.equals(match_var)) {
						match_Counter++;
					}
				}
				
				// if the other_var doesn't match to any in matched VarName
				if (match_Counter == 0) {
					 // append the object exclusive to otherdata to newdata
					 newDataLine.add(filterTable.get(theKey).get(1).get(otherDataSet.nameToIndex.get(other_var)));
				}
			}
			// add the newdataLine to the newData HashSet
			newData.add(newDataLine);
		}
		
		DataSet newDataSet = new DataSet(newHeader, newData);
		
		return newDataSet;
	}
	
	// f). Filter Negated
	public void filterNegated(DataSet otherDataSet) {
		for (ArrayList<Object> obj_other : otherDataSet.dataSet) {
			for (ArrayList<Object> obj_curr : this.dataSet) {
				// remove data appears in the other DataSet
				if (obj_curr.equals(obj_other)) {
					this.dataSet.remove(obj_curr);
				}
			}
		}
	}
	
	
	// g). Union
	public void UnionData(DataSet otherDataSet) {
		for (ArrayList<Object> obj_other : otherDataSet.dataSet) {
			this.appendData(obj_other);
		}
	}
	
	// h). Reordering
	public DataSet reOrdering(ArrayList<String> targetHeader) {
		// prepare a new empty data hashset
		HashSet<ArrayList<Object>> newData = new HashSet<>();

		for (ArrayList<Object> obj : this.dataSet) {
			ArrayList<Object> tmp = new ArrayList<>();
			for (int i = 0; i < targetHeader.size(); i++) {
				tmp.add(obj.get(this.nameToIndex.get(targetHeader.get(i))));
			}
			newData.add(tmp);
		}
		
		DataSet newDataSet = new DataSet(targetHeader, newData);
		
		return newDataSet;
	}
	
	
	
	public static void main(String args[]) throws FileNotFoundException {
		// No Input file provided
		if (args.length == 0) {
			return;
		}
		PeekableCharacterStream PCFS = new PCFileStream(args[0]);
		DataSet myDataSet = new DataSet(PCFS);
		myDataSet.printData();

	}
}
