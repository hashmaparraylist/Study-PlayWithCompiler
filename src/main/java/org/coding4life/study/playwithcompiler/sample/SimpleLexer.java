package org.coding4life.study.playwithcompiler.sample;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleLexer {
    public static void main(String[] args) {
        SimpleLexer lexer = new SimpleLexer();
        String script = "int age = 45;";
        System.out.println("parse :" + script);
        SimpleTokenReader tokenReader = lexer.tokenize(script);
        dump(tokenReader);

        //测试inta的解析
        script = "inta age = 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);

        //测试in的解析
        script = "in age = 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);

        //测试>=的解析
        script = "age >= 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);

        //测试>的解析
        script = "age > 45;";
        System.out.println("\nparse :" + script);
        tokenReader = lexer.tokenize(script);
        dump(tokenReader);
    }

    //下面几个变量是在解析过程中用到的临时变量,如果要优化的话，可以塞到方法里隐藏起来
    private StringBuffer tokenText = null;   //临时保存token的文本
    private List<Token> tokens = null;       //保存解析出来的Token
    private SimpleToken token = null;        //当前正在解析的Token

    //是否是字母
    private boolean isAlpha(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z';
    }

    //是否是数字
    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    //是否是空白字符
    private boolean isBlank(int ch) {
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    /**
     * 有限状态机进入初始状态。
     * 这个初始状态其实并不做停留，它马上进入其他状态。
     * 开始解析的时候，进入初始状态；某个Token解析完毕，也进入初始状态，在这里把Token记下来，然后建立一个新的Token。
     * @param ch
     * @return
     */
    private DfaState initToken(char ch) {
        if (tokenText.length() > 0) {
            token.setText(tokenText.toString());
            tokens.add(token);

            tokenText = new StringBuffer();
            token = new SimpleToken();
        }

        DfaState newState = DfaState.Initial;

        if (isAlpha(ch)) {
            if (ch == 'i') {
                newState = DfaState.Id_int1;
            } else  {
                newState = DfaState.Id;
            }

            token.setType(TokenType.Identifier);
            tokenText.append(ch);
        } else if (isDigit(ch)) {
            newState = DfaState.IntLiteral;
            token.setType(TokenType.IntLiteral);
            tokenText.append(ch);
        } else if (ch == '>') {
            newState = DfaState.GT;
            token.setType(TokenType.GT);
            tokenText.append(ch);
        } else if (ch == '+') {
            newState = DfaState.Plus;
            token.setType(TokenType.Plus);
            tokenText.append(ch);
        } else if (ch == '-') {
            newState = DfaState.Minus;
            token.setType(TokenType.Minus);
            tokenText.append(ch);
        } else if (ch == '*') {
            newState = DfaState.Star;
            token.setType(TokenType.Star);
            tokenText.append(ch);
        } else if (ch == '/') {
            newState = DfaState.Slash;
            token.setType(TokenType.Slash);
            tokenText.append(ch);
        } else if (ch == ';') {
            newState = DfaState.SemiColon;
            token.setType(TokenType.SemiColon);
            tokenText.append(ch);
        } else if (ch == '(') {
            newState = DfaState.LeftParen;
            token.setType(TokenType.LeftParen);
            tokenText.append(ch);
        } else if (ch == ')') {
            newState = DfaState.RightParen;
            token.setType(TokenType.RightParen);
            tokenText.append(ch);
        } else if (ch == '=') {
            newState = DfaState.Assignment;
            token.setType(TokenType.Assignment);
            tokenText.append(ch);
        } else {
            newState = DfaState.Initial;
        }


        return newState;
    }

    /**
     * 解析字符串，形成Token。
     * 这是一个有限状态自动机，在不同的状态中迁移。
     * @param code
     * @return
     */
    public SimpleTokenReader tokenize(String code) {
        tokens = new ArrayList<Token>(code.length() * 2);
        CharArrayReader reader = new CharArrayReader(code.toCharArray());

        tokenText = new StringBuffer();
        token = new SimpleToken();

        int ich = 0;
        char ch = 0;

        DfaState state = DfaState.Initial;

        try {
            while ((ich = reader.read()) != -1) {
                ch = (char) ich;


                switch (state) {
                    case Initial:
                        state = initToken(ch);
                        break;
                    case Id:
                        if (isAlpha(ch) || isDigit(ch)) {
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case  GT:
                        if (ch == '=') {
                            token.setType(TokenType.GE);
                            state = DfaState.GE;
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }

                    break;
                    case GE:
                    case Assignment:
                    case Plus:
                    case Minus:
                    case Star:
                    case Slash:
                    case SemiColon:
                    case LeftParen:
                    case RightParen:
                        state = initToken(ch);
                        break;
                    case IntLiteral:
                        if (isDigit(ch)) {
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case Id_int1:
                        if (ch == 'n') {
                            state = DfaState.Id_int2;
                            tokenText.append(ch);
                        } else if (isDigit(ch) || isAlpha(ch)) {
                            state = DfaState.Id;
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case Id_int2:
                        if (ch == 't') {
                            state = DfaState.Id_int3;
                            tokenText.append(ch);
                        } else if (isDigit(ch) || isAlpha(ch)) {
                            state = DfaState.Id;
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case Id_int3:
                        if (isBlank(ch)) {
                            token.setType(TokenType.Int);
                            state = initToken(ch);
                        } else {
                            state = DfaState.Id;
                            tokenText.append(ch);
                        }
                        break;
                    default:
                }
            }

            // 把最后一个token送进去
            if (tokenText.length() > 0) {
                initToken(ch);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new SimpleTokenReader(tokens);
    }

    /**
     * 打印所有的Token
     * @param tokenReader
     */
    public static void dump(SimpleTokenReader tokenReader) {
        System.out.println("text\ttype");
        Token token = null;
        while ((token = tokenReader.read()) != null) {
            System.out.println(token.getText() + "\t\t" + token.getType());
        }
    }


    /**
     * 有限状态机的各种状态。
     */
    private enum DfaState {
        Initial,

        If, Id_if1, Id_if2, Else, Id_else1, Id_else2, Id_else3, Id_else4, Int, Id_int1, Id_int2, Id_int3, Id, GT, GE,

        Assignment,

        Plus, Minus, Star, Slash,

        SemiColon,
        LeftParen,
        RightParen,

        IntLiteral
    }


}
