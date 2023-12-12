package LLVM;

public class Pointer extends Value{
    public Value.Type pointToType;
    public Integer col;
    public Pointer(String ident,Type pointToType) {
        super(ident, Type.Pointer);
        this.pointToType = pointToType;
    }
    public String getType() {
        if (col != null) {
            return "[" + col + " x " + pointToType +"]*";
        }
        else {
            return pointToType + "*";
        }
    }
}
