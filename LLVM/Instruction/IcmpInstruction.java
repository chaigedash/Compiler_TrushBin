package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.*;

public class IcmpInstruction extends Instruction{
//    <result> = icmp <cond> <ty> <op1>, <op2>
    Value cond;
    Value type;
    Value operand1;
    Value operand2;
    public IcmpInstruction(BasicBlock basicBlock, Value cond, Value operand1, Value operand2) {
        super(Operator.Icmp, basicBlock);
        this.cond = cond;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
}
