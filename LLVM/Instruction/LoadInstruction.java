package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.*;
import Utils.ArrayHandler;
import Utils.IO;

public class LoadInstruction extends Instruction{
//    <result> = load <ty>, <ty>* <pointer>
    Value result;
    Value src;
    Integer dimension1, dimension2;
    Value.Type priType;
    public LoadInstruction(BasicBlock basicBlock, Value result, Value.Type priType, Pointer src) {
        super(Operator.Load, basicBlock);
        this.result = result;
        this.src = src;
        this.priType = priType;
    }
    public LoadInstruction(BasicBlock basicBlock, Value result, Value.Type priType, Pointer src, Integer col) {
        super(Operator.Load, basicBlock);
        this.result = result;
        this.src = src;
        this.priType = priType;
        this.dimension1 = col;
    }
    public LoadInstruction(BasicBlock basicBlock, Value result, Value.Type priType, Pointer src, Integer dimension1, Integer dimension2) {
        super(Operator.Load, basicBlock);
        this.result = result;
        this.src = src;
        this.priType = priType;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
    }
    public LoadInstruction(BasicBlock basicBlock, Value result, Value.Type priType, Global src) {
        super(Operator.Load, basicBlock);
        this.result = result;
        this.src = src;
        this.priType = priType;
    }
    public void print() {
//        %4 = load i32, i32* @a   ;读取全局变量a
        String type = ArrayHandler.getInstance().getType(priType, dimension1, dimension2);
        if (dimension1 != null) type += "*";
        String output = result.getIdent() + " = " + operator + " " + type + ", " + type + "* " + src.getIdent();
        IO.getIO().writelnToLLVM(output);
    }
}
