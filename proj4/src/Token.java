
public class Token{
    public enum TokenType{
        NONE(0), OPERATOR(1), KEYWORD(2), IDENTIFIER(3), EMPTY_IDENTIFIER(4), INT_CONSTANT(5), FLOAT_CONSTANT(6), STRING_CONSTANT(7), INVALID(8);
        
        private int value;
        
        private TokenType(int val){
            this.value = val;   
        }
        
        public int getValue(){
            return value;
        }
        
        public String toString(){
            switch(value){
                case 0:   return "NONE";
                case 1:   return "OPERATOR";
                case 2:   return "KEYWORD";
                case 3:   return "IDENTIFIER";
                case 4:   return "EMPTY_IDENTIFIER";
                case 5:   return "INT_CONSTANT";
                case 6:   return "FLOAT_CONSTANT";
                case 7:   return "STRING_CONSTANT";
                case 8:   return "INVALID";
                default:  return "NONE";
            }
        }
    }
    
    private String text;
    private TokenType tokenType;
    private int lineNumber;
    private int charPosition;
    
    
    public Token(String str, TokenType type, int linenum, int charpos){
        text = str;
        tokenType = type;
        lineNumber = linenum;
        charPosition = charpos;
    }
    
    public String getText(){
        return text;
    }
    
    public TokenType getType(){
        return tokenType;
    }
    
    public int getLineNumber(){
        return lineNumber;
    }
    
    public int getCharPosition(){
        return charPosition;
    }
    
    public static void main(String[] args) {
        Token MyToken = new Token("int", Token.TokenType.KEYWORD, 2, 8);

        System.out.println("Token Text: " + MyToken.getText());
        System.out.println("Token Type: " + Integer.toString(MyToken.getType().getValue()) + " " + MyToken.getType().toString());
        System.out.println("Token Line: " + Integer.toString(MyToken.getLineNumber()));
        System.out.println("Token Char: " + Integer.toString(MyToken.getCharPosition()));
        
        
    }
}