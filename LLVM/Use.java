package LLVM;

public class Use {
    private Use preUse;
    private Use nextUse;
    private Value value;
    private User user;
    public Use(Value value, User user) {
        this.value = value;
        this.user = user;
    }
}
