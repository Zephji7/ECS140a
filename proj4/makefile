default: NRDatalogParser.class NRDatalogParseTree.class DataSet.class

NRDatalogParser.class:
	javac -sourcepath src/ -d class/ src/NRDatalogParser.java

NRDatalogParseTree.class:
	javac -sourcepath src/ -d class/ src/NRDatalogParseTree.java

DataSet.class:
	javac -sourcepath src/ -d class/ src/DataSet.java

clean:
	rm -rf class/

NRDatalogParser:
	java -cp class/ NRDatalogParser $(args)

NRDatalogParseTree:
	java -cp class/ NRDatalogParseTree $(args)

DataSet:
	java -cp class/ DataSet $(args)
