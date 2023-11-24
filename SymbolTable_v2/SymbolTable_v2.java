package SymbolTable_v2;

import java.util.ArrayList;

public class SymbolTable_v2 {
    private SymbolTable_v2 preTable;
    private ArrayList<Symbol_v2> symbols = new ArrayList<Symbol_v2>();
    private ArrayList<SymbolTable_v2> childTables = new ArrayList<SymbolTable_v2>();
    public SymbolTable_v2() {}
    public SymbolTable_v2(SymbolTable_v2 preTable) {
        this.preTable = preTable;
    }
    public SymbolTable_v2 createChildTable() {
        SymbolTable_v2 childTable = new SymbolTable_v2(this);
        this.childTables.add(childTable);
        return childTable;
    }
    public SymbolTable_v2 getPreTable() {
        if (preTable == null) {
            return null;
        }
        return preTable;
    }
    public void addSymbol(Symbol_v2 symbol) {
        this.symbols.add(symbol);
    }
    public Symbol_v2 searchSymbol(String aim) {
        for (Symbol_v2 symbol : symbols) {
            if (symbol.ident.equals(aim)) {
                return symbol;
            }
        }
        if (preTable == null) {
            return null;
        }
        return preTable.searchSymbol(aim);
    }
    public Symbol_v2 searchSymbol_curTable(String aim) {
        for (Symbol_v2 symbol : symbols) {
            if (symbol.ident.equals(aim)) {
                return symbol;
            }
        }
        return null;
    }
    public ArrayList<Symbol_v2> getSymbols () {
        return symbols;
    }
}
