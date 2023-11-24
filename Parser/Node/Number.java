package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class Number {
//    private int intConst;

    private Word INTCON;
    public Number(Word word) {
        this.INTCON = word;
    }
    public void print () {
        IO.getIO().writeln(INTCON.print());
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<Number>");
    }
    public Word getContent () {
        return INTCON;
    }
}
