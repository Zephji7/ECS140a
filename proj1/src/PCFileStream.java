import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PCFileStream implements PeekableCharacterStream {

	//data members
	FileInputStream fileStream;
	//buffer which stores the read from the filestream
	ArrayList<Integer> peekbuffer;
	int linenum = 1;
	int charpos = 0;
	
	//Construct a filestream
	public PCFileStream(String args) throws FileNotFoundException {
		this.fileStream = new FileInputStream(args);
		this.peekbuffer = new ArrayList<>();
	}
	
	public boolean moreAvailable() {
		try {
			//see if more read operation is possible
			if (this.fileStream.available() != 0) {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	
	public int peekNextChar() {
		//when the peekbuffer is empty, read 1 char from filestream
		if (this.peekbuffer.size() == 0) {
			try {
				this.peekbuffer.add(this.fileStream.read());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.peekbuffer.get(0).intValue();
	}


	public int peekAheadChar(int ahead) {
		//when peek ahead exceed the size of current peekbuffer
		if (ahead > (this.peekbuffer.size()-1)) {
			//read the fileiputstream into the peekbuffer until the ahead index
			while (ahead != (this.peekbuffer.size()-1)) {
				try {
					this.peekbuffer.add(this.fileStream.read());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return this.peekbuffer.get(ahead).intValue();
	}

	
	public int getNextChar() {
		//when the peekbuffer is empty
		if (this.peekbuffer.size() == 0) {
			//read the next character into the peekbuffer
			try {
				this.peekbuffer.add(this.fileStream.read());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// save the next character to nexchar
		int nexchar = this.peekbuffer.get(0).intValue();
		// remove next character from the peekbuffer
		this.peekbuffer.remove(0);
		// increment character position
		this.charpos++;
		if (nexchar == 10) {
			this.linenum++;
			this.charpos = 0;
		}
		
		return nexchar;
	}

	//close the fileinputstream
	public void close() {
		try {
			this.fileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public int getLinenum() {
		return this.linenum;
	}
	
	public int getCharpos() {
		return this.charpos;
	}
	

	//testing main
	public static void main(String[] args) throws IOException {
		PCFileStream PCFS = new PCFileStream(args[0]);
		int data = PCFS.peekNextChar();
		while (data != -1) {
			data = PCFS.getNextChar();
			System.out.println((char)data + " " + PCFS.linenum + " " + PCFS.charpos);
		}
		
		
	}

}
