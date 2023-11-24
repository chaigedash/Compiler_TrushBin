package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class LOrExp {
    private LAndExp lAndExp;
    private LOrExp lOrExp;
    private Word operator;
    public LOrExp(LAndExp lAndExp) {
        this.lAndExp = lAndExp;
    }
    public LOrExp(LOrExp lOrExp, Word operator, LAndExp lAndExp) {
        this.lOrExp = lOrExp;
        this.operator = operator;
        this.lAndExp = lAndExp;
    }
    public void print () {
        lAndExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<LOrExp>");
        if (operator != null) {
            IO.getIO().writeln(operator.print());;
            lOrExp.print();
        }
        else {
//            lAndExp.print();
        }
    }

    public LAndExp getlAndExp() {
        return lAndExp;
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }

    public Word getOperator() {
        return operator;
    }
}
