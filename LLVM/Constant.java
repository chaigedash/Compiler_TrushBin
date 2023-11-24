package LLVM;

import Utils.IO;

public class Constant extends User {
    public int value;
    private String ident;
    public Constant(String ident, int value) {
        super(ident, Type._i32);
        this.value = value;
    }
    public Constant(int value) {
        super("", Type._i32);
        this.value = value;
    }
    public void print () {
//        @a = dso_local constant i32 5
        String output = super.getIdent() + " = dso_local constant i32 " + value;
        IO.getIO().writelnToLLVM(output);
    }
}
