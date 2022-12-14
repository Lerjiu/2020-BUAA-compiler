import java.io.IOException;

public class Parse {
    public static final String compUnit = "CompUnit";
    public static final String decl = "Decl";
    public static final String constDecl = "ConstDecl";
    public static final String bType = "BType";
    public static final String constDef = "ConstDef";
    public static final String constInitVal = "ConstInitVal";
    public static final String varDecl = "VarDecl";
    public static final String varDef = "VarDef";
    public static final String initVal = "InitVal";
    public static final String funcDef = "FuncDef";
    public static final String mainFuncDef = "MainFuncDef";
    public static final String funcType = "FuncType";
    public static final String funcFParams = "FuncFParams";
    public static final String funcFParam = "FuncFParam";
    public static final String block = "Block";
    public static final String blockItem = "BlockItem";
    public static final String stmt = "Stmt";
    public static final String exp = "Exp";
    public static final String cond = "Cond";
    public static final String lVal = "LVal";
    public static final String primaryExp = "PrimaryExp";
    public static final String number = "Number";
    public static final String unaryExp = "UnaryExp";
    public static final String unaryOp = "UnaryOp";
    public static final String funcRParams = "FuncRParams";
    public static final String mulExp = "MulExp";
    public static final String addExp = "AddExp";
    public static final String relExp = "RelExp";
    public static final String eqExp = "EqExp";
    public static final String lAndExp = "LAndExp";
    public static final String lOrExp = "LOrExp";
    public static final String constExp = "ConstExp";

    public static Word symbol = null;

    public static void error(String msg) {
        return;
    }

    public static void output(String component) throws IOException {
        Compiler.dstOutput.append('<' + component + '>');
        Compiler.dstOutput.newLine();
    }
    
    public static void getSymbol() throws IOException {
        Compiler.dstOutput.append(symbol.getCategory());
        Compiler.dstOutput.append(' ');
        Compiler.dstOutput.append(symbol.getValue());
        Compiler.dstOutput.newLine();
        symbol = Lexer.getSymbol();
    }
    
    public static boolean symbolIs(String category) {
        if (symbol.getCategory().equals(Word.end)) {
            error("??????" + category);
            System.exit(0);
        }
        return symbol.getCategory().equals(category);
    }

    public static void CompUnit() throws IOException {
        Word pre1 = null;
        Word pre2 = null;
        boolean isMain = false;
        do {
            if (symbolIs(Word._const)) {
                Decl();
            } else if (symbolIs(Word._void)) {
                FuncDef();
            } else if (symbolIs(Word._int)) {
                pre1 = Lexer.preRead();
                pre2 = Lexer.preRead();
                if (pre1.getCategory().equals(Word._main)) {
                    Lexer.undoPreRead();
                    if (pre2.getCategory().equals(Word.lparent)) {
                        isMain = true;
                        MainFuncDef();
                    } else {
                        Decl();
                    }
                } else if (pre2.getCategory().equals(Word.lparent)) {
                    Lexer.undoPreRead();
                    FuncDef();
                    do {
                        if (symbolIs(Word._void)) {
                            FuncDef();
                        } else {
                            pre1 = Lexer.preRead();
                            Lexer.undoPreRead();
                            if (pre1.getCategory().equals(Word._main)) {
                                isMain = true;
                                MainFuncDef();
                            } else {
                                FuncDef();
                            }
                        }
                    } while (!isMain);
                } else {
                    Lexer.undoPreRead();
                    Decl();
                }
            } else {
                error("???????????????????????????");
            }
        } while (!isMain);
        output(compUnit);
    }

    public static void Decl() throws IOException {
        if (symbolIs(Word._const)) {
            ConstDecl();
        } else if (symbolIs(Word._int)) {
            VarDecl();
        } else {
            error("????????????'const'???Btype");
        }
    }

    public static void FuncDef() throws IOException {
        FuncType();
        if (symbolIs(Word.Ident)) {
            getSymbol();
            if (symbolIs(Word.lparent)) {
                getSymbol();
                if (symbolIs(Word.rparent)) {
                    getSymbol();
                    Block();
                } else {
                    FuncFParams();
                    if (symbolIs(Word.rparent)) {
                        getSymbol();
                        Block();
                    } else {
                        error("??????????????????')'");
                    }
                }
            } else {
                error("??????????????????'('");
            }
        } else {
            error("???????????????????????????");
        }
        output(funcDef);
    }

