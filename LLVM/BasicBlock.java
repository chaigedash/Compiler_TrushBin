package LLVM;

import LLVM.Instruction.BrInstruction;
import LLVM.Instruction.Instruction;
import LLVM.Instruction.RetInstruction;
import Utils.IO;

import java.util.ArrayList;

public class BasicBlock extends Value {
//    private ArrayList<Constant> constants = new ArrayList<Constant>();
    private ArrayList<Instruction> instructions = new ArrayList<Instruction>();

    public BasicBlock(String ident, Function function) {
        super(ident, Type._basicBlock);
    }

    public void addInstruct (Instruction instruction) {
        this.instructions.add(instruction);
    }
    public void print() {
        IO.getIO().writelnToLLVM(getIdent().substring(1)+":");
        for (Instruction instruction : instructions) {
//            System.out.print("\t");
            IO.getIO().writeToLLVM("\t");
            instruction.print();
            if (instruction instanceof BrInstruction && ((BrInstruction) instruction).cond == null) {
                break;
            }
            if (instruction instanceof RetInstruction) {
                break;
            }
        }
    }
}
