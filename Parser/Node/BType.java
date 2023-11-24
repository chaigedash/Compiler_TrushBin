package Parser.Node;

import Lexer.Word;
import Utils.IO;

import java.util.ArrayList;

public class BType {
    private String bType;
//    private ArrayList<Word> word = new ArrayList<Word>();
    private Word word;
    public BType(String bType, Word word) {
        this.bType = bType;
        this.word = word;
    }
    public String getType() {
        return bType;
    }
    public void print() {
        IO.getIO().writeln(word.print());
//        if (Config.getInstance().Parser)
//            IO.getIO().writeln("<BType>");
    }
}
