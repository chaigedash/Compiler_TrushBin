package Utils;

public class Config {
    private static final Config INSTANCE = new Config();
    public static Config getInstance() {
        return INSTANCE;
    }
    public boolean Lexer = true; // 无用
    public boolean Parser = true;
    public boolean llVM = true;
    public boolean Error = true; // 无聊
    public boolean llvmOUTPUTMODE = false; // true: both, false: onlyToFILE
//    public String srcFileExtension = "c";
    public String srcFileExtension = "txt";
}
