package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Constant;
import LLVM.Value;
import Utils.IO;

public class MulInstruction extends Instruction{
//	<result> = mul <ty> <op1>, <op2>
    Value result;
    Value.Type type;
    Value operand1;
    Value operand2;
    public MulInstruction(BasicBlock basicBlock, Value storage, Value.Type type, Value operand1, Value operand2) {
        super(Operator.Mul, basicBlock);
        this.result = storage;
        this.type = type;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
    public void print() {
        String output = "";
        String operand1Ident, operand2Ident;
        operand1Ident = operand1 instanceof Constant ? String.valueOf(((Constant) operand1).value) : operand1.getIdent();
        operand2Ident = operand2 instanceof Constant ? String.valueOf(((Constant) operand2).value) : operand2.getIdent();
        output = result.getIdent() + " = " + operator + " i32 " + operand1Ident + ", " + operand2Ident;
        IO.getIO().writelnToLLVM(output);
    }
}
