package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class MulExp {
    private UnaryExp unaryExp;
    private MulExp mulExp;
    private Word operator;
    public MulExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }
    public MulExp(MulExp mulExp, Word operator, UnaryExp unaryExp) {
        this.mulExp = mulExp;
        this.operator = operator;
        this.unaryExp = unaryExp;
    }
    public void print () {
        unaryExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<MulExp>");
        if (operator != null) {
            IO.getIO().writeln(operator.print());
            mulExp.print();
        }
        else {
//            unaryExp.print();
        }
    }
    public Word getContent () {
        Word UnaryExp = unaryExp.getContent();
//        if (mulExp != null) {
//            Word MulExp = mulExp.getContent();
//        }
        return UnaryExp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public MulExp getMulExp() {
        return mulExp;
    }

    public Word getOperator() {
        return operator;
    }
}
