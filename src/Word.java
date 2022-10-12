import java.util.HashMap;

public class Word {
    public static final String Ident = "IDENFR";
    public static final String IntConst = "INTCON";
    public static final String FormatString = "STRCON";
    public static final String _main = "MAINTK";
    public static final String _const = "CONSTTK";
    public static final String _int = "INTTK";
    public static final String _break = "BREAKTK";
    public static final String _continue = "CONTINUETK";
    public static final String _if = "IFTK";
    public static final String _else = "ELSETK";
    public static final String not = "NOT";
    public static final String and = "AND";
    public static final String or = "OR";
    public static final String _while = "WHILETK";
    public static final String _getint = "GETINTTK";
    public static final String _printf = "PRINTFTK";
    public static final String _return = "RETURNTK";
    public static final String _void = "VOIDTK";
    public static final String plus = "PLUS";
    public static final String minu = "MINU";
    public static final String mult = "MULT";
    public static final String div = "DIV";
    public static final String mod = "MOD";
    public static final String lss = "LSS";
    public static final String leq = "LEQ";
    public static final String gre = "GRE";
    public static final String geq = "GEQ";
    public static final String eql = "EQL";
    public static final String neq = "NEQ";
    public static final String assign = "ASSIGN";
    public static final String semicn = "SEMICN";
    public static final String comma = "COMMA";
    public static final String lparent = "LPARENT";
    public static final String rparent = "RPARENT";
    public static final String lbrack = "LBRACK";
    public static final String rbrack = "RBRACK";
    public static final String lbrace = "LBRACE";
    public static final String rbrace = "RBRACE";
    public static final String end = "END";

    public static final HashMap<String, String> reserved = new HashMap<>();
    static {
        reserved.put("main", _main);
        reserved.put("const", _const);
        reserved.put("int", _int);
        reserved.put("break", _break);
        reserved.put("continue", _continue);
        reserved.put("if", _if);
        reserved.put("else", _else);
        reserved.put("while", _while);
        reserved.put("getint", _getint);
        reserved.put("printf", _printf);
        reserved.put("return", _return);
        reserved.put("void", _void);
    }

    private String category;
    private String value;
    private int numValue;
    private int lineNum;

    public Word(String category, String value, int lineNum) {
        this.category = category;
        this.value = value;
        this.lineNum = lineNum;
        if (category.equals(IntConst)) {
            this.numValue = Integer.parseInt(value);
        }
    }

    public String getCategory() {
        return this.category;
    }

    public String getValue() {
        return this.value;
    }
}
