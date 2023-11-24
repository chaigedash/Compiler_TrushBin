package LLVM.Instruction;

import LLVM.BasicBlock;

public class GetElementPtrInstruction extends Instruction{
//    <result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*
//    <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
    public GetElementPtrInstruction(BasicBlock basicBlock) {
        super(Operator.GetElementPtr, basicBlock);
    }
}