    public static void MainFuncDef() throws IOException {
        if (symbolIs(Word._int)) {
            getSymbol();
            if (symbolIs(Word._main)) {
                getSymbol();
                if (symbolIs(Word.lparent)) {
                    getSymbol();
                    if (symbolIs(Word.rparent)) {
                        getSymbol();
                        Block();
                    } else {
                        error("main????????????')'");
                    }
                } else {
                    error("main????????????'('");
                }
            } else {
                error("main????????????'main'");
            }
        } else {
            error("main????????????'int'");
        }
        output(mainFuncDef);
    }

    public static void ConstDecl() throws IOException {
        if (symbolIs(Word._const)) {
            getSymbol();
            BType();
            ConstDef();
            while (symbolIs(Word.comma)) {
                getSymbol();
                ConstDef();
            }
            if (!symbolIs(Word.semicn)) {
                error("??????????????????';'");
            }
        } else {
            error("??????????????????'const'");
        }
        getSymbol();
        output(constDecl);
    }

    public static void VarDecl() throws IOException {
        BType();
        VarDef();
        while (symbolIs(Word.comma)) {
            getSymbol();
            VarDef();
        }
        if (!symbolIs(Word.semicn)) {
            error("??????????????????';'");
        }
        getSymbol();
        output(varDecl);
    }

    public static void FuncType() throws IOException {
        if (!symbolIs(Word._void) && !symbolIs(Word._int)) {
            error("??????????????????'void'???'int'");
        }
        getSymbol();
        output(funcType);
    }

    public static void Block() throws IOException {
        if (symbolIs(Word.lbrace)) {
            getSymbol();
            while (!symbolIs(Word.rbrace)) {
                BlockItem();
            }
        } else {
            error("???????????????'{'");
        }
        getSymbol();
        output(block);
    }

    public static void FuncFParams() throws IOException {
        FuncFParam();
        while (symbolIs(Word.comma)) {
            getSymbol();
            FuncFParam();
        }
        output(funcFParams);
    }

    public static void BType() throws IOException {
        if (!symbolIs(Word._int)) {
            error("??????????????????'int'");
        }
        getSymbol();
    }

    public static void ConstDef() throws IOException {
        if (symbolIs(Word.Ident)) {
            getSymbol();
            while (symbolIs(Word.lbrack)) {
                getSymbol();
                ConstExp();
                if (symbolIs(Word.rbrack)) {
                    getSymbol();
                } else {
                    error("????????????????????????']'");
                }
            }
            if (symbolIs(Word.assign)) {
                getSymbol();
                ConstInitVal();
            } else {
                error("??????????????????'='");
            }
        } else {
            error("???????????????????????????");
        }
        output(constDef);
    }

    public static void VarDef() throws IOException {
        if (symbolIs(Word.Ident)) {
            getSymbol();
            while (symbolIs(Word.lbrack)) {
                getSymbol();
                ConstExp();
                if (symbolIs(Word.rbrack)) {
                    getSymbol();
                } else {
                    error("????????????????????????']'");
                }
            }
            if (symbolIs(Word.assign)) {
                getSymbol();
                InitVal();
            }
        } else {
            error("???????????????????????????");
        }
        output(varDef);
    }

    public static void BlockItem() throws IOException {
        if (symbolIs(Word._const) || symbolIs(Word._int)) {
            Decl();
        } else {
            Stmt();
        }
    }

    public static void FuncFParam() throws IOException {
        BType();
        if (symbolIs(Word.Ident)) {
            getSymbol();
            if (symbolIs(Word.lbrack)) {
                getSymbol();
                if (symbolIs(Word.rbrack)) {
                    getSymbol();
                    while (symbolIs(Word.lbrack)) {
                        getSymbol();
                        ConstExp();
                        if (symbolIs(Word.rbrack)) {
                            getSymbol();
                        } else {
                            error("????????????????????????']'");
                        }
                    }
                } else {
                    error("????????????????????????']'");
                }
            }
        } else {
            error("???????????????????????????");
        }
        output(funcFParam);
    }

    public static void ConstExp() throws IOException {
        AddExp();
        output(constExp);
    }

    public static void ConstInitVal() throws IOException {
        if (symbolIs(Word.lbrace)) {
            getSymbol();
            if (symbolIs(Word.rbrace)) {
                getSymbol();
            } else {
                ConstInitVal();
                while (symbolIs(Word.comma)) {
                    getSymbol();
                    ConstInitVal();
                }
                if (symbolIs(Word.rbrace)) {
                    getSymbol();
                } else {
                    error("??????????????????'}'");
                }
            }
        } else {
            ConstExp();
        }
        output(constInitVal);
    }

