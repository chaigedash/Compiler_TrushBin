package Parser.Node;

import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class CompUnit {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    public CompUnit(ArrayList<Decl> decls, ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef){
        this.decls = decls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }
    public ArrayList<Decl> getDecls() {
        return this.decls;
    }
    public ArrayList<FuncDef> getFuncDefs() {
        return this.funcDefs;
    }
    public MainFuncDef getMainFuncDef() {
        return this.mainFuncDef;
    }
    public void print(){
        for(Decl decl : this.decls) {
            if(decl == null)break;
            decl.print();
        }
        for (FuncDef funcDef : this.funcDefs) {
            if(funcDef == null)break;
            funcDef.print();
        }
        mainFuncDef.print();
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<CompUnit>");
    }
}
