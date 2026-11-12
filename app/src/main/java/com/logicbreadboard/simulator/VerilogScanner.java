package com.logicbreadboard.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by laci on 10/23/15.
 */
public class VerilogScanner {
    static final char SPACE = ' ';
    static final char NEW_LINE = '\n';
    static final char TAB = '\t';
    char ch;
    char nextCh;
    boolean eof;
    HashMap<String, VerilogToken.Symbols> keyWordMap;
    StringBuilder stringBuilder = new StringBuilder();

    VerilogScanner() {
        keyWordMap = new HashMap<String, VerilogToken.Symbols>();
//        for (VerilogToken.Tokens kw : VerilogToken.Tokens.values()) {
        for (int kwi = VerilogToken.Symbols.ALWAYS.ordinal(); kwi <= VerilogToken.Symbols.XOR.ordinal();
             kwi++) {
            VerilogToken.Symbols kw = VerilogToken.Symbols.values()[kwi];
            keyWordMap.put(kw.name().toLowerCase(), kw);
        }
    }

    public VerilogToken scan(String fileName) {
        FileInputStream in = null;
        VerilogToken token = new VerilogToken();
        VerilogToken firstToken = token;
        eof = false;
        try {
            File oldFile = new File(fileName);
            if (!oldFile.exists()) {
                return null;
            }
//            Log.i("scan", "################ fileName = " + fileName +
//                    " ################");
            try {
                in = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            getChar(in); //read nextCh;
            getChar(in); //ch is valid now;
            while (ch == '/' || ch == SPACE || ch == TAB || ch == NEW_LINE) {
                skipWhiteSpaces(in);
                skipComment(in);
            }
            while (!eof) {
                token.next = new VerilogToken();
                token = token.next;
                token.sym = VerilogToken.Symbols.UNKNOWN;
                if (ch >= 'a' && ch <= 'z') { //identifier, can be keyword
                    getKeywordCandidateIdent(in, token);
                } else if (ch >= 'A' && ch <= 'Z' ||
                        ch == '_') { //identifier
                    getIdentifier(in, token);
                } else if (ch == '$') { // system identifier
                    getSystemIdentifier(in, token);
                } else if (ch == '\\') { // escaped identifier
                    getEscapedIdentifier(in, token);
                } else if (ch >= '0' && ch <= '9') {
                    getNumber(in, token);
                } else if (ch == '(') {
                    token.sym = VerilogToken.Symbols.SYM_LEFT_BRACKET;
                    getChar(in);
                } else if (ch == ')') {
                    token.sym = VerilogToken.Symbols.SYM_RIGHT_BRACKET;
                    getChar(in);
                } else if (ch == '[') {
                    token.sym = VerilogToken.Symbols.SYM_LEFT_SQUARE_BRACKET;
                    getChar(in);
                } else if (ch == ']') {
                    token.sym = VerilogToken.Symbols.SYM_RIGHT_SQUARE_BRACKET;
                    getChar(in);
                } else if (ch == '{') {
                    token.sym = VerilogToken.Symbols.SYM_LEFT_CURLY_BRACKET;
                    getChar(in);
                } else if (ch == '}') {
                    token.sym = VerilogToken.Symbols.SYM_RIGHT_CURLY_BRACKET;
                    getChar(in);
                } else if (ch == ':') {
                    token.sym = VerilogToken.Symbols.SYM_COLON;
                    getChar(in);
                } else if (ch == ';') {
                    token.sym = VerilogToken.Symbols.SYM_SEMICOLON;
                    getChar(in);
                } else if (ch == '=') {
                    if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_EQ_EQ;
                        getChar(in);
                        if (nextCh == '=') {
                            token.sym = VerilogToken.Symbols.SYM_EQ_EQ_EQ;
                            getChar(in);
                        }
                    } else if (nextCh == '?') {
                        getChar(in);
                        if (nextCh == '?') {
                            token.sym = VerilogToken.Symbols.SYM_EQ_QUESTION_EQ;
                            getChar(in);
                        }
                    } else {
                        token.sym = VerilogToken.Symbols.SYM_EQ;
                    }
                    getChar(in);
                } else if (ch == '~') {
                    token.sym = VerilogToken.Symbols.SYM_TILDE;
                    if (nextCh == '|') {
                        token.sym = VerilogToken.Symbols.SYM_NOR;
                        getChar(in);
                    } else if (nextCh == '&') {
                        token.sym = VerilogToken.Symbols.SYM_NAND;
                        getChar(in);
                    } else if (nextCh == '^') {
                        token.sym = VerilogToken.Symbols.SYM_XNOR;
                        getChar(in);
                    }
                    getChar(in);
                } else if (ch == '|') {
                    if (nextCh == '|') {
                        token.sym = VerilogToken.Symbols.SYM_OR_OR;
                        getChar(in);
                    } else if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_OR_EQ;
                        getChar(in);
                    } else {
                        token.sym = VerilogToken.Symbols.SYM_OR;
                    }
                    getChar(in);
                } else if (ch == '&') {
                    if (nextCh == '&') {
                        token.sym = VerilogToken.Symbols.SYM_AND_AND;
                        getChar(in);
                    } else if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_AND_EQ;
                        getChar(in);
                    } else {
                        token.sym = VerilogToken.Symbols.SYM_AND;
                    }
                    getChar(in);
                } else if (ch == '@') {
                    token.sym = VerilogToken.Symbols.SYM_AT;
                    getChar(in);
                } else if (ch == '?') {
                    token.sym = VerilogToken.Symbols.SYM_QUESTION;
                    getChar(in);
                } else if (ch == '>') {
                    if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_GT_EQ;
                        getChar(in);
                    } else if (nextCh == '>') {
                        token.sym = VerilogToken.Symbols.SYM_SHIFT_RIGHT;
                        getChar(in);
                        if (nextCh == '>') {
                            token.sym = VerilogToken.Symbols.SYM_ASHIFT_RIGHT;
                            getChar(in);
                            if (nextCh == '=') {
                                token.sym = VerilogToken.Symbols.SYM_ASHIFT_RIGHT_EQ;
                                getChar(in);
                            }
                        } else if (nextCh == '=') {
                            token.sym = VerilogToken.Symbols.SYM_SHIFT_RIGHT_EQ;
                        }
                    } else {
                        token.sym = VerilogToken.Symbols.SYM_GT;
                    }
                    getChar(in);
                } else if (ch == '<') {
                    if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_LT_EQ;
                        getChar(in);
                    } else if (nextCh == '<') {
                        token.sym = VerilogToken.Symbols.SYM_SHIFT_LEFT;
                        getChar(in);
                        if (nextCh == '<') {
                            token.sym = VerilogToken.Symbols.SYM_ASHIFT_LEFT;
                            getChar(in);
                            if (nextCh == '=') {
                                token.sym = VerilogToken.Symbols.SYM_ASHIFT_LEFT_EQ;
                                getChar(in);
                            }
                        } else if (nextCh == '=') {
                            token.sym = VerilogToken.Symbols.SYM_SHIFT_LEFT_EQ;
                        }
                    } else {
                        token.sym = VerilogToken.Symbols.SYM_LT;
                    }
                    getChar(in);
                } else if (ch == '+') {
                    if (nextCh == '+') {
                        token.sym = VerilogToken.Symbols.SYM_PLUS_PLUS;
                        getChar(in);
                    } else if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_PLUS_EQ;
                        getChar(in);
                    } else {
                        token.sym = VerilogToken.Symbols.SYM_PLUS;
                    }
                    getChar(in);
                } else if (ch == '-') {
                    if (nextCh == '-') {
                        token.sym = VerilogToken.Symbols.SYM_MINUS_MINUS;
                        getChar(in);
                    } else if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_MINUS_EQ;
                        getChar(in);
                    } else {
                        token.sym = VerilogToken.Symbols.SYM_MINUS;
                    }
                    getChar(in);
                } else if (ch == ',') {
                    token.sym = VerilogToken.Symbols.SYM_COMMA;
                    getChar(in);
                } else if (ch == '!') {
                    token.sym = VerilogToken.Symbols.SYM_NOT;
                    if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_NOT_EQ;
                        getChar(in);
                        if (nextCh == '=') {
                            token.sym = VerilogToken.Symbols.SYM_NOT_EQ_EQ;
                            getChar(in);
                        }
                    }
                    getChar(in);
                } else if (ch == '^') {
                    token.sym = VerilogToken.Symbols.SYM_XOR;
                    if (nextCh == '~') {
                        token.sym = VerilogToken.Symbols.SYM_XNOR;
                        getChar(in);
                    } else if (ch == '=') {
                        token.sym = VerilogToken.Symbols.SYM_XOR_EQ;
                        getChar(in);
                    }
                    getChar(in);
                } else if (ch == '*') {
                    token.sym = VerilogToken.Symbols.SYM_MUL;
                    if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_MUL_EQ;
                        getChar(in);
                    }
                    getChar(in);
                } else if (ch == '%') {
                    token.sym = VerilogToken.Symbols.SYM_MODULO;
                    if (nextCh == '=') {
                        token.sym = VerilogToken.Symbols.SYM_MODULO_EQ;
                        getChar(in);
                    }
                    getChar(in);
                } else if (ch == '#') {
                    token.sym = VerilogToken.Symbols.SYM_HASHMARK;
                    getChar(in);
                } else if (ch == '"') {
                    token.sym = VerilogToken.Symbols.SYM_DOUBLE_QUOTE;
                    getChar(in);
                } else if (ch == '\'') {
                    token.sym = VerilogToken.Symbols.SYM_QUOTE;
                    getChar(in);
                } else if (ch == '`') {
                    token.sym = VerilogToken.Symbols.SYM_GRAVE_ACCENT;
                    getChar(in);
                }
                // else if symbol
                //
                else {
//                    Log.i("scan", ">>>>>>>>>>>>>>>>>> " + ch);
                    getChar(in);
                }
                while (ch == '/' || ch == SPACE || ch == TAB || ch == NEW_LINE) {
                    skipWhiteSpaces(in);
                    skipComment(in);
                }
            }
            in.close();
        } catch (
                FileNotFoundException e
                ) {
            e.printStackTrace();
        } catch (
                IOException e
                ) {
            e.printStackTrace();
        }
        return firstToken.next;
    }

    private void getNumber(FileInputStream in, VerilogToken token) throws IOException, NumberFormatException {
        getUnsignedNumber(in, token);
        if (ch == '\'') {
            token.size = Integer.parseInt(token.number);
            getBase(in, token);
        } else if (ch == '.') {
            getDecimal(in, token);
            if (ch == 'e' || ch == 'e') {
                getChar(in);
                if (ch == '-') {
                    getChar(in);
                }
                getUnsignedNumber(in, token);
                token.sym = VerilogToken.Symbols.LIT_REAL_NUMBER;
            } else {
                token.sym = VerilogToken.Symbols.LIT_FIXED_POINT_NUMBER;
            }
        } else {
            // default decimal 32 bits unsigned
            token.sym = VerilogToken.Symbols.LIT_UNSIGNED_NUMBER;
            token.base = VerilogToken.Base.DECIMAL;
        }
        int radix = 10;
        switch (token.base) {
            case DECIMAL:
                radix = 10;
                break;
            case BINARY:
                radix = 2;
                break;
            case OCTAL:
                radix = 8;
                break;
            case HEXADECIMAL:
                radix = 16;
                break;
            default:
        }
        token.intValue = Integer.parseInt(token.number, radix);
    }

    private void getBase(FileInputStream in, VerilogToken token) throws IOException {
        getChar(in);
        ch = Character.toLowerCase(ch);
        if (ch == 's') {
            getChar(in);
            token.sym = VerilogToken.Symbols.LIT_NUMBER;
        } else {
            token.sym = VerilogToken.Symbols.LIT_UNSIGNED_NUMBER;
        }
        ch = Character.toLowerCase(ch);
        if (ch == 'd') {
            token.base = VerilogToken.Base.DECIMAL;
            getDecimal(in, token);
        } else if (ch == 'b') {
            token.base = VerilogToken.Base.BINARY;
            getBinary(in, token);
        } else if (ch == 'o') {
            token.base = VerilogToken.Base.OCTAL;
            getOctal(in, token);
        } else if (ch == 'h') {
            token.base = VerilogToken.Base.HEXADECIMAL;
            getHexa(in, token);
        }
    }

    private void getBinary(FileInputStream in, VerilogToken token) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            if (ch != '_') {
                stringBuilder.append(ch);
            }
            getChar(in);
            ch = Character.toLowerCase(ch);
        } while ((ch >= '0' && ch <= '1') || ch == '_' || ch == 'z' || ch == 'x');
        token.number = stringBuilder.toString();
    }

    private void getOctal(FileInputStream in, VerilogToken token) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            if (ch != '_') {
                stringBuilder.append(ch);
            }
            getChar(in);
            ch = Character.toLowerCase(ch);
        } while ((ch >= '0' && ch <= '7') || ch == '_' || ch == 'z' || ch == 'x');
        token.number = stringBuilder.toString();
    }

    private void getDecimal(FileInputStream in, VerilogToken token) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            if (ch != '_') {
                stringBuilder.append(ch);
            }
            getChar(in);
            ch = Character.toLowerCase(ch);
        } while ((ch >= '0' && ch <= '9') || ch == '_' || ch == 'z' || ch == 'x');
        token.number = stringBuilder.toString();
    }

    private void getUnsignedNumber(FileInputStream in, VerilogToken token) throws IOException {
        int value = 0;
        StringBuilder stringBuilder = new StringBuilder();
        do {
            if (ch != '_') {
                value = 10 * value + (int) ch - (int) '0';
                stringBuilder.append(ch);
            }
            getChar(in);
            ch = Character.toLowerCase(ch);
        } while ((ch >= '0' && ch <= '9') || ch == '_');
        token.number = stringBuilder.toString();
    }

    private void getHexa(FileInputStream in, VerilogToken token) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        do {
            if (ch != '_') {
                stringBuilder.append(ch);
            }
            getChar(in);
            ch = Character.toLowerCase(ch);
        }
        while ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || ch == '_' || ch == 'z' ||
                ch == 'x');
        token.number = stringBuilder.toString();
    }

    public void getEscapedIdentifier(FileInputStream in, VerilogToken token) throws IOException {
        stringBuilder.setLength(0);
        do {
            getChar(in);
            stringBuilder.append(ch);
        } while (ch != SPACE && ch != TAB && ch != NEW_LINE);
        token.sym = VerilogToken.Symbols.ID_ESCAPED;
        token.ident = stringBuilder.toString();
    }

    public void getSystemIdentifier(FileInputStream in, VerilogToken token) throws IOException {
        stringBuilder.setLength(0);
        do {
            stringBuilder.append(ch);
            getChar(in);
        } while (ch >= 'a' && ch <= 'z' ||
                ch >= 'A' && ch <= 'Z' ||
                ch >= '0' && ch <= '9' ||
                ch == '$' || ch == '_');
        token.sym = VerilogToken.Symbols.ID_SYSTEM;
        token.ident = stringBuilder.toString();
    }

    public void getIdentifier(FileInputStream in, VerilogToken token) throws IOException {
        stringBuilder.setLength(0);
        do {
            stringBuilder.append(ch);
            getChar(in);
        } while (ch >= 'a' && ch <= 'z' ||
                ch >= 'A' && ch <= 'Z' ||
                ch >= '0' && ch <= '9' ||
                ch == '$' || ch == '_');
        token.ident = stringBuilder.toString();
        if (token.ident.contains("$")) {
            token.sym = VerilogToken.Symbols.ID_SIMPLE;
        } else {
            token.sym = VerilogToken.Symbols.ID_C;
        }

    }

    public void getKeywordCandidateIdent(FileInputStream in, VerilogToken token) throws IOException {
        stringBuilder.setLength(0);
        do {
            stringBuilder.append(ch);
            getChar(in);
        } while (ch >= 'a' && ch <= 'z' ||
                ch >= 'A' && ch <= 'Z' ||
                ch >= '0' && ch <= '9' ||
                ch == '$' || ch == '_');
        String idString = stringBuilder.toString();
        token.sym = keyWordMap.get(idString);
        if (token.sym == null) {
            token.sym = VerilogToken.Symbols.ID_SIMPLE;
            token.ident = stringBuilder.toString();
        } else {
//            Log.i("kwCandidate","keyword=" + idString);
        }
    }

    private void skipWhiteSpaces(FileInputStream in) throws IOException {
        while (!eof && (ch == NEW_LINE || ch == SPACE || ch == TAB)) {
            getChar(in);
        }
    }

    private void skipComment(FileInputStream in) throws IOException {
        if (ch == '/') {
            if (nextCh == '/') {
                getChar(in);
                while (!eof && ch != '\n') {
                    getChar(in);
                }
                getChar(in); // ch is the first character after the comment or eof is true
            } else if (nextCh == '*') {
                getChar(in);
                getChar(in);
                while (!eof && !(ch == '*' && nextCh == '/')) {
                    getChar(in);
                }
                getChar(in); // ch=='/'
                getChar(in); // ch is the first character after the comment or eof is true
            }
        }
    }

    void getChar(FileInputStream in) throws IOException {
        ch = nextCh;
        if (in.available() > 0) {
            nextCh = (char) in.read();
        } else {
            eof = true;
            ch = '\0';
        }
    }
}
