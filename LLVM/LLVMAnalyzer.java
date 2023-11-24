package LLVM;

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
    private boolean isReturned;
    private Pointer lValPointer;
    private Stack<Integer> constValueStack = new Stack<Integer>(); // global和const的计算属性全都存这里
    private Stack<Value> valueStack = new Stack<Value>();
    private Module curModule;
    private Function curFunction;
    private BasicBlock curBasicBlock;
    private Builder builder;
    private boolean fuck;
//    private void gotoNewBasicBlock() {
//        if (curBasicBlock != null)
//            basicBlockStack.push(curBasicBlock);
//        curBasicBlock = builder.buildBasicBlock(curFunction, curFunction.giveName());
//    }
//    private void backPreBasicBlock() {
//        if (basicBlockStack.size() > 0)
//            curBasicBlock = basicBlockStack.pop();
//    }
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
        isReturned = false;
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
        // TODO : putstr参数不对，感觉涉及数组
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
        for (int i = 0; i < constExps.size(); i++) {
            visitConstExp(constExps.get(i));
        }
        visitConstInitVal(constInitVal);
        int res = constValueStack.pop();
        if (isGlobalDecl) {
            builder.buildGlobal(curModule, ident.word, res, true);
            symbol.setGlobal();
        }
        else {
//            builder.buildConstant(curBasicBlock, curFunction.giveName(), res);
            Pointer mem = new Pointer(curFunction.giveName(), Value.Type._i32);
            builder.buildAllocaInstruction(curBasicBlock, mem);
            builder.buildStoreInstruction(curBasicBlock, new Constant(res), mem);
            symbol.value = res;
        }
    }
    private void visitConstInitVal (ConstInitVal constInitVal) {
        ConstExp constExp = constInitVal.getConstExp();
        ArrayList<ConstInitVal> constInitVals = constInitVal.getConstInitVals();
        if (constExp != null) {
            visitConstExp(constExp);
        }
        else { // TODO : 数组
            for (ConstInitVal constInitVal1 : constInitVals) {
                visitConstInitVal(constInitVal1);
            }
        }
    }
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
        for (ConstExp constExp : constExps) {
            visitConstExp(constExp); // TODO : 数组的事儿
        }
        if (isGlobalDecl) {
            if (initVal != null) {
                visitInitVal(initVal);
                int res = constValueStack.pop();
                builder.buildGlobal(curModule, ident.word, res, true);
                symbol.setGlobal();
            }
            else {
                builder.buildGlobal(curModule, ident.word, 0,  false);
                symbol.setGlobal();
            }
        }
        else {
            Pointer memory = new Pointer(curFunction.giveName(), Value.Type._i32);
            builder.buildAllocaInstruction(curBasicBlock, memory);
            symbol.setPointer(memory);
            Value init = null;
            if (initVal != null) {
                visitInitVal(initVal);
                init = valueStack.pop();
                builder.buildStoreInstruction(curBasicBlock, init, memory);
            }
        }
        // TODO : 这个判断结构乱得逆天，有空改改
    }
    private void visitInitVal (InitVal initVal) {
        Exp exp = initVal.getExp();
        ArrayList<InitVal> initVals = initVal.getInitVals();
        if (exp != null) {
            visitExp(exp);
        }
        else {
            for (InitVal initVal1 : initVals) {
                visitInitVal(initVal1);
            }
        }
    }
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
        curBasicBlock = builder.buildBasicBlock(curFunction, curFunction.giveName());
        for (Argument arg : curFunction.arguments) {
            Pointer memory = new Pointer(curFunction.giveName(), Value.Type._i32);
            builder.buildAllocaInstruction(curBasicBlock, memory);
            builder.buildStoreInstruction(curBasicBlock, arg, memory);
            for (Symbol_v2 param : temp_Params) {
                if (param.ident.equals(arg.name)) {
                    param.setPointer(memory);
                }
            }
            // FIXME : 感觉这里可以性能优化，因为暂存表和arg表是一一对应
        }
        curSymbolTable.addSymbol(new Symbol_v2(fName.word, Symbol_v2.Type.function, returnType)); // 函数加进符号表
