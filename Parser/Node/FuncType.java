package Parser.Node;

import Lexer.LexType;
import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class FuncType {
    private Word funcType;
    public FuncType(Word funcType) {
        this.funcType = funcType;
    }
    public LexType getFunctionType() {
        return funcType.lexType;
    }
    public void print () {
        IO.getIO().writeln(funcType.print());
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<FuncType>");
    }

    public Word getFuncType() {
        return funcType;
    }
}
