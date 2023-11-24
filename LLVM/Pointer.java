package LLVM;

public class Pointer extends Value{
    public Value.Type pointToType;
    public Pointer(String ident,Type pointToType) {
        super(ident, Type.Pointer);
        this.pointToType = pointToType;
    }
    public String getType() {
        switch (pointToType) {
            case _i32:
                return "i32*";
            case _i8:
                return "i8*";
            case _i1:
                return "i1*";
            default:
                return "";
        }
    }
}
