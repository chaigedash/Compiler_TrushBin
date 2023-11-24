package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Value;

public class ZextInstruction extends Instruction{
//    <result> = zext <ty> <value> to <ty2>
    Value type;
    Value value;
    Value type2;
    public ZextInstruction(BasicBlock basicBlock, Value type, Value value, Value type2) {
        super(Operator.Zext, basicBlock);
        this.type = type;
        this.value = value;
        this.type2 = type2;
    }
}
