package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.*;

public class BrInstruction extends Instruction {
//    br i1 <cond>, label <iftrue>, label <iffalse>
//    br label <dest>
    Value cond;
    public BrInstruction(BasicBlock basicBlock) {
        super(Operator.Br, basicBlock);
    }
}
