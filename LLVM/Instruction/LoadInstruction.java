package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.*;
import Utils.IO;

public class LoadInstruction extends Instruction{
//    <result> = load <ty>, <ty>* <pointer>
    Value result;
    Value src;
    public LoadInstruction(BasicBlock basicBlock, Value result, Pointer src) {
        super(Operator.Load, basicBlock);
        this.result = result;
        this.src = src;
    }
    public LoadInstruction(BasicBlock basicBlock, Value result, Global src) {
        super(Operator.Load, basicBlock);
        this.result = result;
        this.src = src;
    }
    public void print() {
//        %4 = load i32, i32* @a   ;读取全局变量a
        String output = result.getIdent() + " = " + operator + " i32, " + src.getType() + " " + src.getIdent();
        IO.getIO().writelnToLLVM(output);
    }
}
