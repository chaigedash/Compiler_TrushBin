package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class UnaryOp {
    private Word operator;
    public UnaryOp(Word operator) {
        this.operator = operator;
    }
    public void print () {
        IO.getIO().writeln(operator.print());
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<UnaryOp>");
    }
    public Word getOperator () {
        return operator;
    }
}
