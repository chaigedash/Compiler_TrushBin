package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class ForStmt {
    private Word ASSIGM;
    private LVal lVal;
    private Exp exp;
    public ForStmt(LVal lVal, Word ASSIGM, Exp exp) {
        this.lVal = lVal;
        this.ASSIGM = ASSIGM;
        this.exp = exp;
    }
    public void print () {
        lVal.print();
        IO.getIO().writeln(ASSIGM.print());
        exp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<ForStmt>");
    }

    public Word getASSIGM() {
        return ASSIGM;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }
}
