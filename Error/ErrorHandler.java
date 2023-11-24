package Error;


import Lexer.Word;
import Utils.IO;

public class ErrorHandler {

    private static final ErrorHandler Instance = new ErrorHandler();
    public static ErrorHandler getErrorHandler(){
        return Instance;
    }
//    public void handleError(String message, ErrorType errorType, int errorLine) {
//        IO.getIO().writeln(errorLine, errorType);
//    }
    public void handleError(ErrorType errorType, Word word) {
        IO.getIO().writeln(word.lineNum, errorType);
    }
}
