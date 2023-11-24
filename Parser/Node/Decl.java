package Parser.Node;

public class Decl {
    private ConstDecl constDecl;
    private VarDecl varDecl;
    public Decl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }
    public Decl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }
    public void print() {
        if (this.constDecl != null) {
            constDecl.print();
        }
        else {
            varDecl.print();
        }
//        if (Config.getInstance().Parser)
//            IO.getIO().writeln("<Decl>");
    }
    public ConstDecl getConstDecl() {
        return this.constDecl;
    }
    public VarDecl getVarDecl() {
        return this.varDecl;
    }
}
