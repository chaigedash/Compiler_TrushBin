package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class AddExp {
    private MulExp mulExp;
    private AddExp addExp;
    private Word operator;
    public AddExp(MulExp mulExp) {
        this.mulExp = mulExp;
    }
    public AddExp(AddExp addExp, Word operator, MulExp mulExp) {
        this.addExp = addExp;
        this.operator = operator;
        this.mulExp = mulExp;
    }
    public MulExp getMulExp () {
        return this.mulExp;
    }
    public void print () {
        mulExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<AddExp>");
        if (operator != null) {
            IO.getIO().writeln(operator.print());
            addExp.print();
        }
        else {
//            mulExp.print();
        }
    }
    public Word getContent () {
        Word MulExp = mulExp.getContent();
//        if (addExp != null) {
//            Word AddExp = addExp.getContent();
//        }
        return MulExp;
    }
    public boolean checkContent () {
        if (operator != null) {
            return true;
        }
        else return false;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public Word getOperator() {
        return operator;
    }
}
