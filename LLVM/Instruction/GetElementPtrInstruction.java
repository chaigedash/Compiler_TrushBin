package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Constant;
import LLVM.Pointer;
import LLVM.Value;
import Utils.ArrayHandler;
import Utils.IO;

import java.util.ArrayList;

public class GetElementPtrInstruction extends Instruction{
//    <result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*
//    <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*
    private Pointer res;
    private Pointer headPtr;
    private Value.Type priType;
    Integer dimension1, dimension2;
    Value x, y;
    Integer col;
    public GetElementPtrInstruction(BasicBlock basicBlock, Value.Type priType, Pointer res, Pointer headPtr, Integer dimension1, Integer dimension2, Value x, Value y) {
        super(Operator.GetElementPtr, basicBlock);
        this.res = res;
        this.headPtr = headPtr;
        this.priType = priType;
        this.dimension1 = dimension1;
        this.dimension2 = dimension2;
        this.x = x;
        this.y = y;
    }
    public GetElementPtrInstruction(BasicBlock basicBlock, Value.Type priType, Pointer res, Pointer headPtr, Integer col, Value x) {
        super(Operator.GetElementPtr, basicBlock);
        this.res = res;
        this.headPtr = headPtr;
        this.priType = priType;
        this.col = col;
        this.x = x;
    }
    public GetElementPtrInstruction(BasicBlock basicBlock, Value.Type priType, Pointer res, Pointer headPtr, Integer col, Value x, Value y) {
        super(Operator.GetElementPtr, basicBlock);
        this.res = res;
        this.headPtr = headPtr;
        this.priType = priType;
        this.col = col;
        this.x = x;
        this.y = y;
    }
    public void print() {
        // FIXME: 目前只考虑了第一条文法
        String output = "";
        if (col == null) {
            if (this.x == null) {
                String type = ArrayHandler.getInstance().getType(priType, dimension1, dimension2);
                if (dimension1 != null && dimension2 != null) {
                    System.out.println("Error: in getelementInsturction");
                    output += res.getIdent() + " = " + operator + " " + type + ", " + type + "* " + headPtr.getIdent() + ", ";
                    String y = this.y != null ? (this.y instanceof Constant ? String.valueOf(((Constant)(this.y)).value) : this.y.getIdent()) : String.valueOf(0);
                    output += priType.toString() + " 0, " + priType.toString() + " " + x + ", " + priType.toString() + " " + y;
                }
                else if (dimension1 != null) {
                    output += res.getIdent() + " = " + operator + " " + type + ", " + type + "* " + headPtr.getIdent();
                }
                else {
                    System.out.println("我决定尽量不让他到达这个语句1");
                    output = "我决定尽量不让他到达这个语句1";
                }
            }
            else {
                String x =this.x instanceof Constant ? String.valueOf(((Constant)(this.x)).value) : this.x.getIdent();
                String type = ArrayHandler.getInstance().getType(priType, dimension1, dimension2);
                if (dimension1 != null && dimension2 != null) {
                    output += res.getIdent() + " = " + operator + " " + type + ", " + type + "* " + headPtr.getIdent() + ", ";
                    if (this.y == null) {
                        output += priType.toString() + " 0, " + priType.toString() + " " + x;
                    }
                    else {
                        String y = this.y instanceof Constant ? String.valueOf(((Constant) (this.y)).value) : this.y.getIdent();
                        output += priType.toString() + " 0, " + priType.toString() + " " + x + ", " + priType.toString() + " " + y;
                    }
                }
                else if (dimension1 != null) {
                    output += res.getIdent() + " = " + operator + " " + type + ", " + type + "* " + headPtr.getIdent() + ", ";
                    output += priType.toString() + " 0" + ", " + priType.toString() + " " + x;
                }
                else {
                    System.out.println("我决定尽量不让他到达这个语句2");
                    output = "我决定尽量不让他到达这个语句2";
                }
            }
        }
        else {
            String x = this.x instanceof Constant ? String.valueOf(((Constant)(this.x)).value) : this.x.getIdent();
            String type = ArrayHandler.getInstance().getType(priType, col, null);
            output += res.getIdent() + " = " + operator + " " + type + ", " + type + "* " + headPtr.getIdent() + ", ";
            output += priType.toString() + " " + x;
            if (this.y != null) {
                String y = this.y instanceof Constant ? String.valueOf(((Constant)(this.y)).value) : this.y.getIdent();
                output += ", " + priType.toString() + " " + y;
            }
        }
//        if (output.equals("")) {
//            System.out.println("getElePtr is empty");
//        }
        IO.getIO().writelnToLLVM(output);
    }
}
