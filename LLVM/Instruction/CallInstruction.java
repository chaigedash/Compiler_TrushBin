package LLVM.Instruction;

import LLVM.BasicBlock;
import LLVM.Constant;
import LLVM.Function;
import LLVM.Value;
import Utils.IO;

import java.util.ArrayList;

public class CallInstruction extends Instruction{
    //    <result> = call [ret attrs] <ty> <fnptrval>(<function args>)
    private Value result;
    private Function function;
    private ArrayList<Value> params = new ArrayList<Value>();
    public CallInstruction(BasicBlock basicBlock, Value result, Function function, ArrayList<Value> params) {
        // 有返回值，有参数
        super(Operator.Call, basicBlock);
        this.result = result;
        this.function = function;
        this.params = params;
    }
    public CallInstruction(BasicBlock basicBlock, Value result, Function function) {
        // 有返回值，无参数
        super(Operator.Call, basicBlock);
        this.result = result;
        this.function = function;
    }
    public CallInstruction(BasicBlock basicBlock, Function function, ArrayList<Value> params) {
        // 无返回值，有参数
        super(Operator.Call, basicBlock);
        this.function = function;
        this.params = params;
    }
    public CallInstruction(BasicBlock basicBlock, Function function) {
        // 无返回值，无参数
        super(Operator.Call, basicBlock);
        this.function = function;
    }
    public void print () {
        String output = "";
        if (result != null) {
            output = result.getIdent() + " = " + operator + " i32 " + function.getIdent();
        }
        else {
            output = operator + " void " + function.getIdent();
        }
        if (params.size() > 0) {
            output += "(";
            for (int j = 0; j < params.size(); j++) {
                Value param = params.get(j);
                if (j != 0) {
                    output += ", ";
                }
                if (param instanceof Constant) {
                    output += param.getType() + " " + ((Constant) param).value;
                }
                else {
                    output += param.getType() + " " + param.getIdent();
                }
            }
            output += ")";
        }
        else {
            output += "()";
        }
//        System.out.println(output);
        IO.getIO().writelnToLLVM(output);
    }
}
