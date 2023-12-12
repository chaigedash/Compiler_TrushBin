package LLVM;

import LLVM.Instruction.BrInstruction;
import LLVM.Instruction.GetElementPtrInstruction;
import LLVM.Instruction.IcmpInstruction;
import SymbolTable_v2.*;
import Lexer.LexType;
import Lexer.Word;
import Parser.Node.*;
import Parser.Node.Number;

import java.util.ArrayList;
import java.util.Stack;

public class LLVMAnalyzer {
    private static final LLVMAnalyzer INSTANCE = new LLVMAnalyzer();
    public static LLVMAnalyzer getInstance () {
        return INSTANCE;
    }
    private CompUnit AST;
    private SymbolTable_v2 curSymbolTable;
    private ArrayList<Symbol_v2> temp_Params;
    public void setCompUnit (CompUnit AST) {
        this.AST = AST;
    }
    public void analyzeAST () {
        visitCompUnit(AST);
    }
    public void print() {
//        curBasicBlock.print();
        Module.getInstance().print();
    }
    private boolean isGlobalDecl;
    private boolean isConstExp;
    private boolean isWaitingForLVal;
    private Pointer lValPointer;
    private Stack<Integer> constValueStack = new Stack<Integer>(); // global和const的计算属性全都存这里
    private Stack<Value> valueStack = new Stack<Value>();
    private Module curModule;
    private Function curFunction;
    private BasicBlock curBasicBlock;
    private Builder builder;
    //    private Stack<BrInstruction> branches_lackFalseLabel = new Stack<BrInstruction>();
//    private Stack<BrInstruction> branches_lackTrueLabel = new Stack<BrInstruction>();
//    private Stack<BrInstruction> branches_ifBlock = new Stack<BrInstruction>();
//    private Stack<BrInstruction> branches_elseBlock = new Stack<BrInstruction>();
    private BrHandler curBrHandler = null;
//    private BasicBlock thenBlock, nextBlock;

    //    private void gotoNewBasicBlock() {
//        if (curBasicBlock != null)
//            basicBlockStack.push(curBasicBlock);
//        curBasicBlock = builder.buildBasicBlock(curFunction, curFunction.giveName());
//    }
//    private void backPreBasicBlock() {
//        if (basicBlockStack.size() > 0)
//            curBasicBlock = basicBlockStack.pop();
//    }
    private void newBrHandler (BrHandler.Type type) {
        if (type == BrHandler.Type.ifBlock) {
            curBrHandler = new ifBlockBrHandler(curBrHandler);
        }
        else {
            curBrHandler = new forBlockBrHandler(curBrHandler);
        }
    }
    private forBlockBrHandler findRecentForBrHandler() {
        BrHandler bh = curBrHandler;
        while (bh != null) {
            if (bh instanceof forBlockBrHandler) {
                return (forBlockBrHandler) bh;
            }
            bh = bh.preBrHandler;
        }
        return null;
    }
    private ArrayList<Value> inverseList (ArrayList<Value> list) {
        for (int i = 0; i < list.size()/2; i++) {
            Value temp = list.get(i);
            list.set(i, list.get(list.size() - 1 - i));
            list.set(list.size() - 1 - i, temp);
        }
        return list;
    }
    private int calculate (Word op, int a, int b) {
        switch (op.lexType) {
            case PLUS :
                return a + b;
            case MINU :
                return a - b;
            case MULT:
                return a * b;
            case DIV :
                return a / b;
            case MOD :
                return a % b;
            default: break;
        }
        return 0;
    }
    private ArrayList<Integer> countStrPlaceholder (String str) {
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (int i = 1; i < str.length() - 2; i++) {
            if (str.charAt(i) == '%' && str.charAt(i + 1) == 'd') {
                indexes.add(i);
            }
        }
        return indexes;
    }
    private void init() {
        isConstExp = false;
        isGlobalDecl = false;
        lValPointer = null;
        isWaitingForLVal = false;
//        isReturned = false;
        builder = new Builder();
        curModule = Module.getInstance();
        curSymbolTable = new SymbolTable_v2();
        Symbol_v2 getint = new Symbol_v2("getint", Symbol_v2.Type.function, Value.Type._void);
        Symbol_v2 putint = new Symbol_v2("putint", Symbol_v2.Type.function, Value.Type._void);
        Symbol_v2 putch = new Symbol_v2("putch", Symbol_v2.Type.function, Value.Type._void);
        Symbol_v2 putstr = new Symbol_v2("putstr", Symbol_v2.Type.function, Value.Type._void);
        putint.params.add(new Symbol_v2("", Symbol_v2.Type.param, Value.Type._i32));
        putch.params.add(new Symbol_v2("", Symbol_v2.Type.param, Value.Type._i32));
        putstr.params.add(new Symbol_v2("", Symbol_v2.Type.param, Value.Type._i8));
        // FIXME : putstr参数不对，感觉涉及数组
        curSymbolTable.addSymbol(getint);
        curSymbolTable.addSymbol(putint);
        curSymbolTable.addSymbol(putch);
        curSymbolTable.addSymbol(putstr);
        builder.buildFunction(curModule, "getint", Value.Type._i32);
        builder.buildFunction(curModule, "putint", Value.Type._void);
        builder.buildFunction(curModule, "putch", Value.Type._void);
        builder.buildFunction(curModule, "putstr", Value.Type._void);

    }
    private void visitCompUnit (CompUnit compUnit) {
        init();
        ArrayList<Decl> decls = compUnit.getDecls();
        ArrayList<FuncDef> funcDefs = compUnit.getFuncDefs();
        MainFuncDef mainFuncDef = compUnit.getMainFuncDef();
        isGlobalDecl = true;
        for (Decl decl : decls) {
            visitDecl(decl);
        }
        isGlobalDecl = false;
        for (FuncDef funcDef : funcDefs) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(mainFuncDef);
    }
    private void visitDecl (Decl decl) {
        ConstDecl constDecl = decl.getConstDecl();
        VarDecl varDecl = decl.getVarDecl();
        if (constDecl != null) {
            visitConstDecl(constDecl);
        }
        else if (varDecl != null) {
            visitVarDecl(varDecl);
        }
    }
    private void visitConstDecl (ConstDecl constDecl) {
        ArrayList<ConstDef> constDefs = constDecl.getConstDefs();
        for (ConstDef constDef : constDefs) {
            visitConstDef(constDef);
        }
    }
    private void visitConstDef (ConstDef constDef) {
        Word ident = constDef.getIdent();
        Symbol_v2 symbol = new Symbol_v2(ident.word, Symbol_v2.Type.constVar);
        curSymbolTable.addSymbol(symbol);
        ArrayList<ConstExp> constExps = constDef.getConstExps();
        ConstInitVal constInitVal = constDef.getConstInitVal();
        Integer dimension1 = null, dimension2 = null;
        switch (constExps.size()) {
            case 0 : break;
            case 1 : {
                visitConstExp(constExps.get(0));
                dimension1 = constValueStack.pop();
                break;
            }
            case 2 : {
                visitConstExp(constExps.get(0));
                dimension1 = constValueStack.pop();
                visitConstExp(constExps.get(1));
                dimension2 = constValueStack.pop();
                break;
            }
        }
//        visitConstInitVal(constInitVal);
        ArrayList<ConstExp> initExps = new ArrayList<ConstExp>();
        if (constInitVal != null) {
            if (constInitVal.getLBRACE() != null) { // array
                if (constInitVal.getConstInitVals().get(0).getLBRACE() != null) { // dimension = 2
                    // DFS
                    for (ConstInitVal constInitVal1 : constInitVal.getConstInitVals()) {
                        for (ConstInitVal constInitVal2 : constInitVal1.getConstInitVals()) {
                            initExps.add(constInitVal2.getConstExp());
                        }
                    }
                }
                else { // dimension = 1
                    for (ConstInitVal constInitVal1 : constInitVal.getConstInitVals()) {
                        initExps.add(constInitVal1.getConstExp());
                    }
                }
            }
            else { // dimension = 0
                initExps.add(constInitVal.getConstExp());
            }
        }
        // n dimension -> 1 dimension
        ArrayList<Integer> initRes = new ArrayList<Integer>();
        boolean isInitAllZero = true;
        for (ConstExp initExp : initExps) {
            visitConstExp(initExp);
            Integer newInitRes = constValueStack.pop();
            initRes.add(newInitRes);
            if (newInitRes != 0) {
                isInitAllZero = false;
            }
        }
        if (isGlobalDecl) {
            if (isInitAllZero) {
                builder.buildGlobal(curModule, ident.word, dimension1, dimension2, true);
            }
            else {
                builder.buildGlobal(curModule, ident.word, initRes, dimension1, dimension2, true);
            }
            symbol.setGlobal();
            symbol.setConstArray(initRes, dimension1, dimension2);
        }
        else {
            // FIXME: 局部常量: 目前和局部变量做的是一模一样的，感觉要出锅
            // 他妈的，真的出锅了，但是是因为忘记把寄存器加进符号表了
            // 又出锅了, 但这次纯傻逼，二维转一维下标的表达式我写的行×行数+列。。。
            Pointer mem = new Pointer(curFunction.newIdent(), Value.Type._i32);
            if (dimension1 != null) {
                builder.buildAllocaInstruction(curBasicBlock, mem, dimension1, dimension2, false);
                if (dimension2 != null) {
                    // dimension: 2
                    for (int i = 0; i < dimension1 * dimension2; i += dimension2) {
                        for(int j = i; j < i + dimension2; j++) {
                            Pointer res = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, res, mem, dimension1, dimension2, new Constant(i/dimension2), new Constant(j%dimension2));
                            if (res == null) System.out.println("原来是你小子");
                            builder.buildStoreInstruction(curBasicBlock, new Constant(initRes.get(j)), res);
                        }
                    }
                }
                else {
                    // dimension: 1
                    for (int i = 0; i < dimension1; i++) {
                        Pointer res = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, res, mem, dimension1, dimension2, new Constant(i), null);
                        if (res == null) System.out.println("你小子");
                        builder.buildStoreInstruction(curBasicBlock, new Constant(initRes.get(i)), res);
                    }
                }
            }
            else {
                // dimension: 0
                builder.buildAllocaInstruction(curBasicBlock, mem);
                if (mem == null) System.out.println("原来是你小子");
                builder.buildStoreInstruction(curBasicBlock, new Constant(initRes.get(0)), mem);
            }
            symbol.isConst = true;
            symbol.setConstArray(initRes, dimension1, dimension2);
            symbol.setPointer(mem);
        }
    }
    //    private void visitConstInitVal (ConstInitVal constInitVal) {
