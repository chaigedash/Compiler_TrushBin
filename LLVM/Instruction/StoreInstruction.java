package LLVM.Instruction;

import LLVM.*;
import Utils.ArrayHandler;
import Utils.IO;

public class StoreInstruction extends Instruction{
//    store <ty> <value>, <ty>* <pointer>
    Value src;
    Value dst;
    Integer col;
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
    // 👆我真的是脑子抽抽了才会想出这么诡异的逻辑，但是改了又报错，懒得动了
    public StoreInstruction(BasicBlock basicBlock, Value src, Pointer dst, Integer col) {
        super(Operator.Store, basicBlock);
        this.src = src;
        this.dst = dst;
        this.col = col;
    }
    public void print() {
        String type = ArrayHandler.getInstance().getType(Type._i32, col, null);
        if (col == null) {
        }
        else {
            type += "*";
        }
        String output = operator + " " + type + " ";
        if (src instanceof Constant) {
            output += String.valueOf(((Constant) src).value);
        }
        else {
            output +=  src.getIdent();
        }
        if (dst == null) {
            System.out.println("我要尖叫了");
        }
        output += ", " + type + "* " + dst.getIdent();
        IO.getIO().writelnToLLVM(output);
    }
}
