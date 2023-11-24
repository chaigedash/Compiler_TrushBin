package Parser.Node;

import Utils.Config;
import Utils.IO;

public class Cond {
    private LOrExp lOrExp;
    public Cond(LOrExp lOrExp) {
        this.lOrExp = lOrExp;
    }
    public void print () {
        lOrExp.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<Cond>");
    }

    public LOrExp getlOrExp() {
        return lOrExp;
    }
}
