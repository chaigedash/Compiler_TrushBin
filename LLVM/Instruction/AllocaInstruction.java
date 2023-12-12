package LLVM.Instruction;

import LLVM.*;
import Utils.ArrayHandler;
import Utils.IO;

public class AllocaInstruction extends Instruction{
//    <result> = alloca <type>
    Pointer result;
    Value.Type type;
    Integer dimension1, dimension2;
    boolean isFParam;
    public AllocaInstruction(BasicBlock basicBlock, Pointer mem, Value.Type type) {
        super(Operator.Alloca, basicBlock);
        this.result = mem;
        this.type = type;
    }
    public AllocaInstruction(BasicBlock basicBlock, Pointer mem, Value.Type type, Integer dimension1, Integer dimension2, boolean isFParam) {
        super(Operator.Alloca, basicBlock);
        this.result = mem;
        this.type = type;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.isFParam = isFParam;
    }
    public void print() {
        String type = "";
        if (this.dimension1 == null || this.dimension1 == 0) {
            type = "i32";
        }
        else {
            type = ArrayHandler.getInstance().getType(this.type, this.dimension1, this.dimension2);
        }
        if (isFParam && this.dimension1 != null) {
            type += "*";
        }
        String output = result.getIdent() + " = " + operator + " " + type;
        IO.getIO().writelnToLLVM(output);
    }
}
