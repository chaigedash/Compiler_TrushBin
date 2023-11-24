package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

public class UnaryExp {
    private PrimaryExp primaryExp;
    private Word ident;
    private Word LPARENT;
    private Word RPARENT;
    private FuncRParams funcRParams;
    private UnaryOp unaryOp = null;
    private UnaryExp unaryExp;
    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }
    public UnaryExp(Word ident, Word LPARENT, FuncRParams funcRParams, Word RPARENT) {
        this.ident = ident;
        this.LPARENT = LPARENT;
        this.funcRParams = funcRParams;
        this.RPARENT = RPARENT;
    }
    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }
    public void print () {
        if (primaryExp != null) {
            primaryExp.print();
        }
        else if (unaryOp != null) {
            unaryOp.print();
            unaryExp.print();
        }
        else {
            IO.getIO().writeln(ident.print());
            IO.getIO().writeln(LPARENT.print());
            if(funcRParams != null) {
                funcRParams.print();
            }
            IO.getIO().writeln(RPARENT.print());
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<UnaryExp>");
    }
    public Word getContent () {
        if (primaryExp != null) {
            return primaryExp.getContent();
        }
        else if (ident != null) {
            return ident;
        }
        else {
            return unaryExp.getContent();
        }
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public Word getIdent() {
        return ident;
    }

    public Word getLPARENT() {
        return LPARENT;
    }

    public Word getRPARENT() {
        return RPARENT;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public UnaryOp getUnaryOp() {
        return unaryOp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }
}
