package LLVM;
import LLVM.Instruction.*;

import java.util.ArrayList;

public class Builder {
    public void buildGlobal(Module module, String globalName, int value, boolean isConst) {
        module.addGlobal(new Global("@" + globalName, value, isConst));
    }
    public Function buildFunction (Module module, String functionName, Value.Type returnType) {
        Function function = new Function("@" + functionName, returnType);
        module.addFunction(function);
        return function;
    }
    public BasicBlock buildBasicBlock (Function function, String ident) {
        BasicBlock basicBlock = new BasicBlock(ident, function); // TODO: 基本块的标识符感觉要出锅
        function.addBasicBlock(basicBlock);
        return basicBlock;
    }
    public void buildArgument (Function function, String ident, Value.Type type, String name) {
        function.addArgument(new Argument(ident, type, name));
    }
/*    =========================    build Instruction    =============================     */
    public void buildAddInstruction (BasicBlock basicBlock, Value result, Value operand1, Value operand2) {
        basicBlock.addInstruct(
            new AddInstruction(basicBlock, result, Value.Type._i32, operand1, operand2)
        );
    }
    public void buildSubInstruction (BasicBlock basicBlock, Value result, Value operand1, Value operand2) {
        basicBlock.addInstruct(
            new SubInstruction(basicBlock, result, Value.Type._i32, operand1, operand2)
        );
    }
    public void buildMulInstruction (BasicBlock basicBlock, Value result, Value operand1, Value operand2) {
        basicBlock.addInstruct(
            new MulInstruction(basicBlock, result, Value.Type._i32, operand1, operand2)
        );
    }
    public void buildSDivInstruction (BasicBlock basicBlock, Value result, Value operand1, Value operand2) {
        basicBlock.addInstruct(
            new SdivInstruction(basicBlock, result, Value.Type._i32, operand1, operand2)
        );
    }
    public void buildSremInstruction (BasicBlock basicBlock, Value result, Value operand1, Value operand2) {
        basicBlock.addInstruct(
                new SremInstruction(basicBlock, result, Value.Type._i32, operand1, operand2)
        );
    }
    public void buildAndInstruction (BasicBlock basicBlock, Value result, Value operand1, Value operand2) {
        basicBlock.addInstruct(
            new AndInstruction(basicBlock, result, Value.Type._i32, operand1, operand2)
        );
    }
    public void buildOrInstruction (BasicBlock basicBlock, Value result, Value operand1, Value operand2) {
        basicBlock.addInstruct(
            new OrInstruction(basicBlock, result, Value.Type._i32, operand1, operand2)
        );
    }
    public void buildAllocaInstruction (BasicBlock basicBlock, Pointer memory) {
        basicBlock.addInstruct(
            new AllocaInstruction(basicBlock, memory, Value.Type._i32)
        );
    }
    public BrInstruction buildBrInstruction (BasicBlock basicBlock, Value cond) {
        BrInstruction br = new BrInstruction(basicBlock, cond);
        basicBlock.addInstruct(br);
        return br;
    }
    public BrInstruction buildBrInstruction (BasicBlock basicBlock, BasicBlock destBlock) {
        BrInstruction br = new BrInstruction(basicBlock, destBlock);
        basicBlock.addInstruct(br);
        return br;
    }
    public void buildCallInstruction (BasicBlock basicBlock, Value result, Function function, ArrayList<Value> params) {
        // 有参数，有返回
        basicBlock.addInstruct(
                new CallInstruction(basicBlock, result, function, params)
        );
    }
    public void buildCallInstruction (BasicBlock basicBlock, Value result, Function function) {
        // 无参数，有返回
        basicBlock.addInstruct(
                new CallInstruction(basicBlock, result, function)
        );
    }
    public void buildCallInstruction (BasicBlock basicBlock, Function function, ArrayList<Value> params) {
        // 有参数，无返回
        basicBlock.addInstruct(
                new CallInstruction(basicBlock, function, params)
        );
    }
    public void buildCallInstruction (BasicBlock basicBlock, Function function) {
        // 无参数，无返回
        basicBlock.addInstruct(
                new CallInstruction(basicBlock, function)
        );
    }
    public void buildCallInstruction (BasicBlock basicBlock, Function function, Value param) {
        ArrayList<Value> params = new ArrayList<Value>();
        params.add(param);
        basicBlock.addInstruct(
                new CallInstruction(basicBlock, function, params)
        );
    }
    public void buildGetElementPtrInstruction (BasicBlock basicBlock) {
        basicBlock.addInstruct(
                new GetElementPtrInstruction(basicBlock)
        );
    }
    public void buildIcmpInstruction (BasicBlock basicBlock, IcmpInstruction.IcmpType cond, Value res, Value operand1, Value operand2) {
        basicBlock.addInstruct(
                new IcmpInstruction(basicBlock, cond, res, operand1, operand2)
        );
    }
    public void buildLoadInstruction (BasicBlock basicBlock, Value result, Pointer src) {
        basicBlock.addInstruct(
                new LoadInstruction(basicBlock, result, src)
        );
    }
    public void buildLoadInstruction (BasicBlock basicBlock, Value result, Global src) {
        basicBlock.addInstruct(
                new LoadInstruction(basicBlock, result, src)
        );
    }
    public void buildStoreInstruction (BasicBlock basicBlock, Value src, Pointer dst) {
        basicBlock.addInstruct(
                new StoreInstruction(basicBlock, src, dst)
        );
    }
    public void buildStoreInstruction (BasicBlock basicBlock, Value src, Global dst) {
        basicBlock.addInstruct(
                new StoreInstruction(basicBlock, src, dst)
        );
    }
    public void buildPhiInstruction (BasicBlock basicBlock) {
        basicBlock.addInstruct(
                new PhiInstruction(basicBlock)
        );
    }
    public void buildRetInstruction (BasicBlock basicBlock) { // return void
        basicBlock.addInstruct(
                new RetInstruction(basicBlock)
        );
    }
    public void buildRetInstruction (BasicBlock basicBlock, Value retValue) {
        basicBlock.addInstruct(
                new RetInstruction(basicBlock, retValue)
        );
    }
    public void buildZextInstruction (BasicBlock basicBlock, Value res, Value.Type fromType, Value value, Value.Type toType) {
        basicBlock.addInstruct(
                new ZextInstruction(basicBlock, res, fromType, value, toType)
        );
    }
}