    public static void InitVal() throws IOException {
        if (symbolIs(Word.lbrace)) {
            getSymbol();
            if (symbolIs(Word.rbrace)) {
                getSymbol();
            } else {
                InitVal();
                while (symbolIs(Word.comma)) {
                    getSymbol();
                    InitVal();
                }
                if (symbolIs(Word.rbrace)) {
                    getSymbol();
                } else {
                    error("??????????????????'}'");
                }
            }
        } else {
            Exp();
        }
        output(initVal);
    }

    public static void Stmt() throws IOException {
        if (symbolIs(Word.lbrace)) {
            Block();
        } else if (symbolIs(Word._if)) {
            getSymbol();
            if (symbolIs(Word.lparent)) {
                getSymbol();
                Cond();
                if (symbolIs(Word.rparent)) {
                    getSymbol();
                    Stmt();
                    if (symbolIs(Word._else)) {
                        getSymbol();
                        Stmt();
                    }
                } else {
                    error("if????????????')'");
                }
            } else {
                error("if????????????'('");
            }
        } else if (symbolIs(Word._while)) {
            getSymbol();
            if (symbolIs(Word.lparent)) {
                getSymbol();
                Cond();
                if (symbolIs(Word.rparent)) {
                    getSymbol();
                    Stmt();
                } else {
                    error("while????????????')'");
                }
            } else {
                error("while????????????'('");
            }
        } else if (symbolIs(Word._break)) {
            getSymbol();
            if (symbolIs(Word.semicn)) {
                getSymbol();
            } else {
                error("break????????????';'");
            }
        } else if (symbolIs(Word._continue)) {
            getSymbol();
            if (symbolIs(Word.semicn)) {
                getSymbol();
            } else {
                error("continue????????????';'");
            }
        } else if (symbolIs(Word._return)) {
            getSymbol();
            if (symbolIs(Word.semicn)) {
                getSymbol();
            } else {
                Exp();
                if (symbolIs(Word.semicn)) {
                    getSymbol();
                } else {
                    error("return????????????';'");
                }
            }
        } else if (symbolIs(Word._printf)) {
            getSymbol();
            if (symbolIs(Word.lparent)) {
                getSymbol();
                if (symbolIs(Word.FormatString)) {
                    getSymbol();
                    while (symbolIs(Word.comma)) {
                        getSymbol();
                        Exp();
                    }
                    if (symbolIs(Word.rparent)) {
                        getSymbol();
                        if (symbolIs(Word.semicn)) {
                            getSymbol();
                        } else {
                            error("printf????????????';'");
                        }
                    } else {
                        error("printf????????????')'");
                    }
                } else {
                    error("printf?????????????????????");
                }
            } else {
                error("printf????????????'('");
            }
        } else if (symbolIs(Word.Ident)) {
            Word pre1 = Lexer.preRead();
            int lbrackNum = 0;
            while (pre1.getCategory().equals(Word.lbrack)) {
                lbrackNum++;
                while (!pre1.getCategory().equals(Word.rbrack) || lbrackNum != 0) {
                    pre1 = Lexer.preRead();
                    if (pre1.getCategory().equals(Word.rbrack)) {
                        lbrackNum--;
                    } else if (pre1.getCategory().equals(Word.lbrack)) {
                        lbrackNum++;
                    }
                }
                pre1 = Lexer.preRead();
            }
            if (pre1.getCategory().equals(Word.assign)) {
                pre1 = Lexer.preRead();
                Lexer.undoPreRead();
                if (pre1.getCategory().equals(Word._getint)) {
                    LVal();
                    getSymbol();
                    getSymbol();
                    if (symbolIs(Word.lparent)) {
                        getSymbol();
                        if (symbolIs(Word.rparent)) {
                            getSymbol();
                            if (symbolIs(Word.semicn)) {
                                getSymbol();
                            } else {
                                error("getint????????????';'");
                            }
                        } else {
                            error("getint????????????')'");
                        }
                    } else {
                        error("getint????????????'('");
                    }
                } else {
                    LVal();
                    getSymbol();
                    Exp();
                    if (symbolIs(Word.semicn)) {
                        getSymbol();
                    } else {
                        error("??????????????????';'");
                    }
                }
            } else {
                Lexer.undoPreRead();
                if (symbolIs(Word.semicn)) {
                    getSymbol();
                } else {
                    Exp();
                    if (symbolIs(Word.semicn)) {
                        getSymbol();
                    } else {
                        error("?????????????????????';'");
                    }
                }
            }
        } else {
            if (symbolIs(Word.semicn)) {
                getSymbol();
            } else {
                Exp();
                if (symbolIs(Word.semicn)) {
                    getSymbol();
                } else {
                    error("?????????????????????';'");
                }
            }
        }
        output(stmt);
    }

