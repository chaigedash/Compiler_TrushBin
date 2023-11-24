package Utils;

import Error.ErrorType;
import java.io.*;


public class IO {
    File srcFile;
    File destFile;
    InputStreamReader inputStreamReader = null;
    OutputStreamWriter outputStreamWriter = null;

    private static final IO Instance = new IO();
    public static IO getIO () {
        return Instance;
    }
    public void setSrcFile(String Path) {
        this.srcFile = new File(Path);
    }
    public void initFile(String Path) {
        this.destFile = new File(Path);
        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(destFile));
            outputStreamWriter.write("");
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setDestFile(String Path) {
        this.destFile = new File(Path);
    }
    public String read () {
        String source = "";
        try {
            if(!destFile.exists())
                destFile.createNewFile();
            inputStreamReader = new InputStreamReader(new FileInputStream(srcFile));
//            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(destFile));
            int c;
            while ((c = inputStreamReader.read()) != -1) {
                source += (char) c;
            }
//            System.out.println("\n"+source);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return source;
    }
    public void writeCore(String str, String destFile) {
        try {
            setDestFile(destFile);
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(destFile, true));
            outputStreamWriter.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void writeln (String str) {
        writeCore(str + '\n', "output.txt");
    }
//    public void write (String str) {
//        writeCore(str, "output.txt");
//    }
    public void writeToLLVM(String str) {
        writeCore(str, "llvm_ir.txt");
    }
    public void writelnToLLVM(String str) {
        writeCore(str + '\n', "llvm_ir.txt");
    }
    public void writeln (int line, ErrorType type) {
        writeCore(Integer.valueOf(line).toString() + " " + type.toString() + '\n', "error.txt");
    }
}
