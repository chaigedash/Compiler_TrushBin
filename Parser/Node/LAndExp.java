package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class LAndExp {
    private EqExp eqExp;
    private LAndExp landExp;
    private Word operator;
    public LAndExp(EqExp eqExp){
        this.eqExp = eqExp;
    }
    public LAndExp(LAndExp landExp, Word operator, EqExp eqExp){
        this.landExp = landExp;
        this.operator = operator;
        this.eqExp = eqExp;
    }
    public void print () {
        eqExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<LAndExp>");
        if (operator != null) {
            IO.getIO().writeln(operator.print());
            landExp.print();
        }
        else {
//            eqExp.print();
        }
    }

    public EqExp getEqExp() {
        return eqExp;
    }

    public LAndExp getLandExp() {
        return landExp;
    }

    public Word getOperator() {
        return operator;
    }
}
