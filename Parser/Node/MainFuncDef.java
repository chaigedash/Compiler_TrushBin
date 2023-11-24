package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class MainFuncDef {
    private Word INT;
    private Word MAIN;
    private Word LPARENT;
    private Word RPARENT;

    private Block block;
    public MainFuncDef(Word INT, Word MAIN, Word LPARENT, Word RPARENT, Block block) {
        this.INT = INT;
        this.MAIN = MAIN;
        this.LPARENT = LPARENT;
        this.RPARENT = RPARENT;
        this.block = block;
    }
    public void print () {
        IO.getIO().writeln(INT.print());
        IO.getIO().writeln(MAIN.print());
        IO.getIO().writeln(LPARENT.print());
        IO.getIO().writeln(RPARENT.print());
        block.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<MainFuncDef>");
    }

    public Word getINT() {
        return INT;
    }

    public Word getMAIN() {
        return MAIN;
    }

    public Word getLPARENT() {
        return LPARENT;
    }

    public Word getRPARENT() {
        return RPARENT;
    }

    public Block getBlock() {
        return block;
    }
}