//        ConstExp constExp = constInitVal.getConstExp();
//        ArrayList<ConstInitVal> constInitVals = constInitVal.getConstInitVals();
//        if (constExp != null) {
//            visitConstExp(constExp);
//        }
//        else {
//            for (ConstInitVal constInitVal1 : constInitVals) {
//                visitConstInitVal(constInitVal1);
//            }
//        }
//    }
    private void visitVarDecl (VarDecl varDecl) {
        ArrayList<VarDef> varDefs = varDecl.getVarDefs();
        for (VarDef varDef : varDefs) {
            visitVarDef(varDef);
        }
    }
    private void visitVarDef (VarDef varDef) {
        Word ident = varDef.getIdent();
        Symbol_v2 symbol = new Symbol_v2(ident.word, Symbol_v2.Type.val);
        curSymbolTable.addSymbol(symbol);
        ArrayList<ConstExp> constExps = varDef.getConstExps();
        InitVal initVal = varDef.getInitVal();
        Integer dimension1 = null, dimension2 = null;
        switch (constExps.size()) {
            case 0 : break;
            case 1 : {
                visitConstExp(constExps.get(0));
                dimension1 = constValueStack.pop();
                break;
            }
            case 2 : {
                visitConstExp(constExps.get(0));
                dimension1 = constValueStack.pop();
                visitConstExp(constExps.get(1));
                dimension2 = constValueStack.pop();
                break;
            }
        }
        ArrayList<Exp> initExps = new ArrayList<Exp>();
        if (initVal != null) {
            if (initVal.getLBRACE() != null) { // array
                if (initVal.getInitVals().get(0).getLBRACE() != null) { // dimension = 2
                    // DFS
                    for (InitVal initVal1 : initVal.getInitVals()) {
                        for (InitVal initVal2 : initVal1.getInitVals()) {
                            initExps.add(initVal2.getExp());
                        }
                    }
                }
                else { // dimension = 1
                    for (InitVal initVal1 : initVal.getInitVals()) {
                        initExps.add(initVal1.getExp());
                    }
                }
            }
            else { // dimension = 0
                initExps.add(initVal.getExp());
            }
        }
        // n dimension -> 1 dimension
        if (isGlobalDecl) {
            if (initExps.size() > 0) {
                ArrayList<Integer> initRes = new ArrayList<Integer>();
                boolean isInitAllZero = true;
                for (Exp initExp : initExps) {
                    visitExp(initExp);
                    Integer newInitRes = constValueStack.pop();
                    initRes.add(newInitRes);
                    if (newInitRes != 0) {
                        isInitAllZero = false;
                    }
                }
                if (isInitAllZero) {
                    builder.buildGlobal(curModule, ident.word, dimension1, dimension2, false);
                }
                else {
                    builder.buildGlobal(curModule, ident.word, initRes, dimension1, dimension2, false);
                }
                symbol.setGlobal();
                symbol.setConstArray(initRes, dimension1, dimension2);
            }
            else {
                builder.buildGlobal(curModule, ident.word, dimension1, dimension2, false);
                symbol.setGlobal();
                symbol.setArray(null, dimension1, dimension2); // 相当于初始化全0
            }
        }
        else {
            //DONE : 局部变量还没做数组
            ArrayList<Value> initRes = new ArrayList<Value>();
            for (Exp initExp : initExps) {
                visitExp(initExp);
                Value newInitRes = valueStack.pop();
                // 假定这些初始值都是常量
                initRes.add(newInitRes);
            }
            Pointer mem = new Pointer(curFunction.newIdent(), Value.Type._i32);
            symbol.setPointer(mem);
            ArrayList<Pointer> pointers = new ArrayList<Pointer>();
            if (dimension1 != null) {
                builder.buildAllocaInstruction(curBasicBlock, mem, dimension1, dimension2, false);
                if (dimension2 != null) {
                    // dimension: 2
                    for (int i = 0; i < dimension1 * dimension2; i += dimension2) {
                        for(int j = i; j < i + dimension2; j++) {
                            Pointer res = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            pointers.add(res);
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, res, mem, dimension1, dimension2, new Constant(i/dimension2), new Constant(j%dimension2));
                            if (j < initRes.size())
                                builder.buildStoreInstruction(curBasicBlock, initRes.get(j), res);
                            else {
                                builder.buildStoreInstruction(curBasicBlock, new Constant(0), res);
                            }
                        }
                    }
                }
                else {
                    // dimension: 1
                    for (int i = 0; i < dimension1; i++) {
                        Pointer res = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        pointers.add(res);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, res, mem, dimension1, dimension2, new Constant(i), null);
                        if (i < initRes.size())
                            builder.buildStoreInstruction(curBasicBlock, initRes.get(i), res);
                        else {
                            builder.buildStoreInstruction(curBasicBlock, new Constant(0), res);
                        }
                    }
                }
            }
            else {
                // dimension: 0
                pointers.add(mem);
                builder.buildAllocaInstruction(curBasicBlock, mem);
                if (initRes.size() > 0)
                    builder.buildStoreInstruction(curBasicBlock, initRes.get(0), mem);
            }
            symbol.isConst = false;
