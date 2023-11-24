package Parser;

import SymbolTable_v2.SymbolTable_v2;
import SymbolTable_v2.Symbol_v2;
import Lexer.LexType;
import Error.ErrorType;
import Error.ErrorHandler;
import Lexer.Word;
import Parser.Node.*;
import Parser.Node.Number;

import java.util.ArrayList;
import java.util.Stack;

public class ParserAnalyzer {
    private static final ParserAnalyzer INSTANCE = new ParserAnalyzer();
    public static ParserAnalyzer getParser() {
        return INSTANCE;
    }
    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }
    private ArrayList<Word> words = new ArrayList<Word>();
    private int curPos;
    private boolean isInFor;
    private LexType functionType;
    private boolean returnFlag;
//    private int layer;
//    public SymbolTable rootTable;
    private SymbolTable_v2 curSymbolTable;
    private SymbolTable_v2 paramsTable;
    private boolean ExpNeedInverse;
//    private Integer expHeadPos, expEndPos;
    public class ExpInterval {
        Integer startIndex_words, endIndex_words;
        public ExpInterval (Integer startIndex_words, Integer endIndex_words) {
            this.startIndex_words = startIndex_words;
            this.endIndex_words = endIndex_words;
        }
    }
    private ArrayList<ExpInterval> inversedExpIntervals = new ArrayList<ExpInterval>();
