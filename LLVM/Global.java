package LLVM;

import Utils.IO;

public class Global extends Pointer{
    public int value;
    boolean isConst;
    public Global(String ident, int value, boolean isConst) {
        super(ident, Type._i32);
        this.value = value;
        this.isConst = isConst;
    }
    public void print(){
//        @b = dso_local global i32 5
        String output = "";
        output = super.getIdent() + " = dso_local global i32 " + value;
        IO.getIO().writelnToLLVM(output);
    }
}
