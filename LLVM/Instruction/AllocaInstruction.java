package LLVM.Instruction;

import LLVM.*;
import Utils.IO;

public class AllocaInstruction extends Instruction{
//    <result> = alloca <type>
    Pointer result;
    Value.Type type;
    public AllocaInstruction(BasicBlock basicBlock, Pointer mem, Value.Type type) {
        super(Operator.Alloca, basicBlock);
        this.result = mem;
        this.type = type;
    }
    public void print() {
        String output = result.getIdent() + " = " + operator + " i32";
        IO.getIO().writelnToLLVM(output);
    }
}
