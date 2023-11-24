package LLVM.Instruction;

import LLVM.*;
import Utils.IO;

public class StoreInstruction extends Instruction{
//    store <ty> <value>, <ty>* <pointer>
    Value src;
    Value dst;
    public StoreInstruction(BasicBlock basicBlock, Value src, Pointer dst) {
        super(Operator.Store, basicBlock);
        this.src = src;
        this.dst = dst;
    }
    public StoreInstruction(BasicBlock basicBlock, Value src, Global dst) {
        super(Operator.Store, basicBlock);
        this.src = src;
        this.dst = dst;
    }
    public void print() {
        String output = operator + " i32 ";
        if (src instanceof Constant) {
            output += String.valueOf(((Constant) src).value);
        }
        else {
            output +=  src.getIdent();
        }
        output += ", " + dst.getType() + " " + dst.getIdent();
        IO.getIO().writelnToLLVM(output);
    }
}
