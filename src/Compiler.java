import java.io.*;

public class Compiler {
    public static RandomAccessFile srcInput = null;
    public static BufferedWriter dstOutput = null;

    public static void main(String[] args) {
        try {
            srcInput = new RandomAccessFile("testfile.txt", "r");
            dstOutput = new BufferedWriter(new FileWriter("output.txt"));
            Parse.symbol = Lexer.getSymbol();
            if (Parse.symbol.getCategory().equals(Word.end)) {
                System.out.println("文件为空");
            } else {
                Parse.CompUnit();
            }
            srcInput.close();
            dstOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
