package LLVM;

import Utils.IO;

import java.util.ArrayList;

public class Function extends Value {
    public ArrayList<BasicBlock> basicBlocks = new ArrayList<BasicBlock>();
    public ArrayList<Argument> arguments = new ArrayList<Argument>();
    public Type returnType;
    public int index;
    public Function(String ident, Type returnType) {
        super(ident, Type.Function);
        this.returnType = returnType;
        index = 0;
    }
    public void addBasicBlock(BasicBlock block) {
        this.basicBlocks.add(block);
    }
    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }
    public String newIdent() {
        index++;
        return "%" + Integer.valueOf(index - 1).toString();
    }
    public void print() {
        StringBuilder funcDefOutput = new StringBuilder("define dso_local");
        if (returnType == Type._i32) {
            funcDefOutput.append(" i32 ");
        }
        else {
            funcDefOutput.append(" void ");
        }
        funcDefOutput.append(super.getIdent());
        funcDefOutput.append("(");
        for (int i = 0; i < arguments.size(); i++) {
            Argument argument = arguments.get(i);
            if (i != 0) {
                funcDefOutput.append(", ");
            }
            funcDefOutput.append("i32 ").append(argument.getIdent());
        }
        funcDefOutput.append(") {");
        IO.getIO().writelnToLLVM(String.valueOf(funcDefOutput));
        for (BasicBlock block : basicBlocks) {
            block.print();
        }
        IO.getIO().writelnToLLVM("}");
    }
}
