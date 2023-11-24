package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class LVal {
    private Word ident;
    private ArrayList<Word> LBRACKs;
    private ArrayList<Word> RBRACKs;
    private ArrayList<Exp> exps = new ArrayList<Exp>();
    public LVal(Word ident, ArrayList<Word> LBRACKs, ArrayList<Exp> exps, ArrayList<Word> RBRACKs) {
        this.ident = ident;
        this.LBRACKs = LBRACKs;
        this.exps = exps;
        this.RBRACKs = RBRACKs;
    }
    public void print () {
        IO.getIO().writeln(ident.print());
        for (int i = 0; i < exps.size(); i++) {
            IO.getIO().writeln(LBRACKs.get(i).print());
            exps.get(i).print();
            IO.getIO().writeln(RBRACKs.get(i).print());
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<LVal>");
    }
    public Word getContent () {
        return ident;
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

    public ArrayList<Exp> getExps() {
        return exps;
    }
}
