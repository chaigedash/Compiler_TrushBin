package LLVM;

import Utils.ArrayHandler;
import Utils.IO;

import java.util.ArrayList;

public class Global extends Pointer{
    public ArrayList<Integer> value;
    public boolean isConst;
    public Integer dimension1, dimension2;
    public Global(String ident, ArrayList<Integer> value, Integer dimension1, Integer dimension2, boolean isConst) {
        super(ident, Type._i32);
        this.value = value;
        this.isConst = isConst;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }
    public Global(String ident, Integer dimension1, Integer dimension2, boolean isConst) {
        super(ident, Type._i32);
        this.isConst = isConst;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }
    public void print(){
//        @b = dso_local global i32 5
        String output = "";
        String type = ArrayHandler.getInstance().getType(Type._i32, dimension1, dimension2);
        String init = ArrayHandler.getInstance().getInit(Type._i32, value, dimension1, dimension2);
        String isConst = "global";
        if (this.isConst) {
            isConst = "constant";
        }
        output = super.getIdent() + " = dso_local " + isConst + " " + type + " " + init;
        // DONE : 需要大改，至少二维数组现在是一维形式存在
        IO.getIO().writelnToLLVM(output);
    }
}
