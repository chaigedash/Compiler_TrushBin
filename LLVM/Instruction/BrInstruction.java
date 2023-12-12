package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.*;
import Utils.IO;

public class BrInstruction extends Instruction {
//    br i1 <cond>, label <iftrue>, label <iffalse>
//    br label <dest>
    public Value cond;
    public BasicBlock trueLabel;
    public BasicBlock falseLabel;
    public BrInstruction(BasicBlock basicBlock,Value cond) {
        super(Operator.Br, basicBlock);
        this.cond = cond;
    }
    public BrInstruction(BasicBlock basicBlock, BasicBlock trueLabel) {
        super(Operator.Br, basicBlock);
        this.trueLabel = trueLabel;
    }
    public void print() {
        String output = "";
        if (cond != null) {
            String tL = "<null>", fL = "<null>";
            if (trueLabel == null) {
                System.out.println("no trueLabel");
            }
            else {
                tL = trueLabel.getIdent();
            }
            if (falseLabel == null) {
                System.out.println("no falseLabel");
            }
            else {
                fL = falseLabel.getIdent();
            }
            output = operator + " i1 " + cond.getIdent() + ", label " + tL + ", label " + fL;
        }
        else {
            String tL = "<null>";
            if (trueLabel == null) {
                System.out.println("no trueLabel");
            }
            else {
                tL = trueLabel.getIdent();
            }
            output = operator + " label " + tL;
        }
        IO.getIO().writelnToLLVM(output);
    }
}
