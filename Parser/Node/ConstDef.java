package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class ConstDef {
    private Word ident;
    private ArrayList<Word> LBRACKs = new ArrayList<Word>();
    private ArrayList<Word> RBRACKs = new ArrayList<Word>();
    private ArrayList<ConstExp> constExps;
    private Word ASSIGN;
    private ConstInitVal constInitVal;
    public ConstDef(Word ident, ArrayList<Word> LBRACKs, ArrayList<ConstExp> constExps, ArrayList<Word> RBRACKs, Word ASSIGN, ConstInitVal constInitVal) {
        this.ident = ident;
        this.LBRACKs = LBRACKs;
        this.constExps = constExps;
        this.RBRACKs = RBRACKs;
        this.ASSIGN = ASSIGN;
        this.constInitVal = constInitVal;
    }
    public void print () {
        IO.getIO().writeln(ident.print());
        if (constExps != null) {
            for (int i = 0; i < constExps.size(); i++) {
                IO.getIO().writeln(LBRACKs.get(i).print());
                constExps.get(i).print();
                IO.getIO().writeln(RBRACKs.get(i).print());
            }
        }
        IO.getIO().writeln(ASSIGN.print());
        constInitVal.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<ConstDef>");
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

    public ConstInitVal getConstInitVal() {
        return constInitVal;
    }
}
