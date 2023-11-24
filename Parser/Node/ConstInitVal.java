package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class ConstInitVal {
    private ConstExp constExp;
    private Word LBRACE;
    private ArrayList<Word> COMMAs = new ArrayList<Word>();
    private Word RBRACE;
    private ArrayList<ConstInitVal> constInitVals = new ArrayList<ConstInitVal>();
    public ConstInitVal(ConstExp constExp) {
        this.constExp = constExp;
    }
    public ConstInitVal(Word LBRACE, ArrayList<Word> COMMAs, Word RBRACE, ArrayList<ConstInitVal> constInitVals) {
        this.LBRACE = LBRACE;
        this.COMMAs = COMMAs;
        this.RBRACE = RBRACE;
        this.constInitVals = constInitVals;
    }
//    public void addConstInitVal(ConstInitVal constInitVal) {
//        this.constInitVals.add(constInitVal);
//    }
    public void print () {
        if (constExp != null) {
            constExp.print();
        }
        else {
            IO.getIO().writeln(LBRACE.print());
            for (int i = 0; i < constInitVals.size(); i++) {
                if(i > 0 && i <= COMMAs.size()) {
                    IO.getIO().writeln(COMMAs.get(i - 1).print());
                }
                constInitVals.get(i).print();
            }
            IO.getIO().writeln(RBRACE.print());
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<ConstInitVal>");
    }

    public ConstExp getConstExp() {
        return constExp;
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

    public ArrayList<ConstInitVal> getConstInitVals() {
        return constInitVals;
    }
}
