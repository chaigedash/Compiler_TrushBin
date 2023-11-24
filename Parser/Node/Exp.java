package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class Exp {
    private AddExp addExp;
    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }
    public void print () {
        addExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<Exp>");
    }
    public Word getContent () {
        return addExp.getContent();
    }
    public boolean checkContent () {
        return addExp.checkContent();
    }

    public AddExp getAddExp() {
        return addExp;
    }
}
