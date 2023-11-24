package Parser.Node;

public class BlockItem {
    private Decl decl;
    private Stmt stmt;
    public BlockItem(Decl decl) {
        this.decl = decl;
    }
    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
    }
    public void print () {
        if (stmt != null) {
            stmt.print();
        }
        else {
            decl.print();
        }
//        if (Config.getInstance().Parser)
//            IO.getIO().writeln("<BlockItem>");
    }

    public Decl getDecl() {
        return decl;
    }

    public Stmt getStmt() {
        return stmt;
    }
}
