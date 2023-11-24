package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class VarDecl {
    private BType bType;
    private ArrayList<Word> COMMAs;
    private ArrayList<VarDef> varDefs = new ArrayList<VarDef>();
    private Word SEMICN;
    public VarDecl(BType bType, ArrayList<VarDef> varDefs, ArrayList<Word> COMMAs, Word SEMICN) {
        this.bType = bType;
        this.varDefs = varDefs;
        this.COMMAs = COMMAs;
        this.SEMICN = SEMICN;
    }
    public void print () {
        bType.print();
        for (int i = 0; i < varDefs.size(); i++) {
            if (i > 0 && i <= COMMAs.size()) {
                IO.getIO().writeln(COMMAs.get(i - 1).print());
            }
            varDefs.get(i).print();
        }
        IO.getIO().writeln(SEMICN.print());
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<VarDecl>");
    }

    public BType getbType() {
        return bType;
    }

    public ArrayList<Word> getCOMMAs() {
        return COMMAs;
    }

    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }

    public Word getSEMICN() {
        return SEMICN;
    }
}
