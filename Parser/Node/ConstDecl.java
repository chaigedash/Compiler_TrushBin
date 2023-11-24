package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class ConstDecl {
    private Word CONST;
    private BType bType;
    private ArrayList<Word> COMMAs = new ArrayList<Word>();
    private ArrayList<ConstDef> constDefs = new ArrayList<ConstDef>();
    private Word SEMICN;
    public ConstDecl(Word CONST, ArrayList<Word> COMMAs, BType bType, ArrayList<ConstDef> constDef, Word SEMICN) {
        this.CONST = CONST;
        this.COMMAs = COMMAs;
        this.bType = bType;
        this.constDefs = constDef;
        this.SEMICN = SEMICN;
    }
    public BType getBType() {
        return this.bType;
    }
    public ArrayList<ConstDef> getConstDefs() {
        return this.constDefs;
    }
    public void print() {
        IO.getIO().writeln(CONST.print());
        bType.print();
        for (int i = 0; i < constDefs.size(); i++) {
            if (i != 0) {
                IO.getIO().writeln(COMMAs.get(i - 1).print());
            }
            constDefs.get(i).print();
        }
        IO.getIO().writeln(SEMICN.print());
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<ConstDecl>");
    }

    public Word getCONST() {
        return CONST;
    }

    public BType getbType() {
        return bType;
    }

    public ArrayList<Word> getCOMMAs() {
        return COMMAs;
    }

    public Word getSEMICN() {
        return SEMICN;
    }
}
