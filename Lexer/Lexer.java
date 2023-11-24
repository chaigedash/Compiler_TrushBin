package Lexer;

import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private static final Lexer Instance = new Lexer();
    public static Lexer getLexer(){
        return Instance;
    }
    public void setSource(String src){
        this.source = src;
        this.curPos = 0;
        this.lineNum = 1;
    }

    private String source;
    private int curPos;
    //    private String token;
    private LexType lexType;
    private int lineNum;
    private int number;

    private Map<String, LexType> reserveWords = new HashMap<String, LexType>() {{
//        put("Ident", LexType.IDENFR);
//        put("IntConst", LexType.INTCON);
//        put("FormatString", LexType.STRCON);
        put("main", LexType.MAINTK);
        put("const", LexType.CONSTTK);
        put("int", LexType.INTTK);
        put("break", LexType.BREAKTK);
        put("continue", LexType.CONTINUETK);
        put("if", LexType.IFTK);
        put("else", LexType.ELSETK);
        put("for", LexType.FORTK);
        put("getint", LexType.GETINTTK);
        put("printf", LexType.PRINTFTK);
        put("return", LexType.RETURNTK);
        put("void", LexType.VOIDTK);
    }};
    private Map<String, LexType> reserveSymbols = new HashMap<String, LexType>() {{
        put("+",LexType.PLUS);
        put("-",LexType.MINU);
        put("*",LexType.MULT);
        put("%",LexType.MOD);
        put(";",LexType.SEMICN);
        put(",",LexType.COMMA);
        put("(",LexType.LPARENT);
        put(")",LexType.RPARENT);
        put("[",LexType.LBRACK);
        put("]",LexType.RBRACK);
        put("{",LexType.LBRACE);
        put("}",LexType.RBRACE);
        //
        put("/",LexType.DIV);
        put("=",LexType.ASSIGN);
        put("!",LexType.NOT);
        put("<",LexType.LSS);
        put(">",LexType.GRE);
        put("<=",LexType.LEQ);
        put(">=",LexType.GEQ);
        put("==",LexType.EQL);
        put("!=",LexType.NEQ);
        put("&&",LexType.AND);
        put("||",LexType.OR);
    }};
    public Word next(){
        deleteInvisible();
        if(curPos >= source.length()) {
            return null;
        }
        char c = source.charAt(curPos);
//        System.out.println("c=" + (int)c + " " + c);
        String token = "";
        if(isLetter(c)) {
//            token += c;
            while(curPos < source.length() &&
                    (isLetter(source.charAt(curPos)) || isDigit(source.charAt(curPos)))) {
                // 下一个字符为数字或字母
                c = source.charAt(curPos++);
                token += c;
//                System.out.println("c = " + c + " token = " + token);
            }
            lexType = isReserve(token, 1); // 查关键字表
            if(lexType == null || source.charAt(curPos) == '_'){
                lexType = LexType.IDENFR;
                while(curPos < source.length() &&
                        (source.charAt(curPos) == '_' || isLetter(source.charAt(curPos)) || isDigit(source.charAt(curPos)))) {
                    c = source.charAt(curPos++);
                    token += c;
                }
            }
//            deleteInvisible();
        }
        else if(isDigit(c)) { // 无符号整数
//            token += c;
            while(curPos < source.length() && isDigit(source.charAt(curPos))) {
                // 下一个符号是数字
                c = source.charAt(curPos++);
                token += c;
            }
//            System.out.println("num" + token);
            lexType = LexType.INTCON; // 设置单词类别
            number = Integer.valueOf(token); // 转化为数值
            return new Word(lexType, number, lineNum, true);
        }
        else if(c == '/') { // 第一个 /
            token += c;
            curPos++;
            if(curPos < source.length() && source.charAt(curPos) == '/') { // // 第二个 /
                c = source.charAt(curPos); //
                token += c;
                while (curPos < source.length() && source.charAt(curPos) != '\n') { // 非换行字符
                    c = source.charAt(curPos++);
                    token += c;
                }
                if (source.charAt(curPos) == '\n') {
                    c = source.charAt(curPos++);
                    token += c;
                    lineNum++; // 单行注释末尾的\n
                }
//                lexType = LexType.NOTE;
//                deleteInvisible();
                return new Word(lexType, token, lineNum, false);
            }
            else if(curPos < source.length() && source.charAt(curPos) == '*') { // /* 跨行注释 用状态机判断
                c = source.charAt(curPos);
                token += c;
                while (curPos < source.length()) {  // 状态转换循环（直至末尾）
                    while (curPos < source.length() && source.charAt(curPos) != '*') {
                        // 非*字符 对应状态q5
                        c = source.charAt(curPos++);
                        token += c;
                        if (c == '\n') lineNum++; // 多行注释中 每行最后的回车
                    }
                    // *
                    while (curPos < source.length() && source.charAt(curPos) == '*') {
                        // *字符 对应状态q6 如果没有转移到q7，则会在循环中转移到q5
                        c = source.charAt(curPos++);
                        token += c;
                    }
                    if (curPos < source.length() && source.charAt(curPos) == '/') {
                        // /字符 对应状态q7
                        c = source.charAt(curPos++);
                        token += c;
//                        lexType = LexType.NOTE;
//                        deleteInvisible();
                        return new Word(lexType, token, lineNum, false);
                    }
                }
            }
            else {
                lexType = LexType.DIV;
//                deleteInvisible();
            }
        }
        else if (isSymbol(c)) {
            token += c;
            curPos++;
            if(c == '<' || c == '>' || c == '=' || c == '!'){
                if(curPos < source.length() && source.charAt(curPos) == '='){
                    c = source.charAt(curPos++);
                    token += c;
                }
                lexType = isReserve(token, 2); // 查关键字表
//                deleteInvisible();
            }
            else if (c == '&' || c == '|'){
                c = source.charAt(curPos++);
                token += c;
                lexType = isReserve(token, 2); // 查关键字表
//                deleteInvisible();
            }
            else if (c == '"') {
//                c = source.charAt(curPos); //
//                token += c;
                while (curPos < source.length() && source.charAt(curPos) != '"') {
                    c = source.charAt(curPos++);
                    token += c;
                }
                c = source.charAt(curPos++);
                token += c;
                lexType = LexType.STRCON;
//                deleteInvisible();
            }
            else if (c == '_') {
                lexType = LexType.IDENFR;
                while(curPos < source.length() &&
                        (source.charAt(curPos) == '_' || isLetter(source.charAt(curPos)) || isDigit(source.charAt(curPos)))) {
                    c = source.charAt(curPos++);
                    token += c;
                }
//                deleteInvisible();
            } else{
                lexType = isReserve(token, 2); // 查关键字表
                if(lexType != null) {
//                    deleteInvisible();
                }
                else if(curPos < source.length() && isSymbol(source.charAt(curPos))){
                    token += source.charAt(curPos++);
                    lexType = isReserve(token, 2); // 查关键字表
//                    deleteInvisible();
                } else {
                    System.out.println("fuck symbol");
                    return null;
                }
            }
        }
        else {
            System.out.println("fuck character");
        }
        return new Word(lexType, token, lineNum, true);
    }
    public String getToken(){
        return null;
    }
    public LexType getLexType(){
        return null;
    }
    private boolean isLetter(char c){
        if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') return true;
        else return false;
    }
    private boolean isSymbol(char c){
        if(!isLetter(c) && !isDigit(c)) return true;
        else return false;
    }
    private boolean isDigit(char c){
        if(c >= '0' && c <= '9') return true;
        else return false;
    }
    private LexType isReserve(String str, int mode){ // mode1: word // mode2: symbol
        LexType lt = null;
        str = str.trim();
//        System.out.println(str);
        if(mode == 1){
            lt = reserveWords.get(str);
        }
        else if(mode == 2){
            lt = reserveSymbols.get(str);
        }
        return lt;
    }
    private void deleteInvisible(){
        while(curPos < source.length()){
            if(source.charAt(curPos) == ' ' || source.charAt(curPos) == '\t'){
                curPos++;
            }
            else if (source.charAt(curPos) == '\n'){
                curPos++;
                lineNum++;
            }
            else if (source.charAt(curPos) == '\r'){
                curPos++;
            }
            else return;
        }
    }
}
