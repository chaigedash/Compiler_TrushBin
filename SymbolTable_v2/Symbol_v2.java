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
//    public Integer value;
    private Pointer pointer;
    public boolean isGlobal;
    public boolean isConst;
    public ArrayList<Integer> constValue = new ArrayList<Integer>();
    public ArrayList<Pointer> valuePtr = new ArrayList<Pointer>();
    public Integer dimension1, dimension2;
    public ArrayList<Symbol_v2> params = new ArrayList<Symbol_v2>();
    public Integer col_fParam;
    public Symbol_v2(String ident, Type type) { // variable | constVar | fParam
        this.ident = ident;
        this.symbolType = type;
        this.isGlobal = false;
        this.dimension = 0;
    }
    public Symbol_v2(String ident, Type type, int dimension) { // only for Parser
        this.ident = ident;
        this.symbolType = type;
        this.isGlobal = false;
        this.dimension = dimension;
    }
    public Symbol_v2(String ident, Type type, Value.Type returnType) { // function
        // returnType = null -> void
        this.ident = ident;
        this.symbolType = type;
        this.returnType_value = returnType;
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
    public void setConstArray(ArrayList<Integer> constValue, Integer dimension1, Integer dimension2) {
        this.constValue = constValue;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }
    public void setArray(ArrayList<Pointer> value, Integer dimension1, Integer dimension2) {
        this.valuePtr = value;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }
    public Integer getConstArrayValue(Integer x, Integer y) {
        int col = dimension2 == null ? 0 : dimension2;
        int index = x * col + y;
        if (constValue == null) {
            return 0;
        }
        else {
            if (index >= constValue.size()) {
                System.out.println("fuck，常量或者全局数组取值超范围了，什么鬼东西, index = " + index + " valueSize = " +constValue.size());
            }
            Integer res = constValue.get(index);
            if (res == null) {
                System.out.println("Error: out of bound, so return 0");
                res = 0;
            }
            return res;
        }
    }
    public Pointer getArrayValue(Integer x, Integer y) {
        int split = dimension1 == null ? 0 : dimension1;
        int index = x * split + y;
        if (valuePtr == null) {
            System.out.println("a?");
            return null;
        }
        else {
            System.out.println("valuePtr = " + valuePtr.size() + ", index: " + index);
            Pointer res = valuePtr.get(index);
            if (res == null) {
                System.out.println("Error: out of bound, so return 0");
                res = null;
            }
            return res;
        }
    }
}
