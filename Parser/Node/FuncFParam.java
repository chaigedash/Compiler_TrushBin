package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class FuncFParam {
    private int dimension;
    private BType bType;
    private Word ident;
    private Word LBRACK1;
    private Word RBRACK1;
    private Word LBRACK2;
    private Word RBRACK2;
    private ConstExp constExp;
    public FuncFParam(int dimension, BType bType, Word ident, Word LBRACK1, Word RBRACK1, Word LBRACK2, Word RBRACK2, ConstExp constExp) {
        this.dimension = dimension;
        this.bType = bType;
        this.ident = ident;
        this.LBRACK1 = LBRACK1;
        this.RBRACK1 = RBRACK1;
        this.LBRACK2 = LBRACK2;
        this.RBRACK2 = RBRACK2;
        this.constExp = constExp;
    }
    public void print () {
        bType.print();
        IO.getIO().writeln(ident.print());
        switch (dimension) {
            case 0:
                break;
            case 1:
                IO.getIO().writeln(LBRACK1.print());
                IO.getIO().writeln(RBRACK1.print());
                break;
            case 2:
                IO.getIO().writeln(LBRACK1.print());
                IO.getIO().writeln(RBRACK1.print());
                IO.getIO().writeln(LBRACK2.print());
                constExp.print();
                IO.getIO().writeln(RBRACK2.print());
                break;
            default:
                break;
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<FuncFParam>");
    }

    public int getDimension() {
        return dimension;
    }

    public BType getbType() {
        return bType;
    }

    public Word getIdent() {
        return ident;
    }

    public Word getLBRACK1() {
        return LBRACK1;
    }

    public Word getRBRACK1() {
        return RBRACK1;
    }

    public Word getLBRACK2() {
        return LBRACK2;
    }

    public Word getRBRACK2() {
        return RBRACK2;
    }

    public ConstExp getConstExp() {
        return constExp;
    }
}
