package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class VarDef {
    private int dimension;
    private Word ident;
    private ArrayList<Word> LBRACKs;
    private ArrayList<Word> RBRACKs;
    private ArrayList<ConstExp> constExps = new ArrayList<ConstExp>();
    private Word ASSIGN;
    private InitVal initVal;
    public VarDef(Word ident, ArrayList<Word> LBRACKs, ArrayList<Word> RBRACKs, ArrayList<ConstExp> constExps, Word EQL, InitVal initVal) {
        this.ident = ident;
        this.LBRACKs = LBRACKs;
        this.RBRACKs = RBRACKs;
        this.constExps = constExps;
        this.ASSIGN = EQL;
        this.initVal = initVal;
    }
//    public void setIdent(Word ident) {
//        this.ident = ident;
//    }
//    public void addConstExp(ConstExp constExp) {
//        this.constExps.add(constExp);
//    }
//    public void setInitVal(InitVal initVal) {
//        this.initVal = initVal;
//    }
    public void print () {
        IO.getIO().writeln(ident.print());
        for (int i = 0; i < constExps.size(); i++) {
            IO.getIO().writeln(LBRACKs.get(i).print());
            constExps.get(i).print();
            IO.getIO().writeln(RBRACKs.get(i).print());
        }
        if (ASSIGN != null) {
            IO.getIO().writeln(ASSIGN.print());
            initVal.print();
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<VarDef>");
    }

    public int getDimension() {
        return dimension;
    }

    public Word getIdent() {
        return ident;
    }

    public ArrayList<Word> getLBRACKs() {
        return LBRACKs;
    }

    public ArrayList<Word> getRBRACKs() {
        return RBRACKs;
    }

    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }

    public Word getASSIGN() {
        return ASSIGN;
    }

    public InitVal getInitVal() {
        return initVal;
    }
}
