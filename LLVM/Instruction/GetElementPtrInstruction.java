package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Value;

import java.util.ArrayList;

public class GetElementPtrInstruction extends Instruction{
//    <result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*
//    <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
    private Value res;
    private Value.Type type;
    private ArrayList<index> indexes;
    public class index {
        public Value.Type type;
        public Value index;
    }
    public GetElementPtrInstruction(BasicBlock basicBlock) {
        super(Operator.GetElementPtr, basicBlock);
    }
}
