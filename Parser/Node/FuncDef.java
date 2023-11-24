package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class FuncDef {
    private FuncType funcType;
    private Word ident;
    private Word LPARENT;
    private Word RPARENT;
    private FuncFParams funcFParams;
    private Block block;
    public FuncDef(FuncType funcType, Word ident, Word LPARENT, FuncFParams funcFParams, Word RPARENT, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.LPARENT = LPARENT;
        this.funcFParams = funcFParams;
        this.RPARENT = RPARENT;
        this.block = block;
    }
    public void print () {
        funcType.print();
        IO.getIO().writeln(ident.print());
        IO.getIO().writeln(LPARENT.print());
        if (funcFParams != null) {
            funcFParams.print();
        }
        IO.getIO().writeln(RPARENT.print());
        block.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<FuncDef>");
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public Word getIdent() {
        return ident;
    }

    public Word getLPARENT() {
        return LPARENT;
    }

    public Word getRPARENT() {
        return RPARENT;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }
}
