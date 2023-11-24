package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class EqExp {
    private RelExp relExp;
    private EqExp eqExp;
    private Word operator;
    public EqExp(RelExp relExp) {
        this.relExp = relExp;
    }
    public EqExp(EqExp eqExp, Word operator, RelExp relExp) {
        this.eqExp = eqExp;
        this.operator = operator;
        this.relExp = relExp;
    }
    public void print () {
        relExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<EqExp>");
        if (operator != null) {
            IO.getIO().writeln(operator.print());
            eqExp.print();
        }
        else {
//            relExp.print();
        }
    }

    public RelExp getRelExp() {
        return relExp;
    }

    public EqExp getEqExp() {
        return eqExp;
    }

    public Word getOperator() {
        return operator;
    }
}
