package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.*;
import Utils.IO;

public class IcmpInstruction extends Instruction{
//    <result> = icmp <cond> <ty> <op1>, <op2>
    public enum IcmpType {
        slt,// <
        sgt,// >
        sle,// <=
        sge,// >=
        eq,// ==
        ne;// !=
        public String toString () {
            switch (this) {
                case slt : return "slt";
                case sgt : return "sgt";
                case sle : return "sle";
                case sge : return "sge";
                case eq : return "eq";
                case ne : return "ne";
            }
            return null;
        }
    }
    IcmpType cond;
    Value res;
    Value operand1;
    Value operand2;
    public IcmpInstruction(BasicBlock basicBlock, IcmpType cond, Value res, Value operand1, Value operand2) {
        super(Operator.Icmp, basicBlock);
        this.cond = cond;
        this.res = res;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
    public void print() {
        String type, op1, op2;
        op1 = operand1 instanceof Constant ? String.valueOf(((Constant) operand1).value) : operand1.getIdent();
        op2 = operand2 instanceof Constant ? String.valueOf(((Constant) operand2).value) : operand2.getIdent();
//        if (op1.equals("0"))
//            type = "i1";
//        else
            type = "i32";
        String output = res.getIdent() + " = " + operator + " " + cond + " " + type + " " + op1 + ", " + op2;
        IO.getIO().writelnToLLVM(output);
    }
}
