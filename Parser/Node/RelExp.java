package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class RelExp {
    private AddExp addExp;
    private RelExp relExp;
    private Word operator;
    public RelExp(AddExp addExp) {
        this.addExp = addExp;
    }
    public RelExp(RelExp relExp, Word operator, AddExp addExp) {
        this.relExp = relExp;
        this.operator = operator;
        this.addExp = addExp;
    }
    public void print () {
        addExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<RelExp>");
        if(operator != null) {
            IO.getIO().writeln(operator.print());
            relExp.print();
        }
        else {
//            addExp.print();
        }
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public RelExp getRelExp() {
        return relExp;
    }

    public Word getOperator() {
        return operator;
    }
}