//    private SymbolTable curTable;
//    private ArrayList<Symbol> symbolTable = new ArrayList<Symbol>();
//    private int [] symbolTablePos = new int[15];
    public CompUnit qidong() {
        this.curPos = 0;
        this.isInFor = false;
        this.functionType = null;
        this.returnFlag = false;
//        rootTable = new SymbolTable(0);
        curSymbolTable = new SymbolTable_v2();
        CompUnit root = compUnit();
        return root;
    }
    private Word getWord(LexType type) {
        if(words.get(curPos).lexType == type) {
            Word returnWord = words.get(curPos);
            if (type == LexType.IDENFR) {
//                if (rootTable.getChildTable(layer).isIdentInThisTable(returnWord.word)) {
                if (curSymbolTable.searchSymbol_curTable(returnWord.word) != null) {
                    ErrorHandler.getErrorHandler().handleError(ErrorType.b, returnWord);
                }
            }
            curPos++;
            return returnWord;
        } else {
            switch (type) {
                case SEMICN -> {
                    ErrorHandler.getErrorHandler().handleError(ErrorType.i, words.get(curPos - 1));
                    return new Word(LexType.SEMICN);
                }
                case RPARENT -> {
                    ErrorHandler.getErrorHandler().handleError(ErrorType.j, words.get(curPos - 1));
                    return new Word(LexType.RPARENT);
                }
                case RBRACK -> {
                    ErrorHandler.getErrorHandler().handleError(ErrorType.k, words.get(curPos - 1));
                    return new Word(LexType.RBRACK);
                }
                default -> {
                }
            }
        }
        return null;
    }
    public int checkString (String str) {
        int count = 0;
        for (int i = 1; i < str.length() - 1; i++) {
            if (str.charAt(i) == 32 || str.charAt(i) == 33 || (str.charAt(i) >= 40 && str.charAt(i) <= 126)) {
                if (str.charAt(i) == '\\') {
                    if (str.charAt(i+1) != 'n' ) {
                        return -1;
                    }
                }
            }
            else if (str.charAt(i) == '%' && str.charAt(i + 1) == 'd') {
                count++;
                i+=2;
            }
            else {
                return -1;
            }
        }
        return count;
    }
    private CompUnit compUnit() {
        ArrayList<Decl> decls = new ArrayList<Decl>();
        ArrayList<FuncDef> funcDefs = new ArrayList<FuncDef>();
        while (words.get(curPos).lexType == LexType.CONSTTK || words.get(curPos + 2).lexType != LexType.LPARENT) {
            Decl decl = Decl();
            decls.add(decl);
        }
        while (words.get(curPos + 1).lexType != LexType.MAINTK) {
            FuncDef funcDef = FuncDef();
            funcDefs.add(funcDef);
        }
        MainFuncDef mainFuncDef = MainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }
    private Decl Decl() {
        if(words.get(curPos).lexType == LexType.CONSTTK) {
            ConstDecl constDecl = ConstDecl();
            return new Decl(constDecl);
        }
        else {
            VarDecl varDecl = VarDecl();
            return new Decl(varDecl);
        }
    }
    private ConstDecl ConstDecl() {
        Word CONST = words.get(curPos);
        curPos++;
        BType bType = BType();
        ArrayList<ConstDef> constDefs = new ArrayList<ConstDef>();
        ArrayList<Word> COMMAs = new ArrayList<Word>();
        Word SEMICN = null;
        ConstDef constDef = ConstDef();
        constDefs.add(constDef);
        while (words.get(curPos).lexType == LexType.COMMA) {
            COMMAs.add(words.get(curPos));
            curPos++;
            constDef = ConstDef();
            constDefs.add(constDef);
        }
        SEMICN = getWord(LexType.SEMICN);
        return new ConstDecl(CONST, COMMAs, bType, constDefs, SEMICN);
    }
    private BType BType() {
        BType bType = new BType("int", words.get(curPos));
        curPos++;
        return bType;
    }
    private ConstDef ConstDef() {
        Word ident = null;
        ArrayList<Word> LBRACKs = new ArrayList<Word>();
        ArrayList<Word> RBRACKs = new ArrayList<Word>();
        Word ASSIGN = null;
        int dimension = 0;
        if(words.get(curPos).lexType == LexType.IDENFR) {
            ident = getWord(LexType.IDENFR);
//            rootTable.getChildTable(layer).addSymbol(new Symbol(ident.word, Symbol.SymbolKind.constVar, LexType.INTTK, layer));
            ArrayList<ConstExp> constExps = new ArrayList<ConstExp>();
            ConstInitVal constInitVal = null;
            while (words.get(curPos).lexType == LexType.LBRACK) {
                LBRACKs.add(words.get(curPos));
                curPos++;
                ConstExp constExp = ConstExp();
                constExps.add(constExp);
                RBRACKs.add(getWord(LexType.RBRACK));
                dimension++;
            }
            if(words.get(curPos).lexType == LexType.ASSIGN) {
                ASSIGN = words.get(curPos);
                curPos++;
                constInitVal = ConstInitVal();
                curSymbolTable.addSymbol(new Symbol_v2(ident.word, Symbol_v2.Type.constVar));
//                rootTable.getChildTable(layer).addSymbol(new Symbol(ident.word, Symbol.SymbolKind.constVar, LexType.INTTK, dimension, layer));
//                symbolTable.add(new Symbol(ident.word, Symbol.SymbolKind.constVar, LexType.INTTK, dimension, layer));
                return new ConstDef(ident, LBRACKs, constExps, RBRACKs, ASSIGN, constInitVal);
            }
        }
        return null;
    }
    private ConstInitVal ConstInitVal() {
        ArrayList<ConstInitVal> constInitVals = new ArrayList<ConstInitVal>();
        Word LBRACE =  null;
        ArrayList<Word> COMMAs = new ArrayList<Word>();
        Word RBRACE =  null;
        if(words.get(curPos).lexType == LexType.LBRACE) {
            LBRACE = words.get(curPos);
            curPos++;
            if(words.get(curPos).lexType != LexType.RBRACE) {
                ConstInitVal constInitVal = ConstInitVal();
                constInitVals.add(constInitVal);
                while(words.get(curPos).lexType == LexType.COMMA) {
                    COMMAs.add(words.get(curPos));
                    curPos++;
                    constInitVal = ConstInitVal();
                    constInitVals.add(constInitVal);
                }
            }
            if (words.get(curPos).lexType == LexType.RBRACE) {
                RBRACE = words.get(curPos);
                curPos++;
                return new ConstInitVal(LBRACE, COMMAs, RBRACE,constInitVals);
            }
            else {
                //error
            }
        }
        else {
            ConstExp constExp = ConstExp();
            return new ConstInitVal(constExp);
        }
        return null;
    }
    private VarDecl VarDecl() {
        BType bType = new BType("int", words.get(curPos));
        curPos++;
        ArrayList<VarDef> varDefs = new ArrayList<VarDef>();
        VarDef varDef = VarDef();
        varDefs.add(varDef);
        ArrayList<Word> COMMAs = new ArrayList<Word>();
        Word SEMICN = null;
        while(words.get(curPos).lexType == LexType.COMMA) {
            COMMAs.add(words.get(curPos));
            curPos++;
            varDef = VarDef();
            varDefs.add(varDef);
        }
        SEMICN = getWord(LexType.SEMICN);
        return new VarDecl(bType, varDefs, COMMAs, SEMICN);
    }
    private VarDef VarDef () {
        Word ident = null;
        ArrayList<Word> LBRACKs = new ArrayList<Word>();
        ArrayList<Word> RBRACKs = new ArrayList<Word>();
        Word ASSIGN = null;
        ArrayList<ConstExp> constExps = new ArrayList<ConstExp>();
        InitVal initVal = null;
        int dimension = 0;
        if(words.get(curPos).lexType == LexType.IDENFR) {
            ident = getWord(LexType.IDENFR);
//            rootTable.getChildTable(layer).addSymbol(new Symbol(ident.word, Symbol.SymbolKind.var, LexType.INTTK, layer));
            while(words.get(curPos).lexType == LexType.LBRACK) {
                LBRACKs.add(words.get(curPos));
                curPos++;
                ConstExp constExp = ConstExp();
                constExps.add(constExp);
                RBRACKs.add(getWord(LexType.RBRACK));
                dimension++;
            }
            curSymbolTable.addSymbol(new Symbol_v2(ident.word, Symbol_v2.Type.val, dimension));
//            rootTable.getChildTable(layer).addSymbol(new Symbol(ident.word, Symbol.SymbolKind.var, LexType.INTTK, dimension, layer));
            if(words.get(curPos).lexType == LexType.ASSIGN) {
                ASSIGN = words.get(curPos);
                curPos++;
                initVal = InitVal();
            }
            return new VarDef(ident, LBRACKs, RBRACKs, constExps, ASSIGN, initVal);
        }
        return null;
    }
    private InitVal InitVal() {
        Exp exp = null;
        Word LBRACE = null;
        ArrayList<Word> COMMAs = new ArrayList<Word>();
        Word RBRACE = null;
        ArrayList<InitVal> initVals = new ArrayList<InitVal>();
        if(words.get(curPos).lexType == LexType.LBRACE) {
            LBRACE = words.get(curPos);
            curPos++;
            if(words.get(curPos).lexType != LexType.RBRACE) {
                InitVal initVal = InitVal();
                initVals.add(initVal);
                while (words.get(curPos).lexType == LexType.COMMA) {
                    COMMAs.add(words.get(curPos));
                    curPos++;
                    initVal = InitVal();
                    initVals.add(initVal);
                }
            }
            if(words.get(curPos).lexType == LexType.RBRACE) {
                RBRACE = words.get(curPos);
                curPos++;
                return new InitVal(LBRACE, initVals, COMMAs, RBRACE);
            }
            else {
                //error
            }
        }
        else {
//            ExpNeedInverse = true;
            exp = Exp();
            return new InitVal(exp);
        }
        return null;
    }
    private FuncDef FuncDef () {
        FuncType funcType = FuncType();
        this.functionType = funcType.getFunctionType();
        this.returnFlag = false;
        Word ident = null;
        FuncFParams funcFParams = null;
        Block block = null;
        ident = getWord(LexType.IDENFR);
        Word LPRENT = null;
        Word RPRENT = null;
        if(words.get(curPos).lexType == LexType.LPARENT) {
            LPRENT = words.get(curPos);
            curPos++;
            if(words.get(curPos).lexType == LexType.INTTK) {
                funcFParams = FuncFParams();
            }
            if (paramsTable != null) {
                Symbol_v2 funcSymbol = new Symbol_v2(ident.word, Symbol_v2.Type.function, functionType);
                for (Symbol_v2 param : paramsTable.getSymbols()) {
                    funcSymbol.addParam(param);
                }
                curSymbolTable.addSymbol(funcSymbol);
//                rootTable.addSymbol(new Symbol(ident.word, Symbol.SymbolKind.func, functionType, layer, paramsTable.getSymbols()));
            }
            else {
                curSymbolTable.addSymbol(new Symbol_v2(ident.word, Symbol_v2.Type.function, functionType));
//                rootTable.addSymbol(new Symbol(ident.word, Symbol.SymbolKind.func, functionType, layer));
            }
            RPRENT = getWord(LexType.RPARENT);
            block = Block();
            if (functionType == LexType.INTTK && !returnFlag) {
                ErrorHandler.getErrorHandler().handleError(ErrorType.g, words.get(curPos - 1));
            }
            this.functionType = null;
            this.returnFlag = false;
            return new FuncDef(funcType, ident, LPRENT, funcFParams, RPRENT, block);
        }
        return null;
    }
    private MainFuncDef MainFuncDef () {
        Word INT = null;
        Word MAIN = null;
        Word LPARENT = null;
        Word RPARENT = null;
        if (words.get(curPos).lexType == LexType.INTTK) {
            INT = words.get(curPos);
            curPos++;
            this.functionType = INT.lexType;
            if (words.get(curPos).lexType == LexType.MAINTK) {
                MAIN = words.get(curPos);
                curPos++;
                if (words.get(curPos).lexType == LexType.LPARENT) {
                    LPARENT = words.get(curPos);
                    curPos++;
                    RPARENT = getWord(LexType.RPARENT);
                    Block block = Block();
                    if (!returnFlag) {
                        ErrorHandler.getErrorHandler().handleError(ErrorType.g, words.get(curPos - 1));
                        block.getBlockItems().add(
                            new BlockItem(
                                new Stmt(
                                        Stmt.StmtType.returnExp,
                                        new Word(LexType.RETURNTK,
                                                "return", -1,
                                                false),
                                        new Exp(new AddExp(new MulExp(new UnaryExp(new PrimaryExp(new Number(new Word(LexType.INTCON, 0, -1, false))))))),
                                        new Word(LexType.SEMICN, ";", -1, false)
                                )));
                    }
                    this.functionType = null;
                    this.returnFlag = false;
                    return new MainFuncDef(INT, MAIN, LPARENT, RPARENT, block);
                }
            }
        }
        //error
        return null;
    }
    private FuncType FuncType () {
        if (words.get(curPos).lexType == LexType.INTTK || words.get(curPos).lexType == LexType.VOIDTK) {
            Word funcType = words.get(curPos);
            curPos++;
            return new FuncType(funcType);
        }
        //error
        return null;
    }
    private FuncFParams FuncFParams () {
//        paramsTable = new SymbolTable(layer + 1);
        paramsTable = new SymbolTable_v2(); // 只是个暂存表
        ArrayList<FuncFParam> funcFParams = new ArrayList<FuncFParam>();
        FuncFParam funcParam = FuncFParam();
        funcFParams.add(funcParam);
        ArrayList<Word> COMMAs = new ArrayList<Word>();
        while(words.get(curPos).lexType == LexType.COMMA) {
            COMMAs.add(words.get(curPos));
            curPos++;
            funcParam = FuncFParam();
            funcFParams.add(funcParam);
        }
        return new FuncFParams(funcFParams, COMMAs);
    }
    private FuncFParam FuncFParam () {
        BType bType = new BType("int", words.get(curPos));
        curPos++;
        Word ident = null;
        Word LBRACK1 = null;
        Word RBRACK1 = null;
        Word LBRACK2 = null;
        Word RBRACK2 = null;
        ConstExp constExp = null;
        int dimension = 0;
//        ident = getWord(LexType.IDENFR);
        ident = words.get(curPos);
        curPos++;
//        for (Symbol_v2 param : paramsTable.getSymbols()) {
//            if (param.name.equals(ident.word)) {
//                ErrorHandler.getErrorHandler().handleError(ErrorType.b, ident);
//            }
//        }
        if (paramsTable.searchSymbol_curTable(ident.word) != null) {
            ErrorHandler.getErrorHandler().handleError(ErrorType.b, ident);
        }
//        curTable.addSymbol(new Symbol(ident.word, Symbol.SymbolKind.para, LexType.INTTK, layer));
//        paramsTable.addSymbol(new Symbol(ident.word, Symbol.SymbolKind.para, LexType.INTTK, layer + 1));
        if (words.get(curPos).lexType == LexType.LBRACK) {
            LBRACK1 = words.get(curPos);
            curPos++;
            RBRACK1 = getWord(LexType.RBRACK);
            dimension++;
            if (words.get(curPos).lexType == LexType.LBRACK) {
                LBRACK2 = words.get(curPos);
                curPos++;
                constExp = ConstExp();
                RBRACK2 = getWord(LexType.RBRACK);
                dimension++;
                paramsTable.addSymbol(new Symbol_v2(ident.word, Symbol_v2.Type.param, dimension));
//                paramsTable.addSymbol(new Symbol(ident.word, Symbol.SymbolKind.para, LexType.INTTK, dimension, layer + 1));
                return new FuncFParam(2,bType, ident, LBRACK1, RBRACK1, LBRACK2, RBRACK2, constExp);
            }
            else {
                paramsTable.addSymbol(new Symbol_v2(ident.word, Symbol_v2.Type.param, dimension));
//                paramsTable.addSymbol(new Symbol(ident.word, Symbol.SymbolKind.para, LexType.INTTK, dimension, layer + 1));
                return new FuncFParam(1,bType, ident, LBRACK1, RBRACK1, LBRACK2, RBRACK2, constExp);
            }
        }
        else {
            paramsTable.addSymbol(new Symbol_v2(ident.word, Symbol_v2.Type.param, dimension));
//            paramsTable.addSymbol(new Symbol(ident.word, Symbol.SymbolKind.para, LexType.INTTK, dimension, layer + 1));
            return new FuncFParam(0, bType, ident, LBRACK1, RBRACK1, LBRACK2, RBRACK2, constExp);
        }
    }
    private Block Block () {
        BlockItem blockItem = null;
        Word LBRACE = null;
        Word RBRACE = null;
        if (words.get(curPos).lexType == LexType.LBRACE) {
            LBRACE = words.get(curPos);
            curPos++;
            curSymbolTable = curSymbolTable.createChildTable();
//            rootTable.addChildTable(new SymbolTable(layer), layer);
            if (paramsTable != null) {
//                for (Symbol symbol : paramsTable.getSymbols()) {
//                    rootTable.getChildTable(layer).addSymbol(symbol);
//                }
                for (Symbol_v2 param : paramsTable.getSymbols()) {
                    curSymbolTable.addSymbol(param);
                }
            }

            paramsTable = null;
            ArrayList<BlockItem> blockItems = new ArrayList<BlockItem>();
            while (words.get(curPos).lexType != LexType.RBRACE){
                blockItem = BlockItem();
                blockItems.add(blockItem);
            }
            if (words.get(curPos).lexType == LexType.RBRACE) {
                RBRACE = words.get(curPos);
                curPos++;
//                rootTable.getChildTable(layer).close(false);
                curSymbolTable = curSymbolTable.getPreTable();
                return new Block(LBRACE, blockItems, RBRACE);
            }
        }
        return null;
    }
    private BlockItem BlockItem () {
        Decl decl = null;
        Stmt stmt = null;
        if (words.get(curPos).lexType == LexType.CONSTTK || words.get(curPos).lexType == LexType.INTTK) {
            decl = Decl();
            return new BlockItem(decl);
        }
        else {
            stmt = Stmt();
            return new BlockItem(stmt);
        }
    }
    private Stmt Stmt () {
        if (words.get(curPos).lexType == LexType.IFTK) {
            Word IF = words.get(curPos);
            Word LPRENT = null;
            Cond cond = null;
            Word RPRENT = null;
            Stmt stmt1 = null;
            Word ELSE = null;
            Stmt stmt2 = null;
            curPos++;
            if (words.get(curPos).lexType == LexType.LPARENT) {
                LPRENT = words.get(curPos);
                curPos++;
                cond = Cond();
                RPRENT = getWord(LexType.RPARENT);
                stmt1 = Stmt();
                if (words.get(curPos).lexType == LexType.ELSETK) {
                    ELSE = words.get(curPos);
                    curPos++;
                    stmt2 = Stmt();
                }
                return new Stmt(Stmt.StmtType.ifStmt, IF, LPRENT, cond, RPRENT, stmt1, ELSE, stmt2);
            }
            else {
                //error
            }
        }
        else if (words.get(curPos).lexType == LexType.FORTK) {
            isInFor = true;
            Word FOR = words.get(curPos);
            curPos++;
            ForStmt forStmt1 = null;
            Cond cond = null;
            ForStmt forStmt2 = null;
            if (words.get(curPos).lexType == LexType.LPARENT) {
                Word LPRENT = words.get(curPos);
                curPos++;
                if(words.get(curPos).lexType != LexType.SEMICN) {
                    forStmt1 = ForStmt();
                }
                if(words.get(curPos).lexType != LexType.SEMICN) {
                    //error
                }
                else {
                    Word SEMICN1 = words.get(curPos);
                    curPos++;
                    if(words.get(curPos).lexType != LexType.SEMICN) {
                        cond = Cond();
                    }
                    if(words.get(curPos).lexType != LexType.SEMICN) {
                        //error
                    }
                    else {
                        Word SEMICN2 = words.get(curPos);
                        curPos++;
                        if(words.get(curPos).lexType != LexType.RPARENT) {
                            forStmt2 = ForStmt();
                        }
                        if(words.get(curPos).lexType != LexType.RPARENT) {
                            //error
                        }
                        else {
                            Word RPRENT = words.get(curPos);
                            curPos++;
                            Stmt stmt = Stmt();
                            isInFor = false;
                            return new Stmt(Stmt.StmtType.forStmt, FOR, LPRENT, forStmt1, SEMICN1, cond, SEMICN2, forStmt2, RPRENT, stmt);
                        }
                    }
                }
            }
            else {
                //error
            }
        }
        else if (words.get(curPos).lexType == LexType.BREAKTK) {
            if (!isInFor) {
                ErrorHandler.getErrorHandler().handleError(ErrorType.m, words.get(curPos));
            }
            Word BREAK = words.get(curPos);
            Word SEMICN = null;
            curPos++;
            SEMICN = getWord(LexType.SEMICN);
            return new Stmt(Stmt.StmtType.breakStmt, BREAK, SEMICN);
        }
        else if (words.get(curPos).lexType == LexType.CONTINUETK) {
            if (!isInFor) {
                ErrorHandler.getErrorHandler().handleError(ErrorType.m, words.get(curPos));
            }
            Word CONTINUE = words.get(curPos);
            Word SEMICN = null;
            curPos++;
            SEMICN = getWord(LexType.SEMICN);
            return new Stmt(Stmt.StmtType.continueStmt, CONTINUE, SEMICN);
        }
        else if (words.get(curPos).lexType == LexType.RETURNTK) {
            Word RETURN = words.get(curPos);
            curPos++;
            Exp exp = null;
            Word SEMICN = null;
            if (words.get(curPos).lexType == LexType.SEMICN && functionType == LexType.INTTK) {
                ErrorHandler.getErrorHandler().handleError(ErrorType.g, RETURN);
            }
            if (words.get(curPos).lexType != LexType.SEMICN) {
                if (this.functionType == LexType.VOIDTK) {
                    ErrorHandler.getErrorHandler().handleError(ErrorType.f, RETURN);
                }
//                ExpNeedInverse = true;
                exp = Exp();
            }
            SEMICN = getWord(LexType.SEMICN);
            returnFlag = true;
            return new Stmt(Stmt.StmtType.returnExp, RETURN, exp, SEMICN);
        }
        else if (words.get(curPos).lexType == LexType.PRINTFTK) {
            int printPos = curPos;
            Word PRINTF = words.get(curPos);
            Word LPARENT = null;
            Word RPARENT = null;
            Word formatString = null;
            ArrayList<Word> COMMAs = new ArrayList<Word>();
            ArrayList<Exp> exps = new ArrayList<Exp>();
            Word SEMICN = null;
            curPos++;
            if (words.get(curPos).lexType == LexType.LPARENT) {
                LPARENT = words.get(curPos);
                curPos++;
                if (words.get(curPos).lexType == LexType.STRCON) {
                    formatString = words.get(curPos);
                    int checkRes = checkString(formatString.word);
                    if (checkRes == -1) {
                        ErrorHandler.getErrorHandler().handleError(ErrorType.a, formatString);
                    }
                    curPos++;
                    while (words.get(curPos).lexType == LexType.COMMA) {
                        COMMAs.add(words.get(curPos));
                        curPos++;
                        ExpNeedInverse = true;
                        Exp exp = Exp();
                        exps.add(exp);
                    }
                    if (checkRes != -1 && checkRes != COMMAs.size()) {
                        ErrorHandler.getErrorHandler().handleError(ErrorType.l, words.get(printPos));
                    }
//                    for (Exp exp : exps) {
//                        Word expContent = exp.getContent();
//                        if (rootTable.searchSymbol(expContent.word, layer).type != LexType.INTTK) {
//
//                        }
//                    }
                    RPARENT = getWord(LexType.RPARENT);
                    SEMICN = getWord(LexType.SEMICN);
                }
                else {
                    //error
                }
            }
            return new Stmt(Stmt.StmtType.print, PRINTF, LPARENT, formatString, COMMAs, exps, RPARENT, SEMICN);
        }
        else if (words.get(curPos).lexType == LexType.SEMICN) {
            Word SEMICN = words.get(curPos);
            curPos++;
            Exp exp = null;
            return new Stmt(Stmt.StmtType.Exp, exp, SEMICN);
        }
        else if (words.get(curPos).lexType == LexType.LBRACE) {
            Block block = Block();
            return new Stmt(Stmt.StmtType.Block, block);
        }
        else {
            int assign = -1;
            int line = words.get(curPos).lineNum;
            for (int i = curPos; i < words.size(); i++) {
                if (words.get(i).lexType == LexType.ASSIGN) {
                    assign = i;
                    break;
                }
                if (words.get(i).lineNum != line ) {
                    break;
                }
            }
            if (assign != -1) {
                Word ident = words.get(curPos);
                LVal lVal = LVal();
//                if (rootTable.checkInThisAndChildTable(ident.word, layer) && rootTable.searchSymbol(ident.word, layer).kind == Symbol.SymbolKind.constVar) {
//                    ErrorHandler.getErrorHandler().handleError(ErrorType.h, words.get(curPos - 1));
//                }
                if (curSymbolTable.searchSymbol(ident.word) != null){
                    if (curSymbolTable.searchSymbol(ident.word).symbolType == Symbol_v2.Type.constVar) {
                        ErrorHandler.getErrorHandler().handleError(ErrorType.h, words.get(curPos - 1));
                    }
                }
                Word ASSIGN = words.get(curPos);
                Word GETINT = null;
                Word LPRENT = null;
                Word RPRENT = null;
                Word SEMICN = null;
                curPos++;
                if (words.get(curPos).lexType == LexType.GETINTTK) {
                    GETINT = words.get(curPos);
                    curPos++;
                    if (words.get(curPos).lexType == LexType.LPARENT) {
                        LPRENT = words.get(curPos);
                        curPos++;
                        RPRENT = getWord(LexType.RPARENT);
                        SEMICN = getWord(LexType.SEMICN);
                        return new Stmt(Stmt.StmtType.getint, lVal, ASSIGN, GETINT, LPRENT, RPRENT, SEMICN);
                    }
                }
                else {
//                    ExpNeedInverse = true;
                    Exp exp = Exp();
                    SEMICN = getWord(LexType.SEMICN);
                    return new Stmt(Stmt.StmtType.LValEqExp, lVal, ASSIGN, exp, SEMICN);
                }
            } else {
//                ExpNeedInverse = true;
                Exp exp = Exp();
                Word SEMICN = null;
                SEMICN = getWord(LexType.SEMICN);
                return new Stmt(Stmt.StmtType.Exp, exp, SEMICN);
            }
        }
        return null;
    }
    private ForStmt ForStmt () {
        Word ident = words.get(curPos);
        LVal lVal = LVal();
        if (curSymbolTable.searchSymbol(ident.word) != null){
            if (curSymbolTable.searchSymbol(ident.word).symbolType == Symbol_v2.Type.constVar) {
                ErrorHandler.getErrorHandler().handleError(ErrorType.h, words.get(curPos - 1));
            }
        }
//        if (rootTable.checkInThisAndChildTable(ident.word, layer) && rootTable.searchSymbol(ident.word, layer).kind == Symbol.SymbolKind.constVar) {
//            ErrorHandler.getErrorHandler().handleError(ErrorType.h, words.get(curPos - 1));
//        }
        Word ASSIGN = null;
        if(words.get(curPos).lexType == LexType.ASSIGN) {
            ASSIGN = words.get(curPos);
            curPos++;
        }
        else {
            //error
        }
        Exp exp = Exp();
        return new ForStmt(lVal, ASSIGN, exp);
    }
    private int InverseExp (Integer expHeadPos, Integer expEndPos) {
        Integer headPos = expHeadPos;
        Integer maxPos = expEndPos;
        if (maxPos == null) {
            maxPos = words.size() - 1;
        }
        // 单目保留
        while(headPos <= maxPos && (words.get(headPos).lexType == LexType.PLUS || words.get(headPos).lexType == LexType.MINU)) {
                headPos++;
        }
        if (headPos == maxPos) {return maxPos;}
        // exp逆序
        Stack<Integer> intervalEndpoints = new Stack<Integer>();
        int i = 0;
        for (i = headPos; i <= maxPos; i++) {
//            System.out.println("接下来上场的是： " + words.get(i).lexType);
            if (words.get(i).lexType == LexType.INTCON
                || words.get(i).lexType == LexType.PLUS ||  words.get(i).lexType == LexType.MINU
                || words.get(i).lexType == LexType.MULT || words.get(i).lexType == LexType.DIV || words.get(i).lexType == LexType.MOD) {
                continue;
            }
            else if (words.get(i).lexType == LexType.LPARENT) {
                intervalEndpoints.push(i);
//                Stack<Integer> xiaoxiaole = new Stack<Integer>();
//                xiaoxiaole.push(i);
//                i++;
//                while (xiaoxiaole.size() != 0) {
////                    System.out.println("接下来上场的是 i : " + words.get(i).lexType);
//                    if (words.get(i).lexType == LexType.LPARENT) {
//                        xiaoxiaole.push(i);
//                    }
//                    else if (words.get(i).lexType == LexType.RPARENT) {
//                        int top = xiaoxiaole.pop();
//                    }
//                    i++;
//                }
                i = jumpParents(i);
                intervalEndpoints.push(i);
                continue;
            }
            else if (words.get(i).lexType == LexType.IDENFR) {
                if (words.get(i + 1).lexType == LexType.LPARENT) {
                    intervalEndpoints.push(i); // func_ident
                    i++;
                    i = jumpParents(i);
                    intervalEndpoints.push(i);
                    continue;
                }
                else if (words.get(i + 1).lexType == LexType.LBRACK) {
                    intervalEndpoints.push(i); // ident
                    i++;
                    while (words.get(i).lexType != LexType.RBRACK) {
                        i++;
                    }
                    if (words.get(i + 1).lexType == LexType.LBRACK) { // 二维数组
                        i++;
                        while (words.get(i).lexType != LexType.RBRACK) {
                            i++;
                        }
                    }
                    intervalEndpoints.push(i); // ]
                }
                continue;
            }
            else {
                break;
            }
        }
        int rearPos;
        rearPos = i > maxPos ? maxPos : i - 1;
        ArrayList<Word> temp = new ArrayList<Word>();
        int nextPoint = -1;
        if (intervalEndpoints.size() > 0) {
            nextPoint = intervalEndpoints.pop();
        }
        for (i = rearPos; i >= headPos; i--) {
            if (i == nextPoint) {
                int tempHead = intervalEndpoints.pop();
                for (int j = tempHead; j <= nextPoint; j++) {
                    temp.add(words.get(j));
//                    System.out.println(words.get(j).lexType);
                }
                i = tempHead;
                nextPoint = intervalEndpoints.size() > 0 ? intervalEndpoints.pop() : -1;
            }
            else {
                temp.add(words.get(i));
            }
        }
//        System.out.println("---------temp-----------");
//        for (Word te: temp) {
//            System.out.print(te.lexType + " ");
//            if (te.word == null) {
//                System.out.println(te.number);
//            }
//            else {
//                System.out.println(te.word);
//            }
//        }
        int tempPos;
//        System.out.println(headPos + " ~ " + rearPos + " ");
        for (i = headPos, tempPos = 0; i <= rearPos && tempPos < temp.size(); i++, tempPos++) {
            words.set(i, temp.get(tempPos));
        }

        return rearPos;
    }
    private boolean isInversed (Integer startPos) {
        for (ExpInterval e : inversedExpIntervals) {
            if (startPos >= e.startIndex_words && startPos < e.endIndex_words) {
                return true;
            }
        }
        return false;
    }
    private int jumpParents (int i) {
        int pretendStack = 1; // i = (.index
        while(pretendStack != 0) {
            i++;
            if (i < words.size() && words.get(i).lexType == LexType.LPARENT) {
                pretendStack++;
            }
            else if (i < words.size() && words.get(i).lexType == LexType.RPARENT) {
                pretendStack--;
            }
        }
        return i; // words.get(i) == )
    }
    private int jumpXBRACK (int i) {
        // i = [.index
        while(i < words.size() && words.get(i).lexType != LexType.RBRACK) {
            i++;
        }
        //out: i = ].index
        if (i+1 < words.size() && words.get(i+1).lexType == LexType.LBRACK) {
            i++;
            while(i < words.size() && words.get(i).lexType != LexType.RBRACK) {
                i++;
            }
        }
        return i; // words.get(i) == ]
    }
    private void dealParent (int head, int rear) {
        int maxLayer = 0; // 最大嵌套层数，0-n都能做
        Stack<Word> countMaxLayerStack = new Stack<Word>();
        Integer headPos_nextLayer = head, endPos_nextLayer = rear;
        for (int i = head; i <= rear; i++) {
            if (words.get(i).lexType == LexType.LPARENT) {
                if (words.get(i - 1).lexType == LexType.IDENFR) {
                    i = jumpParents(i);
                    continue;
                }
                countMaxLayerStack.push(words.get(i));
            }
            else if (words.get(i).lexType == LexType.RPARENT) {
                countMaxLayerStack.push(words.get(i));
            }
        }
        ArrayList<Word> array = new ArrayList<Word>();
        while (countMaxLayerStack.size() != 0) {
            Word temp = countMaxLayerStack.pop();
            if (temp.lexType == LexType.RPARENT) {

            }
//            array.add();
        }
        if (maxLayer == 0) {
            InverseExp(headPos_nextLayer, endPos_nextLayer);
        }
        else {
            for (int i = 1; i <= maxLayer; i++) {
                int lpCount = 0;
                int rpCount = 0;
                for (int j = head; j <= rear; j++) {
                    if (words.get(j).lexType == LexType.LPARENT) {
                        if (words.get(j - 1).lexType == LexType.IDENFR) {
                            j = jumpParents(j);
                            continue;
                        }
                        if (lpCount < i) {
                            lpCount++;
                            if (lpCount == i) {
                                headPos_nextLayer = j + 1;
//                            System.out.println("第 " + i + " 个左括号");
//                            System.out.println("lpIndex = " + j);
                            }
                        }
                    }
                    else if (words.get(j).lexType == LexType.RPARENT && rpCount < maxLayer + 1 - i) {
                        rpCount++;
                        if (rpCount == maxLayer + 1 - i) {
                            endPos_nextLayer = j - 1;
//                            System.out.println("第 " + (count + 1 - i) + " 个右括号");
//                            System.out.println("rpIndex = " + j);
                        }
                    }
                    if (lpCount == i && rpCount == maxLayer + 1 - i) {
//                        System.out.println("[[[[[[[[]]]]]]]]head : " + expHeadPos);
//                        System.out.println("[[[[[[[[]]]]end : " + expEndPos);

//                        for (int pp = expHeadPos; pp <= expEndPos; pp++) {
//                            Word te = words.get(pp);
//                            System.out.print(te.lexType + " ");
//                            if (te.word == null) {
//                                System.out.println(te.number);
//                            }
//                            else {
//                                System.out.println(te.word);
//                            }
//                        }
                        InverseExp(headPos_nextLayer, endPos_nextLayer);
                        // 理论上再用到这哥俩，就是下一层次了，那时候应该已经有新生成的pos了，所以置空吧
                        headPos_nextLayer = null;
                        endPos_nextLayer = null;
//                        System.out.println("-----------------------------------------------------then------------------------------------------------------");
//                                                for (int pp = curPos; pp <= rear; pp++) {
//                                                    Word te = words.get(pp);
//                                                    System.out.print(te.lexType + " ");
//                                                    if (te.word == null) {
//                                                        System.out.println(te.number);
//                                                    }
//                                                    else {
//                                                        System.out.println(te.word);
//                                                    }
//                                                }
                        break;
                    }
                }
            }
        }
    }
    private void dealManyParent (int headPos, int endPos) {
        Integer head_parent, end_parent;
        for (int f = headPos; f <= endPos; f++) {
            if (f < words.size() && words.get(f).lexType == LexType.LPARENT) {
                if (words.get(f - 1).lexType == LexType.IDENFR) {
                    f = jumpParents(f);
                    continue;
                }
                head_parent = f + 1; // ( + 1
                f = jumpParents(f);
                end_parent = f - 1; // ) - 1
                dealParent(head_parent, end_parent);
            }
        }
    }
    private void dealOneLayerFunc(int head, int rear) {
        // head: funcIdent.index + 2 // (.index + 1
        // rear: ).index - 1
        Stack<Integer> posStack = new Stack<Integer>(); // 参数index
        posStack.push(head);
        for (int i = head; i <= rear; i++) {
            if (i < words.size() && words.get(i).lexType == LexType.IDENFR) {
                if (words.get(i + 1).lexType == LexType.LBRACK) {
                    i++;
                    i = jumpXBRACK(i);
                    continue;
                }
                else if (words.get(i + 1).lexType == LexType.LPARENT) {
                    i++;
                    i = jumpParents(i);
                    continue;
                }
            }
            if (i < words.size() && words.get(i).lexType == LexType.COMMA) {
                posStack.push(i - 1); // , - 1
                posStack.push(i + 1); // , + 1
            }
        }
        posStack.push(rear);
        for (int i = 0; posStack.size() > 0; i++) {
            int endPos = posStack.pop();
            int headPos = posStack.pop();
            if (headPos == endPos) continue;
            dealManyParent(headPos, endPos);
        }
    }
    private void dealFuncRParams(int head, int rear) { // TODO：函数嵌套。。。忽略数组下标问题
        // head: funcIdent.index + 2 // (.index + 1
        // rear: ).index - 1
        Integer headPos_nextLayer = head, endPos_nextLayer = rear;
        Stack<Integer> posStack = new Stack<Integer>(); // 参数index
        posStack.push(head);
        for (int i = head; i <= rear; i++) {
            if (i < words.size() && words.get(i).lexType == LexType.IDENFR) {
                if (words.get(i + 1).lexType == LexType.LBRACK) {
                    i++;
                    i = jumpXBRACK(i);
                    continue;
                }
            }
            if (i < words.size() && words.get(i).lexType == LexType.COMMA) {
                posStack.push(i - 1); // , - 1
                posStack.push(i + 1); // , + 1
            }
        }
        posStack.push(rear);
        for (int i = 0; posStack.size() > 0; i++) {
            int endPos_param = posStack.pop();
            int headPos_param = posStack.pop();
            if (headPos_param == endPos_param) {
                continue;
            }
            int maxLayer = 1;
            Stack<Word> countMaxLayerStack = new Stack<Word>();
            for (int j = headPos_param; j <= endPos_param; j++) {
                if (words.get(j).lexType == LexType.IDENFR && words.get(j + 1).lexType == LexType.LPARENT) {
                    countMaxLayerStack.push(words.get(j));
                    j++;
                }
                if (words.get(j).lexType == LexType.LPARENT) {
                    countMaxLayerStack.push(words.get(j));
                }
                else if (words.get(j).lexType == LexType.RPARENT) {
                    Word temp = countMaxLayerStack.pop();
                    if(temp.lexType != LexType.LPARENT) {
                        countMaxLayerStack.push(temp);
                    }
                }
            }
            ArrayList<Word> array = new ArrayList<Word>();
            while (countMaxLayerStack.size() != 0) {
                array.add(countMaxLayerStack.pop());
            }
            for (int q = 0; q < array.size(); q++) {
                if (array.get(q).lexType == LexType.IDENFR) {
                    if (q+1 < array.size() && array.get(q+1).lexType == LexType.LPARENT){
                        maxLayer++;
                        q++;
                    }
                }
                if (array.get(q).lexType == LexType.LPARENT) {
                    while(array.get(q).lexType == LexType.LPARENT) {
                        q++;
                    }
                    q--;
                }
            }
            for (int k = maxLayer; k >= 1; k--) { // 从最内层开始处理，因为单层处理忽略内层的函数
                for (int j = headPos_param; j <= endPos_param; j++) {
                    int count = 0;
                    if (words.get(j).lexType == LexType.IDENFR) {
                        if (words.get(j + 1).lexType == LexType.LBRACK) {
                            j++;
                            j = jumpXBRACK(j);
                        }
                        else if (words.get(j + 1).lexType == LexType.LPARENT && count < k) {
                            count++;
                        }
                    }
                    if (count == k) {
                        headPos_nextLayer = j + 2; // fun (.index + 1
                        endPos_nextLayer = jumpParents(j) - 1; // ).index - 1
                        dealOneLayerFunc(headPos_nextLayer, endPos_nextLayer);
                        break;
                    }
                }
            }
            dealParent(headPos_param, endPos_param);
        }
    }
    private void dealArray(Integer headPos, Integer rear) {
        //TODO: 没写呢
        for (int i = headPos; i <= rear; i++) {
            if (i < words.size() && words.get(i).lexType == LexType.IDENFR) {
                i++;
                if (i < words.size() && words.get(i).lexType == LexType.LBRACK) {
                    i = jumpXBRACK(i);
                }
            }
        }
    }
    private void shitExp(){
        int rear = 0;
        if (isInversed(curPos)) {
        }
        else {
            ExpNeedInverse = false;
            rear = InverseExp(curPos, null); // 简单倒置，主要为了获取rear
            if (curPos == rear){
                // 单项表达式直接开摆
            }
            else {
                inversedExpIntervals.add(new ExpInterval(curPos, rear));
                dealManyParent(curPos, rear);
                int head, end;
                for (int i = curPos; i < rear; i++) {
                    if (words.get(i).lexType == LexType.IDENFR && words.get(i+1).lexType == LexType.LPARENT) {
                        head = i + 2;
                        i++;
                        i = jumpParents(i);
                        end = i - 1;
//                        System.out.println("=--------------=");
//                        for (int pp = head; pp <= end; pp++) {
//                            Word te = words.get(pp);
//                            System.out.print(te.lexType + " ");
//                            if (te.word == null) {
//                                System.out.println(te.number);
//                            } else {
//                                System.out.println(te.word);
//                            }
//                        }
                        dealFuncRParams(head, end);
//                        System.out.println("then----------");
//                        for (int pp = head; pp <= end; pp++) {
//                            Word te = words.get(pp);
//                            System.out.print(te.lexType + " ");
//                            if (te.word == null) {
//                                System.out.println(te.number);
//                            } else {
//                                System.out.println(te.word);
//                            }
//                        }
                    }
                }
//                dealArray(curPos, rear);
            }
            int triger = 0;
//            triger++;
            if (triger > 0) {
            }
        }
    }
    private Exp Exp () {
        AddExp addExp = AddExp();
        return new Exp(addExp);
    }
    private Cond Cond () {
        LOrExp lOrExp = LOrExp();
        return new Cond(lOrExp);
    }
    private LVal LVal () {
        Word ident = null;
        if (words.get(curPos).lexType == LexType.IDENFR) {
            ident = words.get(curPos);
//            if (!rootTable.checkInThisAndChildTable(ident.word, layer)) {
//                ErrorHandler.getErrorHandler().handleError(ErrorType.c, ident);
//            }
            if (curSymbolTable.searchSymbol(ident.word) == null) {
                ErrorHandler.getErrorHandler().handleError(ErrorType.c, ident);
            }
            curPos++;
            ArrayList<Word> LBRACKs = new ArrayList<Word>();
            ArrayList<Exp> exps = new ArrayList<Exp>();
            ArrayList<Word> RBRACKs = new ArrayList<Word>();
            while (words.get(curPos).lexType == LexType.LBRACK) {
                LBRACKs.add(words.get(curPos));
                curPos++;
                Exp exp = Exp();
                exps.add(exp);
                RBRACKs.add(getWord(LexType.RBRACK));
            }
            return new LVal(ident, LBRACKs, exps, RBRACKs);
        }
        else {
            //error
        }
        return null;
    }
    private PrimaryExp PrimaryExp () {
        Word LPARENT = null;
        Word RPARENT = null;
        if (words.get(curPos).lexType == LexType.LPARENT) {
            LPARENT = words.get(curPos);
            curPos++;
            Exp exp = Exp();
            RPARENT = getWord(LexType.RPARENT);
            return new PrimaryExp(LPARENT, exp, RPARENT);
        }
        else if (words.get(curPos).lexType == LexType.INTCON) {
            Number number = Number();
            return new PrimaryExp(number);
        }
        else {
            LVal lVal = LVal();
            return new PrimaryExp(lVal);
        }
    }
    private Number Number () {
//        int intConst = 0;
        Word word = null;
        if(words.get(curPos).lexType == LexType.INTCON) {
            word = words.get(curPos);
            curPos++;
        }
        else {
            //error
        }
        return new Number(word);
    }
    private UnaryExp UnaryExp () {
        if (words.get(curPos).lexType == LexType.IDENFR && words.get(curPos + 1).lexType == LexType.LPARENT) {
            Word ident = words.get(curPos);
//            if (!rootTable.checkInThisAndChildTable(ident.word, layer)) {
//                ErrorHandler.getErrorHandler().handleError(ErrorType.c, ident);
//            }
            if (curSymbolTable.searchSymbol(ident.word) == null) {
                ErrorHandler.getErrorHandler().handleError(ErrorType.c, ident);
            }
            curPos++;
            Word LPARENT = null;
            Word RPARENT = null;
            if (words.get(curPos).lexType == LexType.LPARENT) {
                int lparentPos = curPos;
                LPARENT = words.get(curPos);
                curPos++;
                FuncRParams funcRParams = null;
                if (words.get(curPos).lexType != LexType.RPARENT && words.get(curPos).lexType != LexType.SEMICN) {
                    funcRParams = FuncRParams();
//                    ArrayList<Symbol> fParams = rootTable.searchSymbol(ident.word, 0).params;
                    SymbolTable_v2 root = curSymbolTable;
                    while (root.getPreTable() != null) {
                        root = root.getPreTable();
                    }
                    ArrayList<Symbol_v2> fParams = root.searchSymbol(ident.word).params;
//                    System.out.println("----------"+words.get(curPos-1).number+words.get(curPos).word+words.get(curPos+1).word);
                    ArrayList<Exp> exps = funcRParams.getExps();
                    if (exps.size() != fParams.size()) {
                        ErrorHandler.getErrorHandler().handleError(ErrorType.d, ident);
                    }
                    else {
                        for (int i = 0; i < exps.size(); i++) {
                            if (exps.get(i).checkContent()) {
                                // 运算式
                                if (fParams.get(i).dimension != 0) {
                                    ErrorHandler.getErrorHandler().handleError(ErrorType.e, ident);
                                }
                            }
                            else {
                                Word expsContent = exps.get(i).getContent(); // number or ident
                                if (expsContent.lexType == LexType.INTCON) {
                                    if (fParams.get(i).dimension != 0) {
                                        ErrorHandler.getErrorHandler().handleError(ErrorType.e, ident);
                                    }
                                }
                                else {
//                                    Symbol res = rootTable.searchSymbol(expsContent.word, layer);
                                    Symbol_v2 res = curSymbolTable.searchSymbol(expsContent.word);
                                    if (res != null) {
                                        int temp = 0;
                                        // j = ).index - 1
                                        int pretendStack = 1;
                                        for (int j = curPos - 1; j > lparentPos && pretendStack != 0; j--) {
                                            if (words.get(j).lexType == LexType.RPARENT) {
                                                pretendStack++;
                                            }
                                            else if (words.get(j).lexType == LexType.LPARENT) {
                                                pretendStack--;
                                            }
                                            else if (words.get(j).lexType == LexType.LBRACK) {
                                                temp++;
                                            }
                                        }
                                        int rParamDimension = res.dimension - temp;
                                        if (rParamDimension != fParams.get(i).dimension) {
//                                            System.out.println(ident.word);
                                            ErrorHandler.getErrorHandler().handleError(ErrorType.e, ident);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                RPARENT = getWord(LexType.RPARENT);
                return new UnaryExp(ident, LPARENT, funcRParams, RPARENT);
            }
            else {
                //error
            }
        }
        else if (words.get(curPos).lexType == LexType.PLUS || words.get(curPos).lexType == LexType.MINU || words.get(curPos).lexType == LexType.NOT) {
            UnaryOp unaryOp = UnaryOp();
            UnaryExp unaryExp = UnaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        }
        else {
            PrimaryExp primaryExp = PrimaryExp();
            return new UnaryExp(primaryExp);
        }
        return null;
    }
    private UnaryOp UnaryOp () {
        Word operator = words.get(curPos);
        curPos++;
        return new UnaryOp(operator);
    }
    private FuncRParams FuncRParams () {
        ArrayList<Exp> exps = new ArrayList<Exp>();
        Exp exp = Exp();
        exps.add(exp);
        ArrayList<Word> COMMAs = new ArrayList<Word>();
        while(words.get(curPos).lexType == LexType.COMMA) {
            COMMAs.add(words.get(curPos));
            curPos++;
            exp = Exp();
            exps.add(exp);
        }
        return new FuncRParams(exps, COMMAs);
    }
    private MulExp MulExp () {
        UnaryExp unaryExp = UnaryExp();
        if(words.get(curPos).lexType == LexType.MULT || words.get(curPos).lexType == LexType.DIV || words.get(curPos).lexType == LexType.MOD) {
            Word operator = words.get(curPos);
            curPos++;
            MulExp mulExp = MulExp();
            return new MulExp(mulExp, operator, unaryExp);
        }
        else {
            return new MulExp(unaryExp);
        }
//        if(words.get(curPos + 1).lexType == LexType.MULT || words.get(curPos + 1).lexType == LexType.DIV || words.get(curPos + 1).lexType == LexType.MOD) {
//            MulExp mulExp = MulExp();
//            Word operator = words.get(curPos);
//            curPos++;
//            UnaryExp unaryExp = UnaryExp();
//            return new MulExp(mulExp, operator, unaryExp);
//        }
//        else  {
//            UnaryExp unaryExp = UnaryExp();
//            return new MulExp(unaryExp);
//        }
    }
    private AddExp AddExp () {
//        shitExp();
        MulExp mulExp = MulExp();
        if(words.get(curPos).lexType == LexType.PLUS || words.get(curPos).lexType == LexType.MINU) {
            Word operator = words.get(curPos);
            curPos++;
            AddExp addExp = AddExp();
            return new AddExp(addExp, operator, mulExp);
        }
        else {
            return new AddExp(mulExp);
        }
    }
    private RelExp RelExp () {
        AddExp addExp = AddExp();
        if(words.get(curPos).lexType == LexType.LSS || words.get(curPos).lexType == LexType.GRE || words.get(curPos).lexType == LexType.LEQ || words.get(curPos).lexType == LexType.GEQ) {
            Word operator = words.get(curPos);
            curPos++;
            RelExp relExp = RelExp();
            return new RelExp(relExp, operator, addExp);
        }
        else {
            return new RelExp(addExp);
        }
//        if(words.get(curPos + 1).lexType == LexType.LSS || words.get(curPos + 1).lexType == LexType.GRE || words.get(curPos + 1).lexType == LexType.LEQ || words.get(curPos + 1).lexType == LexType.GEQ) {
//            RelExp relExp = RelExp();
//            Word operator = words.get(curPos);
//            curPos++;
//            AddExp addExp = AddExp();
//            return new RelExp(relExp, operator, addExp);
//        }
//        else  {
//            AddExp addExp = AddExp();
//            return new RelExp(addExp);
//        }
    }
    private EqExp EqExp () {
        RelExp relExp = RelExp();
        if(words.get(curPos).lexType == LexType.EQL || words.get(curPos).lexType == LexType.NEQ) {
            Word operator = words.get(curPos);
            curPos++;
            EqExp eqExp = EqExp();
            return new EqExp(eqExp, operator, relExp);
        }
        else {
            return new EqExp(relExp);
        }
//        if(words.get(curPos + 1).lexType == LexType.EQL || words.get(curPos + 1).lexType == LexType.NEQ) {
//            EqExp eqExp = EqExp();
//            String operator = words.get(curPos).word;
//            curPos++;
//            RelExp relExp = RelExp();
//            return new EqExp(eqExp, operator, relExp);
//        }
//        else  {
//            RelExp relExp = RelExp();
//            return new EqExp(relExp);
//        }
    }
    private LAndExp LAndExp () {
        EqExp eqExp = EqExp();
        if(words.get(curPos).lexType == LexType.AND) {
            Word operator = words.get(curPos);
            curPos++;
            LAndExp lAndExp = LAndExp();
            return new LAndExp(lAndExp, operator, eqExp);
        }
        else {
            return new LAndExp(eqExp);
        }
//        if(words.get(curPos + 1).lexType == LexType.AND) {
//            LAndExp lAndExp = LAndExp();
//            Word operator = words.get(curPos);
//            curPos++;
//            EqExp eqExp = EqExp();
//            return new LAndExp(lAndExp, operator, eqExp);
//        }
//        else  {
//            EqExp eqExp = EqExp();
//            return new LAndExp(eqExp);
//        }
    }
    private LOrExp LOrExp () {
        LAndExp lAndExp = LAndExp();
        if(words.get(curPos).lexType == LexType.OR) {
            Word operator = words.get(curPos);
            curPos++;
            LOrExp lOrExp = LOrExp();
            return new LOrExp(lOrExp, operator, lAndExp);
        }
        else {
            return new LOrExp(lAndExp);
        }
//        if(words.get(curPos + 1).lexType == LexType.OR) {
//            LOrExp lOrExp = LOrExp();
//            Word operator = words.get(curPos);
//            curPos++;
//            LAndExp lAndExp = LAndExp();
//            return new LOrExp(lOrExp, operator, lAndExp);
//        }
//        else  {
//            LAndExp lAndExp = LAndExp();
//            return new LOrExp(lAndExp);
//        }
    }
    private ConstExp ConstExp () {
        AddExp addExp = AddExp();
        return new ConstExp(addExp);
    }
}