//            System.out.println("pointers : " + pointers.size());
            symbol.setArray(pointers, dimension1, dimension2);
        }
        // DONE : 这个判断结构乱得逆天，有空改改
    }
    //    private void visitInitVal (InitVal initVal) {
//        Exp exp = initVal.getExp();
//        ArrayList<InitVal> initVals = initVal.getInitVals();
//        if (exp != null) {
//            visitExp(exp);
//        }
//        else {
//            for (InitVal initVal1 : initVals) {
//                visitInitVal(initVal1);
//            }
//        }
//    }
    private void visitFuncDef (FuncDef funcDef) {
        FuncType fType = funcDef.getFuncType();
        Word fName = funcDef.getIdent();
        FuncFParams funcFParams = funcDef.getFuncFParams();
        Block block = funcDef.getBlock();
        Value.Type returnType;
        if (fType.getFuncType().lexType == LexType.VOIDTK) {
            returnType = Value.Type._void;
        } else {
            returnType = Value.Type._i32;
        }
        String funcIdent = fName.word;
        curFunction = builder.buildFunction(curModule, funcIdent, returnType);
        if (funcFParams != null) {
            visitFuncFParams(funcFParams);
        }
        curBasicBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
        for (int i = 0; i < curFunction.arguments.size(); i++) { // 传参实例化
            // DONE: 这里下标都改成i就是正序，但是样例的答案都是逆序，为了方便对答案，暂时写的逆序，以后改回来
            Argument arg = curFunction.arguments.get(i);
//            Argument arg = curFunction.arguments.get(curFunction.arguments.size()-1-i);
            Pointer ptr = new Pointer(curFunction.newIdent(), Value.Type._i32);
            temp_Params.get(i).setPointer(ptr);
//            temp_Params.get(temp_Params.size()-1-i).setPointer(ptr);
//            Integer ttt = arg.col == null ? -1 : arg.col;
//            System.out.println("symbol : " + temp_Params.get(i).ident + " arg : " +arg.name + " col: " + ttt);
            builder.buildAllocaInstruction(curBasicBlock, ptr, arg.col, null, true);
            if (ptr == null) {
                System.out.println("凭什么");
            }
            builder.buildStoreInstruction(curBasicBlock, arg, ptr, arg.col);
        }
        curSymbolTable.addSymbol(new Symbol_v2(fName.word, Symbol_v2.Type.function, returnType)); // 函数加进符号表
//        curSymbolTable = curSymbolTable.createChildTable();
//        for (Symbol_v2 param : temp_Params) {
//            curSymbolTable.addSymbol(param);
//        }
//        temp_Params = null;
//        curSymbolTable = curSymbolTable.getPreTable();
        visitBlock(block);
//        if (!isReturned) {
        if (returnType == Value.Type._i32) builder.buildRetInstruction(curBasicBlock, new Constant(0));
        else builder.buildRetInstruction(curBasicBlock);
//        }
//        isReturned = false;
        curFunction = null;
    }
    private void visitMainFuncDef (MainFuncDef mainFuncDef) {
        Block block = mainFuncDef.getBlock();
        curFunction = builder.buildFunction(curModule, "main", Value.Type._i32);
        curBasicBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
//        System.out.println("main basicBlock: " + temp);
        curSymbolTable.addSymbol(new Symbol_v2("main", Symbol_v2.Type.function, Value.Type._i32)); // 函数加进符号表
//        curSymbolTable = curSymbolTable.createChildTable();
        visitBlock(block);
//        curSymbolTable = curSymbolTable.getPreTable();
//        if (!isReturned) {
        builder.buildRetInstruction(curBasicBlock);
//        }
//        isReturned = false;
    }
    private void visitFuncFParams (FuncFParams funcFParamList) {
        ArrayList<FuncFParam> funcFParams = funcFParamList.getFuncFParams();
        temp_Params = new ArrayList<Symbol_v2>();
        for (FuncFParam funcFParam : funcFParams) {
            visitFuncFParam(funcFParam);
        }
    }
    private void visitFuncFParam (FuncFParam funcFParam) {
        BType bType = funcFParam.getbType();
        Word ident = funcFParam.getIdent();
        ConstExp constExp = funcFParam.getConstExp();
        Value.Type type;
        Integer col = null;
        if (funcFParam.getLBRACK1() == null) {
            type = Value.Type._i32;
        } else {
            col = 0;
            type = Value.Type.Array;
        }
        if (constExp != null) {
            visitConstExp(constExp);
            col = constValueStack.pop();
        }
        Symbol_v2 paramSymbol = new Symbol_v2(ident.word, Symbol_v2.Type.param);
        //DONE: 设置列宽 ( col = null(d0) || 0(d1) || int(d2) )
//        paramSymbol.setFParam(col);
        paramSymbol.col_fParam = col;
//        curSymbolTable.addSymbol(paramSymbol);
        temp_Params.add(paramSymbol);
        String register = curFunction.newIdent();
//        if (col == null) {
//            System.out.println("hello " + curFunction.getIdent());
//        }
        builder.buildArgument(curFunction, register, type, ident.word, col);
//        Pointer pointer = new Pointer(register, Value.Type._i32);
//        paramSymbol.setPointer(pointer);
    }
    private void visitBlock (Block block) {
        ArrayList<BlockItem> blockItems = block.getBlockItems();

        curSymbolTable = curSymbolTable.createChildTable();
        if (temp_Params != null) {
            for (Symbol_v2 param : temp_Params) {
                curSymbolTable.addSymbol(param);
            }
        }
        temp_Params = null;
        for (BlockItem blockItem : blockItems) {
            visitBlockItem(blockItem);
        }
        curSymbolTable = curSymbolTable.getPreTable();
    }
    private void visitBlockItem (BlockItem blockItem) {
        Decl decl = blockItem.getDecl();
        Stmt stmt = blockItem.getStmt();
        if (decl != null) {
            visitDecl(decl);
        }
        else if (stmt != null) {
            visitStmt(stmt);
        }
    }
    private void visitStmt (Stmt stmt) {
        Stmt.StmtType stmtType = stmt.getStmtType();
        LVal lVal;
        Exp exp;
        ArrayList<Exp> exps = new ArrayList<Exp>();
        Block block;
        Cond cond;
        Stmt stmt1;
        Stmt stmt2;
        ForStmt forStmt1;
        ForStmt forStmt2;
        switch (stmtType) {
            case LValEqExp -> {
                lVal = stmt.getlVal();
                exp = stmt.getExp();
                isWaitingForLVal = true;
                visitLVal(lVal);
                Value lValValue = valueStack.pop();
                isWaitingForLVal = false;
                visitExp(exp);
                Value expResult = valueStack.pop();
                // DONE : 赋值没做，感觉还得看一遍指导书，应该还有落下的，但好困，晚安
                if (lValValue instanceof Global) {
                    builder.buildStoreInstruction(curBasicBlock, expResult, (Global) lValValue);
                } else {
                    if (lValPointer == null) System.out.println("原来是你小子");
                    builder.buildStoreInstruction(curBasicBlock, expResult, lValPointer);
                    lValPointer = null; // 用完就扔！
                }
            }
            case Exp -> {
                exp = stmt.getExp();
                if (exp != null) {
                    visitExp(exp);
                }
            }
            case Block -> {
                block = stmt.getBlock();
                visitBlock(block);
            }
            case ifStmt -> {
                cond = stmt.getCond();
                stmt1 = stmt.getStmt1();
                stmt2 = stmt.getStmt2();
//                BasicBlock ifBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
//                builder.buildBrInstruction(curBasicBlock, ifBlock);
//                curBasicBlock = ifBlock;
                // FIXME: 不知道为啥有上边三行，目测删了没啥影响,但是如果出锅了就加回来吧
//                System.out.println("if block : " + curBasicBlock.getIdent());
                newBrHandler(BrHandler.Type.ifBlock);
                visitCond(cond);
                BrInstruction br = null;
                while (((ifBlockBrHandler) curBrHandler).branches_ifBlock.size() != 0) {
                    br = ((ifBlockBrHandler) curBrHandler).branches_ifBlock.pop();
                    br.trueLabel = curBasicBlock;
                }
                visitStmt(stmt1); // if block
                if (stmt2 != null) { // else block
                    BasicBlock elseBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
                    BrInstruction bNext = builder.buildBrInstruction(curBasicBlock, null);
                    while (((ifBlockBrHandler) curBrHandler).branches_elseBlock.size() != 0) {
                        br = ((ifBlockBrHandler) curBrHandler).branches_elseBlock.pop();
                        if (br.cond != null) {
                            br.falseLabel = elseBlock;
                        }
                        else {
                            br.trueLabel = elseBlock;
                        }
                    }
                    curBasicBlock = elseBlock;
                    visitStmt(stmt2);
                    BasicBlock nextBasicBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
                    builder.buildBrInstruction(curBasicBlock, nextBasicBlock);
                    bNext.trueLabel = nextBasicBlock;
                    curBasicBlock = nextBasicBlock;
                } else {
                    BasicBlock nextBasicBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
                    builder.buildBrInstruction(curBasicBlock, nextBasicBlock);
                    while (((ifBlockBrHandler) curBrHandler).branches_elseBlock.size() != 0) {
                        br = ((ifBlockBrHandler) curBrHandler).branches_elseBlock.pop();
                        if (br.cond != null) {
                            br.falseLabel = nextBasicBlock;
                        }
                        else {
                            br.trueLabel = nextBasicBlock;
                        }
                    }
                    curBasicBlock = nextBasicBlock;
                }
                curBrHandler = curBrHandler.preBrHandler;
            }
            case forStmt -> {
//                System.out.println("for block : " + curBasicBlock.getIdent());
                forStmt1 = stmt.getForStmt1();
                cond = stmt.getCond();
                forStmt2 = stmt.getForStmt2();
                stmt1 = stmt.getStmt1();
                BasicBlock condBlock = null, cycleBlock = null, thenBlock = null, nextBlock = null;
                newBrHandler(BrHandler.Type.forBlock);
                if (forStmt1 != null) {
                    visitForStmt(forStmt1);
                }
                if (cond != null) {
                    condBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
                    builder.buildBrInstruction(curBasicBlock, condBlock);
                    curBasicBlock = condBlock;
                    newBrHandler(BrHandler.Type.ifBlock);
                    visitCond(cond);
                    cycleBlock = curBasicBlock;
                    // curBasicBlock : cond_next = cycleBlock
                    while (((ifBlockBrHandler) curBrHandler).branches_ifBlock.size() > 0) {
                        BrInstruction br_for_if = ((ifBlockBrHandler) curBrHandler).branches_ifBlock.pop();
                        br_for_if.trueLabel = curBasicBlock;
                    }
                    while (((ifBlockBrHandler) curBrHandler).branches_elseBlock.size() > 0) {
                        forBlockBrHandler recentFor = findRecentForBrHandler();
                        if (recentFor != null) {
                            BrInstruction lackElse = ((ifBlockBrHandler) curBrHandler).branches_elseBlock.pop();
//                            System.out.println("y");
                            recentFor.branches_nextBlock.push(lackElse);
                        } else {
                            System.out.println("error: no for block");
                        }
                    }
//                    System.out.println("--");
                    curBrHandler = curBrHandler.preBrHandler;
                }
                else {
                    cycleBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
                    builder.buildBrInstruction(curBasicBlock, cycleBlock);
                    curBasicBlock = cycleBlock; // curBasicBlock : cycleBlock
                }
                // in any case, curBasicBlock if cycleBlock
                visitStmt(stmt1); // 会产生一系列continue和break
//                DONE : cond --1-> stmt
                if (forStmt2 != null) {
                    thenBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
                    builder.buildBrInstruction(curBasicBlock, thenBlock);
                    curBasicBlock = thenBlock;
                    visitForStmt(forStmt2);
                    if (condBlock != null) {
                        builder.buildBrInstruction(curBasicBlock, condBlock);
                    } else {
                        builder.buildBrInstruction(curBasicBlock, cycleBlock);
                    }
                    while (((forBlockBrHandler) curBrHandler).branches_thenBlock.size() > 0) {
                        BrInstruction t = ((forBlockBrHandler) curBrHandler).branches_thenBlock.pop();
                        t.trueLabel = thenBlock;
                    }
//                    DONE : stmt -> forStmt2
                }
                else {
                    if (condBlock != null) {
                        builder.buildBrInstruction(curBasicBlock, condBlock);
                    }
                    else {
                        builder.buildBrInstruction(curBasicBlock, cycleBlock);
                    }
                    while (((forBlockBrHandler) curBrHandler).branches_thenBlock.size() > 0) {
                        BrInstruction t = ((forBlockBrHandler) curBrHandler).branches_thenBlock.pop();
                        if (condBlock != null) {
                            t.trueLabel = condBlock;
                        }
                        else {
                            t.trueLabel = cycleBlock;
                        }
                    }
                }
                nextBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
//                System.out.println(nextBlock.getIdent() + "----->" + ((forBlockBrHandler)curBrHandler).branches_nextBlock.size());
                if (curBasicBlock.getIdent().equals("%77")) {
                    System.out.println("next_________>" + ((forBlockBrHandler) curBrHandler).branches_nextBlock.size());
                }
                while (((forBlockBrHandler) curBrHandler).branches_nextBlock.size() > 0) {
                    BrInstruction br_for = ((forBlockBrHandler) curBrHandler).branches_nextBlock.pop();
                    if (br_for.cond == null) {
                        System.out.print("curBB is " + curBasicBlock.getIdent());
                        if (br_for.trueLabel != null) {
                            System.out.println("??????????????????????????");
                        }
                        br_for.trueLabel = nextBlock;
                        System.out.println("   ->    br_tL is " + br_for.trueLabel.getIdent());
                    } else {
                        br_for.falseLabel = nextBlock;
                    }
                }
                builder.buildBrInstruction(curBasicBlock, nextBlock);
                curBasicBlock = nextBlock;
                System.out.println("condBlock = " + (condBlock != null ? condBlock.getIdent() : "<null>") + " thenBlock = " + (thenBlock != null ? thenBlock.getIdent() : "<null>") + " nextBlock = " + nextBlock.getIdent());
                curBrHandler = curBrHandler.preBrHandler;
            }
            case breakStmt -> {
                BrInstruction breakBr = builder.buildBrInstruction(curBasicBlock, null);
                forBlockBrHandler recentFor_b = findRecentForBrHandler();
                recentFor_b.branches_nextBlock.push(breakBr);
            }
//                if (curBasicBlock.getIdent().equals("%116")) {
//                    System.out.println("hei");
//                    System.out.println(recentFor_b.branches_nextBlock.size());
//                }
            case continueStmt -> {
                BrInstruction continueBr = builder.buildBrInstruction(curBasicBlock, null);
                forBlockBrHandler recentFor_c = findRecentForBrHandler();
                recentFor_c.branches_thenBlock.push(continueBr);
            }
            case returnExp -> {
                exp = stmt.getExp();
                if (exp != null) {
                    visitExp(exp);
                    Value result = valueStack.pop();
                    builder.buildRetInstruction(curBasicBlock, result);
                } else {
                    builder.buildRetInstruction(curBasicBlock);
                }
            }
//                isReturned = true;
            case getint -> { // FIXME : 感觉逻辑有、怪
                lVal = stmt.getlVal();
                isWaitingForLVal = true;
                visitLVal(lVal);
                Value lVal_getint = valueStack.pop();
                isWaitingForLVal = false;
                Value getintReturn = new Value(curFunction.newIdent(), Value.Type._i32);
                Function function = curModule.getFunction("getint");
                builder.buildCallInstruction(curBasicBlock, getintReturn, function);
                if (lVal_getint instanceof Global) {
                    builder.buildStoreInstruction(curBasicBlock, getintReturn, (Global) lVal_getint);
                } else {
                    builder.buildStoreInstruction(curBasicBlock, getintReturn, lValPointer);
                    lValPointer = null; // 用完就扔！
                }
            }
            case print -> {
                String formatString = stmt.getFormatString().word;
                exps = stmt.getExps();
                for (Exp exp1 : exps) {
                    visitExp(exp1);
                }
                Function putint = curModule.getFunction("putint");
                Function putch = curModule.getFunction("putch");
//                Function putstr = curModule.getFunction("putstr"); // FIXME：暂时没用上，真怕它用上
                ArrayList<Integer> indexes = countStrPlaceholder(formatString);
                int indexesIndex = 0;
                Stack<Value> params = new Stack<Value>();
                for (int i = 0; i < indexes.size(); i++) {
                    Value param = valueStack.pop();
                    params.push(param);
                }
                for (int i = 1; i < formatString.length() - 1; i++) {
                    if (indexesIndex < indexes.size() && i == indexes.get(indexesIndex)) {
                        // call putint (no return, have params)
                        builder.buildCallInstruction(curBasicBlock, putint, params.pop());
                        indexesIndex++;
                        i++;
                    } else {
                        // call putch
                        Constant ch = null;
                        if (formatString.charAt(i) == '\\' && formatString.charAt(i + 1) == 'n') {
                            ch = new Constant('\n');
                            i++;
                        } else {
                            ch = new Constant(formatString.charAt(i));
                        }
                        builder.buildCallInstruction(curBasicBlock, putch, ch);
                    }
                }
            }
            default -> {
            }
        }
    }
    private void visitForStmt (ForStmt forStmt) {
        LVal lVal = forStmt.getlVal();
        Exp exp = forStmt.getExp();
        isWaitingForLVal = true;
        visitLVal(lVal);
        Value lValValue = valueStack.pop();
        isWaitingForLVal = false;
        visitExp(exp);
        Value expResult = valueStack.pop();
        if (lValValue instanceof Global) {
            builder.buildStoreInstruction(curBasicBlock, expResult, (Global)lValValue);
        }
        else {
            builder.buildStoreInstruction(curBasicBlock, expResult, lValPointer);
            lValPointer = null; // 用完就扔！
        }
    }
    private void visitExp (Exp exp) {
        AddExp addExp = exp.getAddExp();
        visitAddExp(addExp);
    }
    private void visitCond (Cond cond) {
        LOrExp lOrExp = cond.getlOrExp();
        visitLorExp(lOrExp);
    }
    private void visitLVal (LVal lVal) {
        Word ident = lVal.getIdent();
        ArrayList<Exp> exps = lVal.getExps();
        Value dimension1 = null, dimension2 = null;
        switch (exps.size()) {
            case 0 -> {
            }
            case 1 -> {
                visitExp(exps.get(0));
                dimension1 = valueStack.pop();
            }
            case 2 -> {
                visitExp(exps.get(0));
                dimension1 = valueStack.pop();
                visitExp(exps.get(1));
                dimension2 = valueStack.pop();
            }
        }
        Symbol_v2 symbol = curSymbolTable.searchSymbol(ident.word);
        if (symbol != null) {
            if (symbol.isGlobal) {
                Global globalLVal = curModule.getGlobal(symbol.ident);
                if (isGlobalDecl || isConstExp) { // 姑且认为全局定义时或者常量运算的数组index(x, y)也是Constant
                    Value x = dimension1 != null ? dimension1 : new Constant(0);
                    Value y = dimension2 != null ? dimension2 : new Constant(0);
//                    System.out.println("你在作妖？");
                    Integer arrayValue = symbol.getConstArrayValue(((Constant)x).value, ((Constant)y).value);
                    if (arrayValue == null) System.out.println("Error : load constArrayValue from symbol is null");
                    else constValueStack.push(arrayValue);
                }
                else { // DONE：没有做全局数组在局部取值
                    if (symbol.dimension1 != null && symbol.dimension2 == null) { // 被调用的数组是1维的
                        if (dimension1 != null) { // 产生的是一个实型
                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, globalLVal, symbol.dimension1, null, dimension1, null);
                            Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                            builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, resPtr);
                            valueStack.push(res);
                            if (isWaitingForLVal){
                                lValPointer = resPtr;
                            }
                        }
                        else { // 产生一个i32*
                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, globalLVal, symbol.dimension1, null, new Constant(0), null);
                            valueStack.push(resPtr);
                            if (isWaitingForLVal){
                                System.out.println("理论上不可能是左值赋值语句跳转过来的");
                            }
                        }
                    }
                    else if (symbol.dimension1 != null && symbol.dimension2 != null) { // 被调用的数组是2维的
                        if (dimension1 != null && dimension2 != null) { // 产生一个实型
                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, globalLVal, symbol.dimension1, symbol.dimension2, dimension1, dimension2);
                            Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                            builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, resPtr);
                            valueStack.push(res);
                            if (isWaitingForLVal){
                                lValPointer = resPtr;
                            }
                        }
                        else if (dimension1 != null && dimension2 == null) { // 产生一个i32 *
//                            Pointer headPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, headPtr, globalLVal, symbol.dimension1, symbol.dimension2, new Constant(0), null);
//                            Value off = new Value(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildMulInstruction(curBasicBlock, off, new Constant(symbol.dimension1), dimension1);
//                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, headPtr, symbol.dimension1, null, off, null);

//                            Pointer headPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, headPtr, globalLVal, symbol.dimension1, symbol.dimension2,  dimension1, null);
                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, globalLVal, symbol.dimension1, symbol.dimension2, new Constant(0), new Constant(0));

                            valueStack.push(resPtr);
                            if (isWaitingForLVal){
                                System.out.println("理论上不可能是左值赋值语句跳转过来的");
                            }
                        }
                        else { // 产生一个i32 **
                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            resPtr.col = symbol.dimension2;
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, globalLVal, symbol.dimension1, symbol.dimension2, new Constant(0), null);
                            valueStack.push(resPtr);
                            if (isWaitingForLVal){
                                System.out.println("理论上不可能是左值赋值语句跳转过来的");
                            }
                        }
                    }
                    else { // 被调用的全局变量是0维的
                        Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, globalLVal);
                        valueStack.push(res);
                        if (isWaitingForLVal){
                            lValPointer = globalLVal;
                        }
                    }
                }
            }
            else if (symbol.symbolType == Symbol_v2.Type.param) { // 函数参数
                if (symbol.col_fParam == null) { // 调用0维
                    Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                    builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, symbol.getPointer());
                    valueStack.push(res);
                    if (isWaitingForLVal) {
                        lValPointer = symbol.getPointer();
                    }
                }
                else if (symbol.col_fParam == 0) { // 调用1维
                    if (dimension1 == null) { // 调用传来的一维数组，整体传出去(i32*)
                        Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        if (symbol.getPointer() == null) {
                            System.out.println("我求求你错吧");
                        }
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, symbol.getPointer(), symbol.col_fParam, null, new Constant(0), null);
                        valueStack.push(resPtr);
                    }
                    else { // 实型
                        Pointer headPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, headPtr, Value.Type._i32, symbol.getPointer(), 0);
                        Pointer ptr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, ptr, headPtr, 0, dimension1);
                        Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, ptr);
                        valueStack.push(res);
                        if (isWaitingForLVal) {
                            lValPointer = ptr;
                        }
                    }
                }
                else { // 调用参数传来的二维数组( a[][n] )
                    if (dimension1 == null) { // a
                        // TODO : 有、没想好👇
                        Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        resPtr.col = symbol.col_fParam;
                        builder.buildLoadInstruction(curBasicBlock, resPtr, Value.Type._i32, symbol.getPointer(), symbol.col_fParam);
                        valueStack.push(resPtr);
                        if (isWaitingForLVal) System.out.println("理论上不可能是左值赋值语句跳转过来的");
                    }
                    else if (dimension1 != null && dimension2 == null) { // a[x]
                        Pointer headPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, headPtr, Value.Type._i32, symbol.getPointer(), symbol.col_fParam);
                        Pointer tempPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, tempPtr, headPtr, symbol.col_fParam, dimension1, null);
                        Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, tempPtr, symbol.col_fParam, null, new Constant(0), new Constant(0));
                        valueStack.push(resPtr);
                        if (isWaitingForLVal) System.out.println("理论上不可能是左值赋值语句跳转过来的");
                    }
                    else { // 实型
                        Value x = dimension1 != null ? dimension1 : new Constant(0);
                        Value y = dimension2 != null ? dimension2 : new Constant(0);
                        Pointer headPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, headPtr, Value.Type._i32, symbol.getPointer(),symbol.col_fParam);
                        Pointer ptr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, ptr, headPtr, symbol.col_fParam, x);
                        Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, ptr, symbol.col_fParam, new Constant(0), y);
                        Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, resPtr);
                        valueStack.push(res);
                        if (isWaitingForLVal) {
                            lValPointer = resPtr;
                        }
                    }

                }
            }
            else { // 这个左值是局部变量或常量
                if (symbol.dimension1 != null && symbol.dimension2 == null) { // 被调用的数组是1维的
                    Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                    if (dimension1 != null) { // 产生的是一个实型
                        if (symbol.isConst && (dimension1 instanceof Constant)) {
                            Integer arrayValue = symbol.getConstArrayValue(((Constant) dimension1).value, 0);
                            if (isConstExp) constValueStack.push(arrayValue);
                            else valueStack.push(new Constant(arrayValue));
                        }
                        else {
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, symbol.getPointer(), symbol.dimension1, null, dimension1, null);
                            Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                            builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, resPtr);
                            valueStack.push(res);
                            if (isWaitingForLVal) {
                                lValPointer = resPtr;
                            }
                        }
                    }
                    else { // 产生一个i32*
                        if (symbol.getPointer() == null) {
                            System.out.println("谁来救我1");
                        }
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, symbol.getPointer(), symbol.dimension1, null, new Constant(0), null);
                        valueStack.push(resPtr);
                        if (isWaitingForLVal){
//                                lValPointer = resPtr;
                            System.out.println("理论上不可能是左值赋值语句跳转过来的");
                        }
                    }
                }
                else if (symbol.dimension1 != null && symbol.dimension2 != null) { // 被调用的数组是2维的
                    if (dimension1 != null && dimension2 != null) { // 产生一个实型
                        if(symbol.isConst && (dimension1 instanceof Constant) && (dimension2 instanceof Constant)) {
                            Integer arrayValue = symbol.getConstArrayValue(((Constant)dimension1).value, ((Constant)dimension2).value);
                            if (arrayValue == null) System.out.println("Error : load constArrayValue from symbol is null");
                            if (isConstExp) constValueStack.push(arrayValue);
                            else valueStack.push(new Constant(arrayValue));
                        }
                        else {
                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, symbol.getPointer(), symbol.dimension1, symbol.dimension2, dimension1, dimension2);
                            Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                            builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, resPtr);
                            valueStack.push(res);
                            if (isWaitingForLVal){
                                lValPointer = resPtr;
                            }
                        }
                    }
                    else if (dimension1 != null && dimension2 == null) { // 产生一个i32 *
//                            Pointer headPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, headPtr, symbol.getPointer(), symbol.dimension1, symbol.dimension2, new Constant(0), dimension1);
////                            Value off = new Value(curFunction.newIdent(), Value.Type._i32);
////                            builder.buildMulInstruction(curBasicBlock, off, new Constant(symbol.dimension1), dimension1);
//                            Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, headPtr, symbol.dimension2, null, new Constant(0), new Constant(0));
                        Pointer headPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, headPtr, symbol.getPointer(), symbol.dimension1, symbol.dimension2,  dimension1, null);
                        Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, headPtr, symbol.dimension2, new Constant(0), new Constant(0));

                        valueStack.push(resPtr);
                        if (isWaitingForLVal){
                            System.out.println("理论上不可能是左值赋值语句跳转过来的");
                        }
                    }
                    else { // 产生一个i32 **
                        Pointer resPtr = new Pointer(curFunction.newIdent(), Value.Type._i32);
                        resPtr.col = symbol.dimension2;
                        if (symbol.getPointer() == null) {
                            System.out.println("谁来救我");
                        }
                        builder.buildGetElementPtrInstruction(curBasicBlock, Value.Type._i32, resPtr, symbol.getPointer(), symbol.dimension1, symbol.dimension2, new Constant(0), null);
                        valueStack.push(resPtr);
                        if (isWaitingForLVal){
                            System.out.println("理论上不可能是左值赋值语句跳转过来的");
                        }
                    }
                }
                else { // 被调用的全局变量是0维的
                    if (symbol.isConst) {
                        Integer arrayValue = symbol.getConstArrayValue(0, 0);
                        if (isConstExp) constValueStack.push(arrayValue);
                        else valueStack.push(new Constant(arrayValue));
                    }
                    else {
                        Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, res, Value.Type._i32, symbol.getPointer());
                        valueStack.push(res);
                        if (isWaitingForLVal) {
                            lValPointer = symbol.getPointer();
                        }
                    }
                }
            }
        } else {
            System.out.println("lVal Error：没找到变量 " + ident.word);
        }
    }
    private void visitPrimaryExp (PrimaryExp primaryExp) {
        Exp exp = primaryExp.getExp();
        LVal lVal = primaryExp.getlVal();
        Number number = primaryExp.getNumber();
        if (exp != null) {
            visitExp(exp);
        }
        else if (lVal != null) {
            visitLVal(lVal);
        }
        else {
            visitNumber(number);
        }
    }
    private void visitNumber (Number number) {
        Word intConst = number.getContent();
        if (isGlobalDecl || isConstExp) {
            constValueStack.push(intConst.number);
        }
        else {
            valueStack.push(new Constant(intConst.number));
        }

    }
    private void visitUnaryExp (UnaryExp unaryExp) {
        PrimaryExp primaryExp = unaryExp.getPrimaryExp();
        Word ident = unaryExp.getIdent();
        FuncRParams funcRParams = unaryExp.getFuncRParams();
        UnaryOp unaryOp = unaryExp.getUnaryOp();
        UnaryExp unaryExp1 = unaryExp.getUnaryExp();
        if (primaryExp != null) {
            visitPrimaryExp(primaryExp);
        }
        else if (ident != null) { // 函数调用
            if (isGlobalDecl || isConstExp) {
                // 全局变量或者常量应该不能函数调用吧
            }
            else {
                if (funcRParams != null) {
                    visitFuncRParams(funcRParams);
                }
                Function function = curModule.getFunction(ident.word);
                if (function.returnType == Value.Type._i32) {
                    Value result = new Value(curFunction.newIdent(), Value.Type._i32);
                    if (funcRParams != null) {
                        // 有返回 有参数
                        ArrayList<Value> params = new ArrayList<Value>();
                        for (int i = 0; i < function.arguments.size(); i++) {
                            params.add(valueStack.pop());
                        }
                        inverseList(params);
                        builder.buildCallInstruction(curBasicBlock, result, function, params);
                    } else {
                        // 有返回 无参数
                        builder.buildCallInstruction(curBasicBlock, result, function);
                    }
                    valueStack.push(result);
                }
                else {
                    if (funcRParams != null) {
                        // 无返回 有参数
                        ArrayList<Value> params = new ArrayList<Value>();
                        for (int i = 0; i < function.arguments.size(); i++) {
                            params.add(valueStack.pop());
                        }
                        inverseList(params);
                        builder.buildCallInstruction(curBasicBlock, function, params);
                    } else {
                        // 无返回 无参数
                        builder.buildCallInstruction(curBasicBlock,function);
                    }
                }
            }
        }
        else { // 单目运算
            Word operator = unaryOp.getOperator();
            visitUnaryExp(unaryExp1);
            if (operator.lexType == LexType.MINU) {
                if (isGlobalDecl || isConstExp) {
                    int abs = constValueStack.pop();
                    constValueStack.push(-abs);
                }
                else {
                    Value abs = valueStack.pop();
                    if (abs instanceof Constant) {
                        Constant res = new Constant(-((Constant) abs).value);
                        valueStack.push(res);
                    } else {
                        Value res = new Value(curFunction.newIdent(), Value.Type._i32);
                        builder.buildSubInstruction(curBasicBlock, res, new Constant(0), abs);
                        valueStack.push(res);
                    }
                }
            }
            else if (operator.lexType == LexType.PLUS) {

            }
            else if (operator.lexType == LexType.NOT) {
                // DONE : 判断还没做
                Value oppo = valueStack.pop();
                Value src = new Value(curFunction.newIdent(), Value.Type._i1);
                Value res = new Value(curFunction.newIdent(), Value.Type._i1);
                Value zextRes = new Value(curFunction.newIdent(), Value.Type._i32);
                builder.buildIcmpInstruction(curBasicBlock, IcmpInstruction.IcmpType.ne, src, oppo, new Constant(0));
                builder.buildXorInstruction(curBasicBlock, res, src);
                builder.buildZextInstruction(curBasicBlock, zextRes, Value.Type._i1, res, Value.Type._i32);
                valueStack.push(zextRes);
            }
            else {
                // plus => nothing
            }
        }
    }
    private void visitFuncRParams (FuncRParams funcRParams) {
        ArrayList<Exp> exps = funcRParams.getExps();
        for (Exp exp : exps) {
            visitExp(exp);
        }
    }
    private void visitMulExp (MulExp mulExp) {
        UnaryExp unaryExp = mulExp.getUnaryExp();
        MulExp mulExp1 = mulExp.getMulExp();
        Word operator = mulExp.getOperator();
        ArrayList<UnaryExp> unaryExps = new ArrayList<UnaryExp>();
        ArrayList<Word> ops = new ArrayList<Word>();
        unaryExps.add(unaryExp);
        if (operator != null) {
            ops.add(operator);
        }
        while (mulExp1 != null) {
            unaryExps.add(mulExp1.getUnaryExp());
            operator = mulExp1.getOperator();
            if (operator != null) {
                ops.add(operator);
            }
            mulExp1 = mulExp1.getMulExp();
        }
        for (int i = 0; i < unaryExps.size(); i++) {
            visitUnaryExp(unaryExps.get(i));
            if (i > 0) {
                operator = ops.get(i - 1);
                if (isGlobalDecl || isConstExp) {
                    Integer operand2 = constValueStack.pop();
                    Integer operand1 = constValueStack.pop();
                    constValueStack.push(calculate(operator, operand1, operand2));
                }
                else {
                    Value operand2 = valueStack.pop();
                    Value operand1 = valueStack.pop();
                    if (operand1 instanceof Constant && operand2 instanceof Constant) {
//                    System.out.println(((Constant) operand1).value + " " + operator.lexType +  " " + ((Constant) operand2).value);
                        valueStack.push(new Constant(calculate(operator, ((Constant) operand1).value, ((Constant) operand2).value)));
                    }
                    else {
                        Value result = new Value(curFunction.newIdent(), Value.Type._i32);
                        if (operator.lexType == LexType.MULT) {
                            builder.buildMulInstruction(curBasicBlock, result, operand1, operand2);
                            valueStack.push(result);
                        }
                        else if (operator.lexType == LexType.DIV) {
                            builder.buildSDivInstruction(curBasicBlock, result, operand1, operand2);
                            valueStack.push(result);
                        }
                        else if (operator.lexType == LexType.MOD) {
//                        a % b = a - (a/b)*b srem
                            builder.buildSremInstruction(curBasicBlock, result, operand1, operand2);
                            valueStack.push(result);
//                            builder.buildSDivInstruction(curBasicBlock, result, operand1, operand2);
//                            Value result1 = new Value(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildMulInstruction(curBasicBlock, result1, result, operand2);
//                            Value result2 = new Value(curFunction.newIdent(), Value.Type._i32);
//                            builder.buildSubInstruction(curBasicBlock, result2, operand1, result1);
//                            valueStack.push(result2);
                        }
                    }
                }
            }
        }
    }
    private void visitAddExp (AddExp addExp) {
        MulExp mulExp = addExp.getMulExp();
        AddExp addExp1 = addExp.getAddExp();
        Word operator = addExp.getOperator();
        ArrayList<MulExp> mulExps = new ArrayList<MulExp>();
        ArrayList<Word> ops = new ArrayList<Word>();
        mulExps.add(mulExp);
        if (operator != null) {
            ops.add(addExp.getOperator());
        }
        while (addExp1 != null) {
            mulExps.add(addExp1.getMulExp());
            operator = addExp1.getOperator();
            if (operator != null) {
                ops.add(operator);
            }
            addExp1 = addExp1.getAddExp();
        }
        for (int i = 0; i < mulExps.size(); i++) {
            visitMulExp(mulExps.get(i));
            if (i > 0) {
                operator = ops.get(i - 1);
                if (isGlobalDecl || isConstExp) {
                    Integer operand2 = constValueStack.pop();
                    Integer operand1 = constValueStack.pop();
                    constValueStack.push(calculate(operator, operand1, operand2));
                }
                else {
                    Value operand2 = valueStack.pop();
                    Value operand1 = valueStack.pop();
                    if (operand1 instanceof Constant && operand2 instanceof Constant) {
//                    System.out.println(((Constant) operand1).value + " " + operator.lexType +  " " + ((Constant) operand2).value);
                        valueStack.push(new Constant(calculate(operator, ((Constant) operand1).value, ((Constant) operand2).value)));
                    }
                    else {
                        Value result = new Value(curFunction.newIdent(), Value.Type._i32);
                        if (operator.lexType == LexType.MINU) {
                            builder.buildSubInstruction(curBasicBlock, result, operand1, operand2);
                        }
                        else if (operator.lexType == LexType.PLUS){
                            builder.buildAddInstruction(curBasicBlock, result, operand1, operand2);
                        }
                        valueStack.push(result);
                    }
                }
            }
        }
        // 他妈的，就是AST错了，我他妈一个节点一个节点的输出的
    }
    private void visitRelExp (RelExp relExp) {
        AddExp addExp = relExp.getAddExp();
        RelExp relExp1 = relExp.getRelExp();
        Word operator = relExp.getOperator();
        ArrayList<AddExp> addExps = new ArrayList<AddExp>();
        ArrayList<Word> ops = new ArrayList<Word>();
        addExps.add(addExp);
        if (operator != null) {
            ops.add(operator);
        }
        while (relExp1 != null) {
            addExps.add(relExp1.getAddExp());
            operator = relExp1.getOperator();
            if (operator != null) {
                ops.add(operator);
            }
            relExp1 = relExp1.getRelExp();
        }
        for (int i = 0; i < addExps.size(); i++) {
            visitAddExp(addExps.get(i));
            if (i > 0) {
                operator = ops.get(i - 1);
                Value relRes = valueStack.pop();
                Value addRes = valueStack.pop();
                Value res = new Value(curFunction.newIdent(), Value.Type._i1);
                IcmpInstruction.IcmpType icmpType = null;
                switch (operator.lexType) {
                    case LSS -> icmpType = IcmpInstruction.IcmpType.slt;
                    case LEQ -> icmpType = IcmpInstruction.IcmpType.sle;
                    case GRE -> icmpType = IcmpInstruction.IcmpType.sgt;
                    case GEQ -> icmpType = IcmpInstruction.IcmpType.sge;
                    default -> System.out.println("怎么会这样");
                }
                builder.buildIcmpInstruction(curBasicBlock, icmpType, res, addRes, relRes);
                Value res_i32 = new Value(curFunction.newIdent(), Value.Type._i32);
                builder.buildZextInstruction(curBasicBlock, res_i32, Value.Type._i1, res, Value.Type._i32);
                valueStack.push(res_i32);
            }
        }
    }
    private void visitEqExp (EqExp eqExp) {
        RelExp relExp = eqExp.getRelExp();
        EqExp eqExp1 = eqExp.getEqExp();
        Word operator = eqExp.getOperator();
        ArrayList<RelExp> relExps = new ArrayList<RelExp>();
        ArrayList<Word> ops = new ArrayList<Word>();
        relExps.add(relExp);
        if (operator != null) {
            ops.add(operator);
        }
        while (eqExp1 != null) {
            relExps.add(eqExp1.getRelExp());
            operator = eqExp1.getOperator();
            if (operator != null) {
                ops.add(operator);
            }
            eqExp1 = eqExp1.getEqExp();
        }
        for (int i = 0; i < relExps.size(); i++) {
            visitRelExp(relExps.get(i));
            if (i > 0) {
                operator = ops.get(i - 1);
                Value relRes = valueStack.pop();
                Value eqRes = valueStack.pop();
                Value res = new Value(curFunction.newIdent(), Value.Type._i1);
                IcmpInstruction.IcmpType icmpType = null;
                switch (operator.lexType) {
                    case EQL -> icmpType = IcmpInstruction.IcmpType.eq;
                    case NEQ -> icmpType = IcmpInstruction.IcmpType.ne;
                    default -> System.out.println("怎么会这样");
                }
                builder.buildIcmpInstruction(curBasicBlock, icmpType, res, relRes, eqRes);
                Value res_i32 = new Value(curFunction.newIdent(), Value.Type._i32);
                builder.buildZextInstruction(curBasicBlock, res_i32, Value.Type._i1, res, Value.Type._i32);
                valueStack.push(res_i32);
            }
        }
    }
    private void visitLAndExp (LAndExp lAndExp) { // DONE : 还没想好短路求值怎么搞
        EqExp eqExp = lAndExp.getEqExp();
        LAndExp lAndExp1 = lAndExp.getLandExp();
        Word operator = lAndExp.getOperator();
        ArrayList<EqExp> eqExps = new ArrayList<EqExp>();
        eqExps.add(eqExp);
        while (lAndExp1 != null) {
            eqExps.add(lAndExp1.getEqExp());
            lAndExp1 = lAndExp1.getLandExp();
        }
        for (int i = 0; i < eqExps.size(); i++) {
            visitEqExp(eqExps.get(i));
            Value eqRes = valueStack.pop();
            Value res = new Value(curFunction.newIdent(), Value.Type._i1);
            builder.buildIcmpInstruction(curBasicBlock, IcmpInstruction.IcmpType.ne, res, new Constant(0), eqRes);
            BrInstruction br = builder.buildBrInstruction(curBasicBlock, res);
            curBasicBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
            if (i != eqExps.size() - 1) {
                ((ifBlockBrHandler)curBrHandler).branches_lackTrueLabel.push(br);
                ((ifBlockBrHandler)curBrHandler).branches_lackFalseLabel.push(br);
//                if (i > 0) {
                while (((ifBlockBrHandler)curBrHandler).branches_lackTrueLabel.size() != 0) {
                    BrInstruction brLackTrue = ((ifBlockBrHandler)curBrHandler).branches_lackTrueLabel.pop();
                    brLackTrue.trueLabel = curBasicBlock;
                }
//                }
            }
            else {
                ((ifBlockBrHandler)curBrHandler).branches_ifBlock.push(br);
                ((ifBlockBrHandler)curBrHandler).branches_lackFalseLabel.push(br);
            }
        }
    }
    private void visitLastLandExp(LAndExp lAndExp) {
        EqExp eqExp = lAndExp.getEqExp();
        LAndExp lAndExp1 = lAndExp.getLandExp();
        Word operator = lAndExp.getOperator();
        ArrayList<EqExp> eqExps = new ArrayList<EqExp>();
        eqExps.add(eqExp);
        while (lAndExp1 != null) {
            eqExps.add(lAndExp1.getEqExp());
            lAndExp1 = lAndExp1.getLandExp();
        }
        for (int i = 0; i < eqExps.size(); i++) {
            visitEqExp(eqExps.get(i));
            Value eqRes = valueStack.pop();
            Value res = new Value(curFunction.newIdent(), Value.Type._i1);
            builder.buildIcmpInstruction(curBasicBlock, IcmpInstruction.IcmpType.ne, res, new Constant(0), eqRes);
            BrInstruction br = builder.buildBrInstruction(curBasicBlock, res);
            curBasicBlock = builder.buildBasicBlock(curFunction, curFunction.newIdent());
            if (i != eqExps.size() - 1) {
                ((ifBlockBrHandler)curBrHandler).branches_lackTrueLabel.push(br);
                ((ifBlockBrHandler)curBrHandler).branches_elseBlock.push(br);
//                if (i > 0) {
                while (((ifBlockBrHandler)curBrHandler).branches_lackTrueLabel.size() != 0) {
                    BrInstruction brLackTrue = ((ifBlockBrHandler)curBrHandler).branches_lackTrueLabel.pop();
                    brLackTrue.trueLabel = curBasicBlock;
                }
//                }
            }
            else {
                ((ifBlockBrHandler)curBrHandler).branches_ifBlock.push(br);
                ((ifBlockBrHandler)curBrHandler).branches_elseBlock.push(br);
            }
//            System.out.println("in last landExp : " + curBasicBlock.getIdent());
        }
    }
    private void visitLorExp (LOrExp lOrExp) { // DONE : 还没想好短路求值怎么搞
        LAndExp lAndExp = lOrExp.getlAndExp();
        LOrExp lOrExp1 = lOrExp.getlOrExp();
        Word operator = lOrExp.getOperator();
        ArrayList<LAndExp> lAndExps = new ArrayList<LAndExp>();
        lAndExps.add(lAndExp);
        while (lOrExp1 != null) {
            lAndExps.add(lOrExp1.getlAndExp());
            lOrExp1 = lOrExp1.getlOrExp();
        }
        for (int i = 0; i < lAndExps.size() - 1; i++) {
            visitLAndExp(lAndExps.get(i));
            while (((ifBlockBrHandler)curBrHandler).branches_lackFalseLabel.size() != 0) {
                BrInstruction brLackTrue = ((ifBlockBrHandler)curBrHandler).branches_lackFalseLabel.pop();
                brLackTrue.falseLabel = curBasicBlock;
            }
        }
        visitLastLandExp(lAndExps.get(lAndExps.size() - 1));
    }
    private void visitConstExp (ConstExp constExp) {
        AddExp addExp = constExp.getAddExp();
        isConstExp = true;
        visitAddExp(addExp);
        isConstExp = false;
    }
}
