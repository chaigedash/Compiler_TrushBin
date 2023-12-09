package Parser.Node;

import Lexer.Word;
import Utils.Config;
import Utils.IO;

import java.util.ArrayList;

public class Stmt {
    private Word ASSIGN;
    private Word SEMICN1;
    private Word SEMICN2;
    private Word IF;
    private Word LPRENT;
    private Word RPRENT;
    private Word ELSE;
    private Word FOR;
    private Word BREAK;
    private Word CONTINUE;
    private Word RETURN;
    private Word GETINT;
    private Word PRINTF;
    private ArrayList<Word> COMMAs;

    public enum StmtType {
        LValEqExp, Exp, Block, ifStmt, forStmt, breakStmt, continueStmt, returnExp, getint, print;
        public String toString() {
            switch (this) {
                case LValEqExp: return "LValEqExp";
                case Exp: return "Exp";
                case Block: return "Block";
                case ifStmt: return "ifStmt";
                default: return "懒得写了";
            }
        }
    }
    private StmtType stmtType;
    private LVal lVal;
    private Exp exp;
    private ArrayList<Exp> exps = new ArrayList<Exp>();
    private Block block;
    private Cond cond;
    private Stmt stmt1;
    private Stmt stmt2;
    private ForStmt forStmt1;
    private ForStmt forStmt2;
    private Word formatString;
    public Stmt(StmtType type, LVal lVal, Word ASSIGN, Exp exp, Word SEMICN) {
        this.stmtType = type;
        this.lVal = lVal;
        this.ASSIGN = ASSIGN;
        this.exp = exp;
        this.SEMICN1 = SEMICN;
    }
    public Stmt(StmtType type, Exp exp, Word SEMICN) {
        this.stmtType = type;
        this.exp = exp;
        this.SEMICN1 = SEMICN;
    }
    public Stmt(StmtType type, Block block) {
        this.stmtType = type;
        this.block = block;
    }
    public Stmt(StmtType type, Word IF, Word LPRENT, Cond cond, Word RPRENT, Stmt stmt1, Word ELSE, Stmt stmt2) {
        this.stmtType = type;
        this.IF = IF;
        this.LPRENT = LPRENT;
        this.cond = cond;
        this.RPRENT = RPRENT;
        this.stmt1 = stmt1;
        this.ELSE = ELSE;
        this.stmt2 = stmt2;
    }
    public Stmt(StmtType type, Word FOR, Word LPRENT, ForStmt forStmt1, Word SEMICN1, Cond cond, Word SEMICN2, ForStmt forStmt2, Word RPRENT, Stmt stmt) {
        this.stmtType = type;
        this.FOR = FOR;
        this.LPRENT = LPRENT;
        this.forStmt1 = forStmt1;
        this.SEMICN1 = SEMICN1;
        this.SEMICN2 = SEMICN2;
        this.cond = cond;
        this.forStmt2 = forStmt2;
        this.RPRENT = RPRENT;
        this.stmt1 = stmt;
    }
    public Stmt(StmtType type, Word BREAKorCONTINUE, Word SEMICN) {
        this.stmtType = type;
        if(type == StmtType.breakStmt) {
            this.BREAK = BREAKorCONTINUE;
        } else {
            this.CONTINUE = BREAKorCONTINUE;
        }
        this.SEMICN1 = SEMICN;
    }
    public Stmt(StmtType type, Word RETURN, Exp exp, Word SEMICN) {
        this.stmtType = type;
        this.RETURN = RETURN;
        this.exp = exp;
        this.SEMICN1 = SEMICN;
    }
    public Stmt(StmtType type, LVal lVal, Word ASSIGN, Word GETINT, Word LPRENT, Word RPRENT, Word SEMICN) {
        this.stmtType = type;
        this.lVal = lVal;
        this.ASSIGN = ASSIGN;
        this.GETINT = GETINT;
        this.LPRENT = LPRENT;
        this.RPRENT = RPRENT;
        this.SEMICN1 = SEMICN;
    }
    public Stmt(StmtType type, Word PRINTF, Word LPRENT, Word formatString, ArrayList<Word> COMMAs, ArrayList<Exp> exps, Word RPRENT, Word SEMICN) {
        this.stmtType = type;
        this.PRINTF = PRINTF;
        this.LPRENT = LPRENT;
        this.formatString = formatString;
        this.COMMAs = COMMAs;
        this.exps = exps;
        this.RPRENT = RPRENT;
        this.SEMICN1 = SEMICN;
    }
    public void print () {
        switch (stmtType) {
            case LValEqExp :
                lVal.print();
                IO.getIO().writeln(ASSIGN.print());
                exp.print();
                IO.getIO().writeln(SEMICN1.print());
                break;
            case Exp :
                if (exp != null) {
                    exp.print();
                }
                IO.getIO().writeln(SEMICN1.print());
                break;
            case Block:
                block.print();
                break;
            case ifStmt:
                IO.getIO().writeln(IF.print());
                IO.getIO().writeln(LPRENT.print());
                cond.print();
                IO.getIO().writeln(RPRENT.print());
                stmt1.print();
                if (ELSE != null) {
                    IO.getIO().writeln(ELSE.print());
                    stmt2.print();
                }
                break;
            case forStmt:
                IO.getIO().writeln(FOR.print());
                IO.getIO().writeln(LPRENT.print());
                if (forStmt1 != null) {
                    forStmt1.print();
                }
                IO.getIO().writeln(SEMICN1.print());
                if (cond != null){
                    cond.print();
                }
                IO.getIO().writeln(SEMICN2.print());
                if (forStmt2 != null) {
                    forStmt2.print();
                }
                IO.getIO().writeln(RPRENT.print());
                stmt1.print();
                break;
            case breakStmt:
                IO.getIO().writeln(BREAK.print());
                IO.getIO().writeln(SEMICN1.print());
                break;
            case continueStmt:
                IO.getIO().writeln(CONTINUE.print());
                IO.getIO().writeln(SEMICN1.print());
                break;
            case returnExp:
                IO.getIO().writeln(RETURN.print());
                if (exp != null) {
                    exp.print();
                }
                IO.getIO().writeln(SEMICN1.print());
                break;
            case getint:
                lVal.print();
                IO.getIO().writeln(ASSIGN.print());
                IO.getIO().writeln(GETINT.print());
                IO.getIO().writeln(LPRENT.print());
                IO.getIO().writeln(RPRENT.print());
                IO.getIO().writeln(SEMICN1.print());
                break;
            case print:
                IO.getIO().writeln(PRINTF.print());
                IO.getIO().writeln(LPRENT.print());
                IO.getIO().writeln(formatString.print());
                for (int i = 0; i < exps.size(); i++) {
                    IO.getIO().writeln(COMMAs.get(i).print());
                    exps.get(i).print();
                }
                IO.getIO().writeln(RPRENT.print());
                IO.getIO().writeln(SEMICN1.print());
                break;
            default:break;
        }
        if (Config.getInstance().Parser)
            IO.getIO().writeln("<Stmt>");
    }

    public Word getASSIGN() {
        return ASSIGN;
    }

    public Word getSEMICN1() {
        return SEMICN1;
    }

    public Word getSEMICN2() {
        return SEMICN2;
    }

    public Word getIF() {
        return IF;
    }

    public Word getLPRENT() {
        return LPRENT;
    }

    public Word getRPRENT() {
        return RPRENT;
    }

    public Word getELSE() {
        return ELSE;
    }

    public Word getFOR() {
        return FOR;
    }

    public Word getBREAK() {
        return BREAK;
    }

    public Word getCONTINUE() {
        return CONTINUE;
    }

    public Word getRETURN() {
        return RETURN;
    }

    public Word getGETINT() {
        return GETINT;
    }

    public Word getPRINTF() {
        return PRINTF;
    }

    public ArrayList<Word> getCOMMAs() {
        return COMMAs;
    }

    public StmtType getStmtType() {
        return stmtType;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public Block getBlock() {
        return block;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt1() {
        return stmt1;
    }

    public Stmt getStmt2() {
        return stmt2;
    }

    public ForStmt getForStmt1() {
        return forStmt1;
    }

    public ForStmt getForStmt2() {
        return forStmt2;
    }

    public Word getFormatString() {
        return formatString;
    }
}
