package LLVM.Instruction;

import LLVM.BasicBlock;

public class PhiInstruction extends Instruction{
//    <result> = phi [fast-math-flags] <ty> [ <val0>, <label0>], ...
    public PhiInstruction(BasicBlock basicBlock) {
        super(Operator.Phi, basicBlock);
    }
}