//        curSymbolTable = curSymbolTable.createChildTable();
//        for (Symbol_v2 param : temp_Params) {
//            curSymbolTable.addSymbol(param);
//        }
//        temp_Params = null;
//        curSymbolTable = curSymbolTable.getPreTable();
        visitBlock(block);
        if (!isReturned) {
            builder.buildRetInstruction(curBasicBlock);
        }
        isReturned = false;
        curFunction = null;
    }
    private void visitMainFuncDef (MainFuncDef mainFuncDef) {
        Block block = mainFuncDef.getBlock();
        curFunction = builder.buildFunction(curModule, "main", Value.Type._i32);
        String temp = curFunction.giveName();
        curBasicBlock = builder.buildBasicBlock(curFunction, temp);
//        System.out.println("main basicBlock: " + temp);
        curSymbolTable.addSymbol(new Symbol_v2("main", Symbol_v2.Type.function, Value.Type._i32)); // 函数加进符号表
//        curSymbolTable = curSymbolTable.createChildTable();
        visitBlock(block);
//        curSymbolTable = curSymbolTable.getPreTable();
        if (!isReturned) {
            builder.buildRetInstruction(curBasicBlock);
        }
        isReturned = false;
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
        if (funcFParam.getLBRACK1() != null) {
            type = Value.Type._i32;
        } else {
            type = Value.Type.Array;
        }
        if (constExp != null) {
            visitConstExp(constExp); // TODO: 二维数组
        }
        Symbol_v2 paramSymbol = new Symbol_v2(ident.word, Symbol_v2.Type.param);
//        curSymbolTable.addSymbol(paramSymbol);
        temp_Params.add(paramSymbol);
        String register = curFunction.giveName();
        builder.buildArgument(curFunction, register, type, ident.word);
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
            case LValEqExp :
                lVal = stmt.getlVal();
                exp = stmt.getExp();
                isWaitingForLVal = true;
                visitLVal(lVal);
                Value lValValue = valueStack.pop();
                isWaitingForLVal = false;
                visitExp(exp);
                Value expResult = valueStack.pop();
                // FIXME : 赋值没做，感觉还得看一遍指导书，应该还有落下的，但好困，晚安
                if (lValValue instanceof Global) {
                    builder.buildStoreInstruction(curBasicBlock, expResult, (Global)lValValue);
                }
                else {
                    builder.buildStoreInstruction(curBasicBlock, expResult, lValPointer);
                    lValPointer = null; // 用完就扔！
                }
                break;
            case Exp :
                exp = stmt.getExp();
                if (exp != null) {
                    visitExp(exp);
                }
                break;
            case Block:
                block = stmt.getBlock();
                visitBlock(block);
                break;
            case ifStmt:
                cond = stmt.getCond();
                stmt1 = stmt.getStmt1();
                stmt2 = stmt.getStmt2();
                if (cond != null) {
                    visitCond(cond);
                }
                if (stmt1 != null) {
                    visitStmt(stmt1);
                }
                if (stmt2 != null) {
                    visitStmt(stmt2);
                }
                break;
            case forStmt:
                forStmt1 = stmt.getForStmt1();
                cond = stmt.getCond();
                forStmt2 = stmt.getForStmt2();
                stmt1 = stmt.getStmt1();
                if (forStmt1 != null)
                    visitForStmt(forStmt1);
                if (cond != null)
                    visitCond(cond);
                if (forStmt2 != null)
                    visitForStmt(forStmt2);
                if (stmt1 != null)
                    visitStmt(stmt1);
                break;
            case breakStmt:
                break;
            case continueStmt:
                break;
            case returnExp:
                exp = stmt.getExp();
                if (exp != null) {
                    visitExp(exp);
                    Value result = valueStack.pop();
                    builder.buildRetInstruction(curBasicBlock, result);
                }
                else {
                    builder.buildRetInstruction(curBasicBlock);
                }
                isReturned = true;
                break;
            case getint: // TODO : 感觉逻辑有问题
                lVal = stmt.getlVal();
                isWaitingForLVal = true;
                visitLVal(lVal);
                Value lVal_getint = valueStack.pop();
                isWaitingForLVal = false;
                Value getintReturn = new Value(curFunction.giveName(), Value.Type._i32);
                Function function = curModule.getFunction("getint");
                builder.buildCallInstruction(curBasicBlock, getintReturn, function);
                if (lVal_getint instanceof Global) {
                    builder.buildStoreInstruction(curBasicBlock, getintReturn, (Global)lVal_getint);
                }
                else {
                    builder.buildStoreInstruction(curBasicBlock, getintReturn, lValPointer);
                    lValPointer = null; // 用完就扔！
                }
                break;
            case print:
                String formatString = stmt.getFormatString().word;
                exps = stmt.getExps();
                for (Exp exp1 : exps) {
                    visitExp(exp1);
                }
                Function putint = curModule.getFunction("putint");
                Function putch = curModule.getFunction("putch");
//                Function putstr = curModule.getFunction("putstr"); // TODO：暂时没用上，真怕它用上
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
                    }
                    else {
                        // call putch
                        Constant ch = null;
                        if (formatString.charAt(i) == '\\' && formatString.charAt(i + 1) == 'n') {
                            ch = new Constant('\n');
                            i++;
                        }
                        else {
                            ch = new Constant(formatString.charAt(i));
                        }
                        builder.buildCallInstruction(curBasicBlock, putch, ch);
                    }
                }
                break;
            default:break;
        }
    }
    private void visitForStmt (ForStmt forStmt) {
        LVal lVal = forStmt.getlVal();
        Exp exp = forStmt.getExp();
        visitLVal(lVal);
        visitExp(exp);
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
        for (Exp exp : exps) {
            visitExp(exp); // TODO 数组的事儿
        }
        Symbol_v2 symbol = curSymbolTable.searchSymbol(ident.word);
        if (symbol != null) { // Todo: 没考虑数组呢还
            if (symbol.isGlobal) {
                Global globalLVal = curModule.getGlobal(symbol.ident);
                if (isGlobalDecl || isConstExp) {
                    constValueStack.push(globalLVal.value);
                }
                else {
                    Value res = new Value(curFunction.giveName(), Value.Type._i32);
                    builder.buildLoadInstruction(curBasicBlock, res, globalLVal);
                    valueStack.push(res);
                    if (isWaitingForLVal){
                        lValPointer = globalLVal;
                    }
                }
            }
            else {
                if (symbol.value != null) {
                    // 这个左值是局部常量
                    if (isConstExp) {
                        constValueStack.push(symbol.value);
                    }
                    else {
                        valueStack.push(new Constant(symbol.value));
                    }
                }
                else {
                    // 这个左值是局部变量
                    if (isConstExp) {
                    }
                    else {
                        Value res = new Value(curFunction.giveName(), Value.Type._i32);
                        builder.buildLoadInstruction(curBasicBlock, res, symbol.getPointer());
                        valueStack.push(res); // valueStack是为了计算而做的栈！！不可以加pointer
//                        好崩溃，pointer到底加不加进valueStack啊啊啊啊
                        if (isWaitingForLVal){
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
                        Value result = new Value(curFunction.giveName(), Value.Type._i32);
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
                    Value res = new Value(curFunction.giveName(), Value.Type._i32);
                    if (abs instanceof Constant) {
                        ((Constant) res).value = -((Constant) abs).value;
                    } else {
                        builder.buildSubInstruction(curBasicBlock, res, new Constant(0), abs);
                    }
                    valueStack.push(res);
                }
            }
            else if (operator.lexType == LexType.PLUS) {

            }
            else if (operator.lexType == LexType.NOT) {
                // TODO: 判断还没做
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
//        System.out.println("in mul have unary : -----"+unaryExps.size());
//        if (mulExp.getMulExp()!=null) {
//            PrimaryExp p = mulExp.getUnaryExp().getPrimaryExp();
//            System.out.println("aaaaaaaaaaaaa:::::" + (p.getNumber() != null ? p.getNumber().getContent().number : p.getlVal().getIdent().word));
//            p = mulExp.getMulExp().getUnaryExp().getPrimaryExp();
//            System.out.println("kanhaole:::::" + (p.getNumber() != null ? p.getNumber().getContent().number : p.getlVal().getIdent().word));
//        }
        for (int i = 0; i < unaryExps.size(); i++) {
            visitUnaryExp(unaryExps.get(i));
            if (i > 0) {
                operator = ops.get(i - 1);
                if (isGlobalDecl || isConstExp) {
                    Integer operand2 = constValueStack.pop();
                    Integer operand1 = constValueStack.pop();
//                    System.out.println(operand1 + " " + operator.lexType + " " + operand2);
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
                        Value result = new Value(curFunction.giveName(), Value.Type._i32);
                        if (operator.lexType == LexType.MULT) {
                            builder.buildMulInstruction(curBasicBlock, result, operand1, operand2);
                            valueStack.push(result);
                        }
                        else if (operator.lexType == LexType.DIV) {
                            builder.buildSDivInstruction(curBasicBlock, result, operand1, operand2);
                            valueStack.push(result);
                        }
                        else if (operator.lexType == LexType.MOD) {
//                        a % b = a - (a/b)*b
                            builder.buildSDivInstruction(curBasicBlock, result, operand1, operand2);
                            Value result1 = new Value(curFunction.giveName(), Value.Type._i32);
                            builder.buildMulInstruction(curBasicBlock, result1, result, operand2);
                            Value result2 = new Value(curFunction.giveName(), Value.Type._i32);
                            builder.buildSubInstruction(curBasicBlock, result2, operand1, result1);
                            valueStack.push(result2);
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
//        System.out.println("in add have muls : -----"+mulExps.size());
        for (int i = 0; i < mulExps.size(); i++) {
            visitMulExp(mulExps.get(i));
            if (i > 0) {
                operator = ops.get(i - 1);
                if (isGlobalDecl || isConstExp) {
                    Integer operand2 = constValueStack.pop();
                    Integer operand1 = constValueStack.pop();
//                    System.out.println(operand1 + " " + operator.lexType + " " + operand2);
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
                        Value result = new Value(curFunction.giveName(), Value.Type._i32);
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
//        if (!fuck && operator!=null){
//            System.out.println(addExp.getAddExp().getAddExp().getMulExp().getUnaryExp().getPrimaryExp().getExp() == null ? "y" : "n");
//            System.out.println(addExp.getAddExp().getAddExp().getMulExp().getUnaryExp().getPrimaryExp().getExp().getAddExp().getAddExp().getMulExp().getUnaryExp().getPrimaryExp().getNumber().getContent().number);
//            fuck = true;
//        }
        // 他妈的，就是AST错了，我他妈一个节点一个节点的输出的
    }
    private void visitRelExp (RelExp relExp) {
        AddExp addExp = relExp.getAddExp();
        RelExp relExp1 = relExp.getRelExp();
        Word operator = relExp.getOperator();
        if (operator != null) {
            visitRelExp(relExp1);
            visitAddExp(addExp);
        }
        else {
            visitAddExp(addExp);
        }
    }
    private void visitEqExp (EqExp eqExp) {
        RelExp relExp = eqExp.getRelExp();
        EqExp eqExp1 = eqExp.getEqExp();
        Word operator = eqExp.getOperator();
        if (operator != null) {
            visitEqExp(eqExp1);
            visitRelExp(relExp);
        }
        else {
            visitRelExp(relExp);
        }
    }
    private void visitLAndExp (LAndExp lAndExp) {
        EqExp eqExp = lAndExp.getEqExp();
        LAndExp lAndExp1 = lAndExp.getLandExp();
        Word operator = lAndExp.getOperator();
        if (operator != null) {
            visitLAndExp(lAndExp1);
            visitEqExp(eqExp);
        }
        else {
            visitEqExp(eqExp);
        }
    }
    private void visitLorExp (LOrExp lOrExp) {
        LAndExp lAndExp = lOrExp.getlAndExp();
        LOrExp lOrExp1 = lOrExp.getlOrExp();
        Word operator = lOrExp.getOperator();
        if (operator != null) {
            visitLorExp(lOrExp1);
            visitLAndExp(lAndExp);
        }
        else {
            visitLAndExp(lAndExp);
        }
    }
    private void visitConstExp (ConstExp constExp) {
        AddExp addExp = constExp.getAddExp();
        isConstExp = true;
        visitAddExp(addExp);
        isConstExp = false;
    }
}
