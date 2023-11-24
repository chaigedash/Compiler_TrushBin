package LLVM.Instruction;

import LLVM.*;
import Utils.IO;

public class RetInstruction extends Instruction{
//    ret <type> <value> ,ret void
    private boolean isVoid = false;
    Value value;
    public RetInstruction(BasicBlock basicBlock) {
        super(Operator.Ret, basicBlock);
        this.isVoid = true;
    }
    public RetInstruction(BasicBlock basicBlock, Value value) {
        super(Operator.Ret, basicBlock);
        this.isVoid = false;
        this.value = value;
    }
    public void print() {
        String output = "";
        if (isVoid) {
            output = operator.toString() + " void";
        }
        else if (value instanceof Constant) {
            output = operator + " i32 " + ((Constant)value).value;
        }
        else {
            output = operator + " i32 " + value.getIdent();
        }
        IO.getIO().writelnToLLVM(output);
    }
}
