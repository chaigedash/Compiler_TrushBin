package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class InitVal {
    private int dimension;
    private Exp exp;
    private Word LBRACE;
    private ArrayList<Word> COMMAs;
    private Word RBRACE;
    private ArrayList<InitVal> initVals;
    public InitVal(Exp exp) {
        this.exp = exp;
    }
    public InitVal(Word LBRACE, ArrayList<InitVal> initVals, ArrayList<Word> COMMAs, Word RBRACE) {
        this.LBRACE = LBRACE;
        this.initVals = initVals;
        this.COMMAs = COMMAs;
        this.RBRACE = RBRACE;
    }
//    public void addInitVal(InitVal initVal) {
//        this.initVals.add(initVal);
//    }
    public void print () {
        if (exp != null) {
            exp.print();
        }
        else {
            IO.getIO().writeln(LBRACE.print());
            for (int i = 0; i < initVals.size(); i++) {
                if (i > 0 && i <= COMMAs.size()) {
                    IO.getIO().writeln(COMMAs.get(i - 1).print());
                }
                initVals.get(i).print();
            }
            IO.getIO().writeln(RBRACE.print());
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<InitVal>");
    }

    public int getDimension() {
        return dimension;
    }

    public Exp getExp() {
        return exp;
    }

    public Word getLBRACE() {
        return LBRACE;
    }

    public ArrayList<Word> getCOMMAs() {
        return COMMAs;
    }

    public Word getRBRACE() {
        return RBRACE;
    }

    public ArrayList<InitVal> getInitVals() {
        return initVals;
    }
}
