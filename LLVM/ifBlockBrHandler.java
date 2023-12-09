package LLVM;

import LLVM.Instruction.BrInstruction;

import java.util.Stack;

public class ifBlockBrHandler extends BrHandler{

    public Stack<BrInstruction> branches_lackFalseLabel = new Stack<BrInstruction>();
    public Stack<BrInstruction> branches_lackTrueLabel = new Stack<BrInstruction>();
    public Stack<BrInstruction> branches_ifBlock = new Stack<BrInstruction>();
    public Stack<BrInstruction> branches_elseBlock = new Stack<BrInstruction>();

    public ifBlockBrHandler(BrHandler preBrHandler) {
        super(Type.ifBlock, preBrHandler);
    }
}