    public static void AddExp() throws IOException {
        MulExp();
        while (symbolIs(Word.plus) || symbolIs(Word.minu)) {
            output(addExp);
            getSymbol();
            MulExp();
        }
        output(addExp);
    }

    public static void Exp() throws IOException {
        AddExp();
        output(exp);
    }

    public static void MulExp() throws IOException {
        UnaryExp();
        while (symbolIs(Word.mult) || symbolIs(Word.div) || symbolIs(Word.mod)) {
            output(mulExp);
            getSymbol();
            UnaryExp();
        }
        output(mulExp);
    }

    public static void UnaryExp() throws IOException {
        if (symbolIs(Word.plus) || symbolIs(Word.minu) || symbolIs(Word.not)) {
            UnaryOp();
            UnaryExp();
        } else {
            if (symbolIs(Word.Ident)) {
                Word pre1 = Lexer.preRead();
                Lexer.undoPreRead();
                if (pre1.getCategory().equals(Word.lparent)) {
                    getSymbol();
                    getSymbol();
                    if (symbolIs(Word.rparent)) {
                        getSymbol();
                    } else {
                        FuncRParams();
                        if (symbolIs(Word.rparent)) {
                            getSymbol();
                        } else {
                            error("?????????????????????????????????')'");
                        }
                    }
                } else {
                    PrimaryExp();
                }
            } else {
                PrimaryExp();
            }
        }
        output(unaryExp);
    }

    public static void UnaryOp() throws IOException {
        if (!symbolIs(Word.plus) && !symbolIs(Word.minu) && !symbolIs(Word.not)) {
            error("?????????????????????");
        }
        getSymbol();
        output(unaryOp);
    }

    public static void FuncRParams() throws IOException {
        Exp();
        while (symbolIs(Word.comma)) {
            getSymbol();
            Exp();
        }
        output(funcRParams);
    }

    public static void PrimaryExp() throws IOException {
        if (symbolIs(Word.lparent)) {
            getSymbol();
            Exp();
            if (symbolIs(Word.rparent)) {
                getSymbol();
            } else {
                error("????????????????????????????????????')'");
            }
        } else if (symbolIs(Word.Ident)) {
            LVal();
        } else if (symbolIs(Word.IntConst)) {
            Number();
        } else {
            error("?????????????????????");
        }
        output(primaryExp);
    }

    public static void LVal() throws IOException {
        if (symbolIs(Word.Ident)) {
            getSymbol();
            while (symbolIs(Word.lbrack)) {
                getSymbol();
                Exp();
                if (symbolIs(Word.rbrack)) {
                    getSymbol();
                } else {
                    error("?????????????????????']'");
                }
            }
        } else {
            error("??????????????????????????????");
        }
        output(lVal);
    }

    public static void Number() throws IOException {
        if (symbolIs(Word.IntConst)) {
            getSymbol();
        } else {
            error("??????????????????");
        }
        output(number);
    }

    public static void Cond() throws IOException {
        LOrExp();
        output(cond);
    }

    public static void LOrExp() throws IOException {
        LAndExp();
        while (symbolIs(Word.or)) {
            output(lOrExp);
            getSymbol();
            LAndExp();
        }
        output(lOrExp);
    }

    public static void LAndExp() throws IOException {
        EqExp();
        while (symbolIs(Word.and)) {
            output(lAndExp);
            getSymbol();
            EqExp();
        }
        output(lAndExp);
    }

    public static void EqExp() throws IOException {
        RelExp();
        while (symbolIs(Word.eql) || symbolIs(Word.neq)) {
            output(eqExp);
            getSymbol();
            RelExp();
        }
        output(eqExp);
    }

    public static void RelExp() throws IOException {
        AddExp();
        while (symbolIs(Word.lss) || symbolIs(Word.gre) || symbolIs(Word.leq) || symbolIs(Word.geq)) {
            output(relExp);
            getSymbol();
            AddExp();
        }
        output(relExp);
    }
}
