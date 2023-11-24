package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class Block {
    private Word LBRACE;
    private ArrayList<BlockItem> blockItems = new ArrayList<BlockItem>();
    private Word RBRACE;
    public Block (Word LBRACE, ArrayList<BlockItem> blockItems, Word RBRACE) {
        this.LBRACE = LBRACE;
        this.blockItems = blockItems;
        this.RBRACE = RBRACE;
    }
    public void print () {
        IO.getIO().writeln(LBRACE.print());
        for (BlockItem item : blockItems) {
            item.print();
        }
        IO.getIO().writeln(RBRACE.print());

        if (Config.getInstance().Parser)
            IO.getIO().writeln("<Block>");
    }

    public Word getLBRACE() {
        return LBRACE;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    public Word getRBRACE() {
        return RBRACE;
    }
}
