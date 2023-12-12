package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Pointer;
import Utils.ArrayHandler;
import Utils.IO;

public class BitCastInstruction extends Instruction{
    Pointer resPtr, srcPtr;
    Integer oriD1, oriD2, aimCol;
    public BitCastInstruction(BasicBlock basicBlock, Pointer resPtr, Integer oriDimension1, Integer oriDimension2, Integer aimCol, Pointer srcPtr) {
        super(Operator.BitCast, basicBlock);
        this.resPtr = resPtr;
        this.srcPtr = srcPtr;
        this.oriD1 = oriD1;
        this.oriD2 = oriD2;
        this.aimCol = aimCol;
    }
    public void print () {
        String aimType, oriType;
        aimType = ArrayHandler.getInstance().getType(Type._i32, aimCol, null);
        oriType = ArrayHandler.getInstance().getType(Type._i32, oriD1, oriD2);
        String output = aimType + "* " + operator + " (" + oriType + "* " + srcPtr.getIdent() + " to " + aimType + "*)";
        IO.getIO().writelnToLLVM(output);
    }
}
