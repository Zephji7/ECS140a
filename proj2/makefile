default: XParser.class XFormatter.class

XParser.class:
	javac -sourcepath src/ -d class/ src/XParser.java

XFormatter.class:
	javac -sourcepath src/ -d class/ src/XFormatter.java

clean:
	rm -rf class/

XParser:
	java -cp class/ XParser $(args)

XFormatter:
	java -cp class/ XFormatter $(args)
