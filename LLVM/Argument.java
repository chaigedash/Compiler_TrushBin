package LLVM;

public class Argument extends Value {
    public String name;
    public Argument(String ident, Type type, String name) {
        super(ident, type);
        this.name = name;
    }
}
