/* 	 	  	 	 
    Copyright (c) 2020, Christopher Nitta
    All rights reserved.

    All source material has been provided to University of California, Davis 
    students of course ECS 140A for educational purposes. It may not be 
    distributed beyond those enrolled in the course without prior permission 
    from the copyright holder.
*/
public interface PeekableCharacterStream{
    public boolean moreAvailable();
    public int peekNextChar();
    public int peekAheadChar(int ahead);
    public int getNextChar();
    public void close();
    public int getLinenum();
    public int getCharpos();
}