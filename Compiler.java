import LLVM.LLVMAnalyzer;
import Lexer.Lexer;
import Lexer.Word;
import Parser.*;
import Parser.Node.CompUnit;
import Utils.Config;
import Utils.IO;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
//        System.out.println("Hello world!");

//        File srcFile = new File("testfile.txt");
//        File destFile = new File("output.txt");
//        InputStreamReader inputStreamReader = null;
//        OutputStreamWriter outputStreamWriter = null;
        IO io = IO.getIO();
        io.setSrcFile("testfile." + Config.getInstance().srcFileExtension);
        io.initFile("output.txt");
        io.initFile("llvm_ir.txt");
        io.initFile("error.txt");
        String source = io.read();
//        try {
//            if(!destFile.exists())
//                destFile.createNewFile();
//            inputStreamReader = new InputStreamReader(new FileInputStream(srcFile));
//            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(destFile));
//            int c;
//            while ((c = inputStreamReader.read()) != -1) {
////                System.out.print((char)c);
//                source += (char) c;
//            }
//            System.out.println("\n"+source);
        Lexer lexer = Lexer.getLexer();
        lexer.setSource(source);
        ArrayList<Word> words = new ArrayList<Word>();
        for (int i = 0; i < source.length(); i++) {
            Word t = lexer.next();
            if (t == null) {
                break;
            }
            if(t.print) {
                words.add(t);
//                System.out.println(t.print());
            }
//            if(t.print) {
////                outputStreamWriter.write(t.lexType.toString() + ' ' + t.word + '\n');
//                io.write(t.lexType.toString() + ' ' + t.word + '\n');
//            }
        }
        ParserAnalyzer parser = ParserAnalyzer.getParser();
        parser.setWords(words);
        CompUnit AST = parser.qidong();
        AST.print();
        if (Config.getInstance().llVM) {
            LLVMAnalyzer irBuilder = LLVMAnalyzer.getInstance();
            irBuilder.setCompUnit(AST);
            irBuilder.analyzeAST();
            IO.getIO().writelnToLLVM(
                    "declare i32 @getint()\n" +
                            "declare void @putint(i32)\n" +
                            "declare void @putch(i32)\n" +
                            "declare void @putstr(i8*)\n"
            );
            irBuilder.print();
        }
        System.out.println("--------------done--------------");
    }
}
