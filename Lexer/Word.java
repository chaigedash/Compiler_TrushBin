package Lexer;

public class Word {
    public LexType lexType;
    public String word;
    public int number;
    public int lineNum;
    public boolean print;
    public boolean isReal;
    public Word(LexType lt, String word, int ln, boolean p){
        this.lexType = lt;
        this.word = word;
        this.lineNum = ln;
        this.print = p;
        this.isReal = true;
    }
    public Word(LexType lt, int number, int ln, boolean p){
        this.lexType = lt;
        this.number = number;
        this.lineNum = ln;
        this.print = p;
        this.isReal = true;
    }
    public Word(LexType lt) {
        this.lexType = lt;
        this.isReal = false;
    }
    public String print () {
        if (isReal) {
            if (word != null) {
//                return lexType.toString() + " " + word + " line = " + lineNum;
                return lexType.toString() + " " + word;
            }
            else {
//                return lexType.toString() + " " + Integer.valueOf(number).toString() + " line = " + lineNum;
                return lexType.toString() + " " + Integer.valueOf(number).toString();
            }
        }
        return null;
    }
    public String getContent () {
        if (isReal) {
            if (word != null) {
                return word;
            }
            else {
                return Integer.valueOf(number).toString();
            }
        }
        return null;
    }
}
