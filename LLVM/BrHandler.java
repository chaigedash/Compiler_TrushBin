package LLVM;

import LLVM.Instruction.BrInstruction;

import java.util.Stack;

public class BrHandler {
    public enum Type {
        ifBlock, forBlock
    }
    BrHandler preBrHandler;
    Type type;
    public BrHandler (Type type, BrHandler preBrHandler) {
        this.type = type;
        this.preBrHandler = preBrHandler;
    }
}
