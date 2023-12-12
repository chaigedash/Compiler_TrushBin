package LLVM;

public class Argument extends Value {
    public String name;
    public Integer col;
    public Argument(String ident, Type type, String name, Integer col) {
        super(ident, type);
        this.name = name;
        this.col = col;
    }
}
