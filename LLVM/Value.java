package LLVM;

import java.util.ArrayList;

public class Value {
    public enum Type {
        Function, Label, Pointer, Void, Array, Instruction,
        _i32, _i8, _i1, _void, _basicBlock;
        public String toString() {
            switch (this) {
                case _i32 -> {
                    return "i32";
                }
                case _i8 -> {
                    return "i8";
                }
                case _i1 -> {
                    return "i1";
                }
                case _void -> {
                    return "void";
                }
                default -> {
                    return "";
                }
            }
        }
    }
    private String ident; // 包含了 % 或 @
    private Type type;
    private ArrayList<Use> useList = new ArrayList<Use>();
    public Value(String ident, Type type){
        this.ident = ident;
        this.type = type;
    }
    public String getType(){
        return type.toString();
    }
    public String getIdent(){
        return ident;
    }
}
