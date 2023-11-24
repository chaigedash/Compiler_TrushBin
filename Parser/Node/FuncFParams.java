package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class FuncFParams {
    private ArrayList<FuncFParam> funcFParams = new ArrayList<FuncFParam>();
    private ArrayList<Word> COMMAs = new ArrayList<Word>();
    public FuncFParams(ArrayList<FuncFParam> funcFParams, ArrayList<Word> COMMAs) {
        this.funcFParams = funcFParams;
        this.COMMAs = COMMAs;
    }
    public void print () {
        for (int i = 0; i < funcFParams.size(); i++) {
            if (i > 0 && i <= COMMAs.size()) {
                IO.getIO().writeln(COMMAs.get(i - 1).print());
            }
            funcFParams.get(i).print();
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<FuncFParams>");
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }

    public ArrayList<Word> getCOMMAs() {
        return COMMAs;
    }
}
