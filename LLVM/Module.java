package LLVM;

import Utils.IO;

import java.util.ArrayList;

public class Module {
    private static final Module INSTANCE = new Module();
    public static Module getInstance() {
        return INSTANCE;
    }
    ArrayList<Global> globals = new ArrayList<Global>();
    ArrayList<Function> functions = new ArrayList<Function>();
    public void addGlobal(Global global) {
        this.globals.add(global);
    }
    public void addFunction(Function function) {
        this.functions.add(function);
    }
    public Global getGlobal(String name) {
        String ident = "@" + name;
        for (Global global : globals) {
            if (global.getIdent().equals(ident)) {
                return global;
            }
        }
        return null;
    }
    public Function getFunction(String name) {
        String ident = "@" + name;
        for (Function function : functions) {
            if (function.getIdent().equals(ident)) {
                return function;
            }
        }
        return null;
    }
    public void print() {
        for (Global global : globals) {
            global.print();
        }
        if (globals.size() > 0)
            IO.getIO().writelnToLLVM("");
        for (Function function : functions) {
            if (function.getIdent().equals("@getint") || function.getIdent().equals("@putint") || function.getIdent().equals("@putch") || function.getIdent().equals("@putstr")) {
                continue;
            }
            function.print();
        }
        if (functions.size() > 0)
            IO.getIO().writelnToLLVM("");
    }
}
