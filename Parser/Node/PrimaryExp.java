package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class PrimaryExp {
    private Word LPARENT;
    private Exp exp;
    private Word RPARENT;
    private LVal lVal;
    private Number number;
    public PrimaryExp(Word LPARENT, Exp exp, Word RPARENT) {
        this.LPARENT = LPARENT;
        this.exp = exp;
        this.RPARENT = RPARENT;
    }
    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
    }
    public PrimaryExp(Number number) {
        this.number = number;
    }
    public void print () {
        if( number != null ) {
            number.print();
        }
        else if( lVal != null ) {
            lVal.print();
        }
        else {
            IO.getIO().writeln(LPARENT.print());
            exp.print();
            IO.getIO().writeln(RPARENT.print());
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<PrimaryExp>");
    }
    public Word getContent () {
        if( lVal != null ) {
            return lVal.getContent();
        }
        else if (number != null ) {
            return number.getContent();
        }
        else {
            return exp.getContent();
        }
    }

    public Word getLPARENT() {
        return LPARENT;
    }

    public Exp getExp() {
        return exp;
    }

    public Word getRPARENT() {
        return RPARENT;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Number getNumber() {
        return number;
    }
}
