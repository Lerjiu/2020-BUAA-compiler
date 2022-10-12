import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Lexer {
    public static int lineNum = 1;
    public static char c;
    public static StringBuilder token = new StringBuilder();
    public static long point = 0;
    public static long markCharNum = 0;
    public static long getCharNum = 0;
    public static long preReadLength = 0;

    public static int getChar() {
        try {
            point = Compiler.srcInput.getFilePointer();
            int readNum = Compiler.srcInput.read();
            if (readNum == -1) {
                return -1;
            } else {
                c = (char) readNum;
                getCharNum++;
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isEmpty() {
        if (c == '\n') lineNum++;
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    public static boolean isLetter() {
        return Character.isLetter(c) || c == '_';
    }

    public static boolean isDigit() {
        return Character.isDigit(c);
    }

    public static boolean isAllowedChar() {
        if (c == 32 || c == 33 || (c >= 40 && c <= 91) || (c >= 93 && c <= 126)) {
            return true;
        } else if (c == '%') {
            catToken();
            getChar();
            if (c == 'd') {
                return true;
            } else {
                return false;
            }
        } else if (c == '\\') {
            catToken();
            getChar();
            if (c == 'n') {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void catToken() {
        token.append(c);
    }

    public static void retract() {
        try {
            Compiler.srcInput.seek(point);
            getCharNum--;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isReserved() {
        return Word.reserved.containsKey(token.toString());
    }

    public static void error() {
        return;
    }

    public static Word getSymbol() throws IOException {
        token.delete(0,token.length());
        Word symbol = new Word(Word.end, token.toString(), lineNum);
        if (getChar() != -1) {
            while (isEmpty()) {
                if (getChar() == -1) {
                    return null;
                }
            }
            if (isLetter()) {
                do {
                    catToken();
                    getChar();
                } while (isLetter() || isDigit());
                retract();
                if (isReserved()) {
                    symbol = new Word(Word.reserved.get(token.toString()), token.toString(), lineNum);
                } else {
                    symbol = new Word(Word.Ident, token.toString(), lineNum);
                }
            } else if (isDigit()) {
                if (c == '0') {
                    catToken();
                    getChar();
                    if (isDigit()) {
                        retract();
                        error();
                        return null;
                    } else {
                        retract();
                        symbol = new Word(Word.IntConst, token.toString(), lineNum);
                    }
                } else {
                    do {
                        catToken();
                        getChar();
                    } while (isDigit());
                    retract();
                    symbol = new Word(Word.IntConst, token.toString(), lineNum);
                }
            } else if (c == '<') {
                getChar();
                if (c == '=') {
                    symbol = new Word(Word.leq, "<=", lineNum);
                } else {
                    retract();
                    symbol = new Word(Word.lss, "<", lineNum);
                }
            } else if (c == '>') {
                getChar();
                if (c == '=') {
                    symbol = new Word(Word.geq, ">=", lineNum);
                } else {
                    retract();
                    symbol = new Word(Word.gre, ">", lineNum);
                }
            } else if (c == '!') {
                getChar();
                if (c == '=') {
                    symbol = new Word(Word.neq, "!=", lineNum);
                } else {
                    retract();
                    symbol = new Word(Word.not, "!", lineNum);
                }
            } else if (c == '=') {
                getChar();
                if (c == '=') {
                    symbol = new Word(Word.eql, "==", lineNum);
                } else {
                    retract();
                    symbol = new Word(Word.assign, "=", lineNum);
                }
            } else if (c == '|') {
                getChar();
                if (c == '|') {
                    symbol = new Word(Word.or, "||", lineNum);
                } else {
                    retract();
                    error();
                    return null;
                }
            } else if (c == '&') {
                getChar();
                if (c == '&') {
                    symbol = new Word(Word.and, "&&", lineNum);
                } else {
                    retract();
                    error();
                    return null;
                }
            } else if (c == '"') {
                do {
                    catToken();
                    getChar();
                } while (isAllowedChar());
                if (c == '"') {
                    catToken();
                    symbol = new Word(Word.FormatString, token.toString(), lineNum);
                } else {
                    error();
                    return null;
                }
            } else if (c == '/') {
                getChar();
                if (c == '/') {
                    do {
                        getChar();
                    } while (c != '\n');
                    return getSymbol();
                } else if (c == '*') {
                    char pre = c;
                    getChar();
                    char now = c;
                    do {
                        pre = c;
                        getChar();
                        now = c;
                    } while (!(pre == '*' && now == '/'));
                    return getSymbol();
                } else {
                    retract();
                    symbol = new Word(Word.div, "/", lineNum);
                }
            } else if (c == '+') {
                symbol = new Word(Word.plus, "+", lineNum);
            } else if (c == '-') {
                symbol = new Word(Word.minu, "-", lineNum);
            } else if (c == '*') {
                symbol = new Word(Word.mult, "*", lineNum);
            } else if (c == '%') {
                symbol = new Word(Word.mod, "%", lineNum);
            } else if (c == '(') {
                symbol = new Word(Word.lparent, "(", lineNum);
            } else if (c == ')') {
                symbol = new Word(Word.rparent, ")", lineNum);
            } else if (c == '[') {
                symbol = new Word(Word.lbrack, "[", lineNum);
            } else if (c == ']') {
                symbol = new Word(Word.rbrack, "]", lineNum);
            } else if (c == '{') {
                symbol = new Word(Word.lbrace, "{", lineNum);
            } else if (c == '}') {
                symbol = new Word(Word.rbrace, "}", lineNum);
            } else if (c == ';') {
                symbol = new Word(Word.semicn, ";", lineNum);
            } else if (c == ',') {
                symbol = new Word(Word.comma, ",", lineNum);
            }
        } else {
            if (!token.toString().equals("")) {
                error();
            }
        }
        return symbol;
    }

    public static Word preRead() throws IOException {
        markCharNum = getCharNum;
        Word preSymbol = getSymbol();
        if (preSymbol.getCategory().equals(Word.end)) {
            Parse.error("");
            System.exit(0);
        }
        preReadLength += getCharNum - markCharNum;
        return preSymbol;
    }

    public static void undoPreRead() throws IOException {
        Compiler.srcInput.seek(Compiler.srcInput.getFilePointer() - preReadLength);
        preReadLength = 0;
    }
}
