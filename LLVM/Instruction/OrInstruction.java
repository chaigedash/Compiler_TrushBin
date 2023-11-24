package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Value;

public class OrInstruction extends Instruction{
//    <result> = or <ty> <op1>, <op2>
    Value result;
    Value.Type type;
    Value operand1;
    Value operand2;
    public OrInstruction(BasicBlock basicBlock, Value storage, Value.Type type, Value operand1, Value operand2) {
        super(Operator.Or, basicBlock);
        this.result = storage;
        this.type = type;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
}
