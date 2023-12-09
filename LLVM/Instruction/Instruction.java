package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.User;

public abstract class Instruction extends User{
    public enum Operator {
        Add, Sub, Mul, Div, Srem, And, Or,
        Lt, Le, Ge, Gt, Eq, Ne,
        Zext, Trunc,
        Alloca, Load, Store, GetElementPtr, Icmp,
        Phi, MemPhi, LoadDep,
        Br, Call, Ret,
        Not;
        public String toString () {
            switch (this) {
                case Add : return "add";
                case Sub : return "sub";
                case Mul : return "mul";
                case Div : return "sdiv";
                case Srem : return "srem";
                case Icmp : return "icmp";
                case And : return "and";
                case Or : return "or";
                case Call : return "call";
                case Alloca: return "alloca";
                case Load : return "load";
                case Store : return "store";
                case GetElementPtr : return "getelementptr";
                case Phi : return "phi";
                case Zext : return "zext";
                case Trunc : return "trunc";
                case Br : return "br";
                case Ret : return "ret";
//                case Not : return "not";
//                case Mod : return "mod";
//                case Lt : return "lt";
//                case Le : return "le";
//                case Gt : return "gt";
//                case Eq : return "eq";
//                case Ne : return "ne";
            }
            return null;
        }
    }
    public Operator operator;
    private BasicBlock basicBlock;
    public Instruction(Operator operator, BasicBlock basicBlock) {
        super("", Type.Instruction);
        this.operator = operator;
        this.basicBlock = basicBlock;
    }
    public void print () {
//        super.print();
//        System.out.println(operator.toString());
    }
}
