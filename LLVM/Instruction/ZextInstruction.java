package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Value;
import Utils.IO;

public class ZextInstruction extends Instruction{
//    <result> = zext <ty> <value> to <ty2>
    Value res;
    Value.Type fromType;
    Value value;
    Value.Type toType;
    public ZextInstruction(BasicBlock basicBlock, Value res, Value.Type fromType, Value value, Value.Type toType) {
        super(Operator.Zext, basicBlock);
        this.res = res;
        this.fromType = fromType;
        this.value = value;
        this.toType = toType;
    }
    public void print () {
        String output = res.getIdent() + " = " + operator + " " + fromType + " " + value.getIdent() + " to " + toType;
        IO.getIO().writelnToLLVM(output);
    }
}
