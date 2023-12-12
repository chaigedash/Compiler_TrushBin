package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Value;
import Utils.IO;

public class XorInstruction extends Instruction{
    public Value res;
    public Value src;
    public XorInstruction(BasicBlock basicBlock, Value src, Value res) {
        super(Operator.Xor, basicBlock);
        this.res = res;
        this.src = src;
    }
    public void print() {
        String output = "";
        output += res.getIdent() + " = " + operator + " i1 " + src.getIdent() + ", true";
        IO.getIO().writelnToLLVM(output);
    }
}
