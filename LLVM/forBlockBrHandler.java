package LLVM;

import LLVM.Instruction.BrInstruction;

import java.util.Stack;

public class forBlockBrHandler extends BrHandler{
    public Stack<BrInstruction> branches_thenBlock = new Stack<BrInstruction>();
    public Stack<BrInstruction> branches_nextBlock = new Stack<BrInstruction>();
    public forBlockBrHandler(BrHandler preBrHandler) {
        super(Type.forBlock, preBrHandler);
    }
}
