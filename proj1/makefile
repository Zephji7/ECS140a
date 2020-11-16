default: Scanner.class CSVParser.class

Scanner.class:
	javac -sourcepath src/ -d class/ src/Scanner.java

CSVParser.class:
	javac -sourcepath src/ -d class/ src/CSVParser.java

clean:
	rm -rf class/

Scanner:
	java -cp class/ Scanner $(args)

CSVParser:
	java -cp class/ CSVParser $(args)
