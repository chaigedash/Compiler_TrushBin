package SymbolTable_v2;

import LLVM.Pointer;
import LLVM.Value;
import Lexer.LexType;

import java.util.ArrayList;

public class Symbol_v2 {
    public enum Type {
        function, val, array, param, constVar
    }
    public String ident;
    public Type symbolType;
    public int dimension;
    public Value.Type returnType_value;
    public Lexer.LexType returnType_lexer;
    public Integer value;
    private Pointer pointer;
    public boolean isGlobal;
    public ArrayList<Symbol_v2> params = new ArrayList<Symbol_v2>();
    public Symbol_v2(String ident, Type type) { // variable | constVar
        this.ident = ident;
        this.symbolType = type;
        this.isGlobal = false;
        this.dimension = 0;
    }
    public Symbol_v2(String ident, Type type, int dimension) { // array
        this.ident = ident;
        this.symbolType = type;
        this.isGlobal = false;
        this.dimension = dimension;
    }
    public Symbol_v2(String ident, Type type, Value.Type returnType) { // function | fParam
        this.ident = ident;
        this.symbolType = type;
        this.returnType_value = returnType;
        this.isGlobal = false;
    }
    public Symbol_v2(String ident, Type type, LexType returnType) { // function | fParam
        this.ident = ident;
        this.symbolType = type;
        this.returnType_lexer = returnType;
        this.isGlobal = false;
    }
    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }
    public Pointer getPointer() {
        return this.pointer;
    }
    public void setGlobal () {
        this.isGlobal = true;
    }
    public void addParam (Symbol_v2 param) {
        this.params.add(param);
    }
}
