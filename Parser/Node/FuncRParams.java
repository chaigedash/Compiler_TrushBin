package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class FuncRParams {
    private ArrayList<Exp> exps = new ArrayList<Exp>();
    private ArrayList<Word> COMMAs = new ArrayList<Word>();
    public FuncRParams(ArrayList<Exp> exps, ArrayList<Word> COMMAs) {
        this.exps = exps;
        this.COMMAs = COMMAs;
    }
    public void print() {
        for (int i = 0; i < exps.size(); i++) {
            if(i > 0 && i <= COMMAs.size()) {
                IO.getIO().writeln(COMMAs.get(i - 1).print());
            }
            exps.get(i).print();
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<FuncRParams>");
    }
    public ArrayList<Exp> getExps() {
        return exps;
    }

    public ArrayList<Word> getCOMMAs() {
        return COMMAs;
    }
}
