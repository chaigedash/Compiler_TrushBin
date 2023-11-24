package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Value;
public class AndInstruction extends Instruction{
//<result> = and <ty> <op1>, <op2>
    Value storage;
    Value.Type type;
    Value operand1;
    Value operand2;
    public AndInstruction(BasicBlock basicBlock, Value storage, Value.Type type, Value operand1, Value operand2) {
        super(Operator.And, basicBlock);
        this.storage = storage;
        this.type = type;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
}
