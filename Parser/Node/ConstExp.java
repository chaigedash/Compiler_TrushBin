package Parser.Node;

import Utils.Config;
import Utils.IO;

public class ConstExp {
    private AddExp addExp;
    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }
    public void print() {
        addExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<ConstExp>");
    }

    public AddExp getAddExp() {
        return addExp;
    }
}
