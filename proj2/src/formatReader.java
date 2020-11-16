import java.util.Map;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class formatReader {
	// map that stores format of each element type
	private Map<String, Map<String, String>> progFormat;
	
	// construct the format map of the
	public formatReader(CSVParser csvStream) {
		this.progFormat = new HashMap<>();
		//this.csvstream = filestream;
		while (csvStream.fileStream.peekNextChar() != -1) {
			// get the format of of an element type
			Map<String, String> tmpMap = csvStream.getNextRow();
			String element_type = tmpMap.get("ELEMENT_TYPE");
			tmpMap.remove("ELEMENT_TYPE");
			this.progFormat.put(element_type, tmpMap);
		}
		
		/*// fulfill null value with DEFAULT value
		for (String i : progFormat.keySet()) {
			// skip default case
			if (i.equals("DEFAULT")) {
				continue;
			}
			for (String j : progFormat.get(i).keySet()) {
				// replace null values with default values
				progFormat.get(i).replace(j, "null", progFormat.get("DEFAULT").get(j));
			}
		}*/
	}
	
	// method that returns the formatMap
	public Map<String, Map<String, String>> getformatMap() {
		return this.progFormat;
	}
				
	
	public static void main(String args[]) throws FileNotFoundException {
		CSVParser myparser = new CSVParser(new PCFileStream(args[0]));
		formatReader myReader = new formatReader(myparser);
		
		Map<String, Map<String, String>> formatMap = myReader.getformatMap();
		
		for (String i : formatMap.keySet()) {
			System.out.print(i + " -> ");
			System.out.println(formatMap.get(i));
		}
		
	}
}
