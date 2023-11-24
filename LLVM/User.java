package LLVM;

import java.util.ArrayList;

public class User extends Value {
    private ArrayList<Value> operands = new ArrayList<Value>();
    public User (String ident, Value.Type type) {
        super(ident, type);
    }
}
