package org.coding4life.study.playwithcompiler.sample;

/**
 * 一个简单的语法解析器。
 * 能够解析简单的表达式、变量声明和初始化语句、赋值语句。
 * 它支持的语法规则为：
 *
 * programm -> intDeclare | expressionStatement | assignmentStatement
 * intDeclare -> 'int' Id ( = additive) ';'
 * expressionStatement -> addtive ';'
 * addtive -> multiplicative ( (+ | -) multiplicative)*
 * multiplicative -> primary ( (* | /) primary)*
 * primary -> IntLiteral | Id | (additive)
 */
public class SimpleParser {
    public static void main(String[] args) {
        SimpleParser parser = new SimpleParser();
        String script = null;
        ASTNode tree = null;

        try {
            script = "int age = 45+2; age= 20; age+10*2;";
            System.out.println("解析："+script);
            tree = parser.parse(script);
            parser.dumpAST(tree, "");
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        //测试异常语法
        try {
            script = "2+3+;";
            System.out.println("解析："+script);
            tree = parser.parse(script);
            parser.dumpAST(tree, "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //测试异常语法
        try {
            script = "2+3*;";
            System.out.println("解析："+script);
            tree = parser.parse(script);
            parser.dumpAST(tree, "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 解析脚本
     * @param script
     * @return
     * @throws Exception
     */
    public ASTNode parse(String script) throws Exception {
        SimpleLexer lexer = new SimpleLexer();
        TokenReader tokens = lexer.tokenize(script);
        ASTNode rootNode = prog(tokens);
        return rootNode;
    }


    /**
     * AST的根节点，解析的入口。
     * @return
     * @throws Exception
     */
    private SimpleASTNode prog(TokenReader tokens) throws Exception {

        SimpleASTNode node = new SimpleASTNode(ASTNodeType.Programm, "pwc");

        while (tokens.peek() != null) {
            SimpleASTNode child = intDeclare(tokens);

            if (child == null) {
                child = expressionStatement(tokens);
            }

            if (child == null) {
                child = assignmentStatement(tokens);
            }

            if (child != null) {
                node.addChild(child);
            } else {
                throw new Exception("unknown statement");
            }
        }
        return node;
    }

    /**
     * 表达式语句，即表达式后面跟个分号。
     * @return
     * @throws Exception
     */
    private SimpleASTNode expressionStatement(TokenReader tokens) throws Exception {
        int pos = tokens.getPosition();
        SimpleASTNode node = additive(tokens);
        if (node != null) {
            Token token = tokens.peek();
            if (token != null && token.getType() == TokenType.SemiColon) {
                tokens.read();
            } else {
                node = null;
                tokens.setPosition(pos);
            }
        }

        return node;
    }

    /**
     * 赋值语句，如age = 10*2;
     * @return
     * @throws Exception
     */
    private SimpleASTNode assignmentStatement(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if (token != null && token.getType() == TokenType.Identifier) {
            token = tokens.read();
            node = new SimpleASTNode(ASTNodeType.AssignmentStmt, token.getText());
            token = tokens.peek();
            if (token != null && token.getType() == TokenType.Assignment) {
                tokens.read();
                SimpleASTNode child = additive(tokens);
                if (child == null) {
                    throw new Exception("invalide assignment statement, expecting an expression");
                } else {
                    node.addChild(child);
                    token = tokens.peek();
                    if (token != null && token.getType() == TokenType.SemiColon) {
                        tokens.read();
                    } else {
                        throw new Exception("invalid statement, expecting semicolon");
                    }
                }
            } else {
                tokens.unread();
                node = null;
            }
        }

        return node;
    }

    /**
     * 整型变量声明，如：
     * int a;
     * int b = 2*3;
     *
     * @return
     * @throws Exception
     */
    private SimpleASTNode intDeclare(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();

        if (token != null && token.getType() == TokenType.Int) {
            token = tokens.read();
            if (tokens.peek().getType() == TokenType.Identifier) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.IntDeclaration, token.getText());
                token = tokens.peek();
                if (token != null && token.getType() == TokenType.Assignment) {
                    tokens.read();
                    SimpleASTNode child = additive(tokens);
                    if (child == null) {
                        throw new Exception("invalide variable initialization, expecting an expression");
                    } else {
                        node.addChild(child);
                    }
                }
            } else {
                throw new Exception("variable name expected");
            }

            if (node != null) {
                token = tokens.peek();
                if (token != null && token.getType() == TokenType.SemiColon) {
                    tokens.read();
                } else {
                    throw new Exception("invalid statement, expecting semicolon");
                }
            }
        }
        return node;
    }

    /**
     * 加法表达式
     * @return
     * @throws Exception
     */
    private SimpleASTNode additive(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = multiplicative(tokens);  //应用add规则
        SimpleASTNode node = child1;
        if (child1 != null) {
            while (true) {                              //循环应用add'规则
                Token token = tokens.peek();
                if (token != null && (token.getType() == TokenType.Plus || token.getType() == TokenType.Minus)) {
                    token = tokens.read();              //读出加号
                    SimpleASTNode child2 = multiplicative(tokens);  //计算下级节点
                    if (child2 !=null) {
                        node = new SimpleASTNode(ASTNodeType.Additive, token.getText());
                        node.addChild(child1);              //注意，新节点在顶层，保证正确的结合性
                        node.addChild(child2);
                        child1 = node;
                    }else{
                        throw new Exception("invalid additive expression, expecting the right part.");
                    }
                } else {
                    break;
                }
            }
        }
        return node;
    }

    /**
     * 乘法表达式
     * @return
     * @throws Exception
     */
    private SimpleASTNode multiplicative(TokenReader tokens) throws Exception {
        SimpleASTNode child1 = primary(tokens);
        SimpleASTNode node = child1;

        while (true) {
            Token token = tokens.peek();
            if (token != null && (token.getType() == TokenType.Star || token.getType() == TokenType.Slash)) {
                token = tokens.read();
                SimpleASTNode child2 = primary(tokens);
                if (child2 != null) {
                    node = new SimpleASTNode(ASTNodeType.Multiplicative, token.getText());
                    node.addChild(child1);
                    node.addChild(child2);
                    child1 = node;
                }else{
                    throw new Exception("invalid multiplicative expression, expecting the right part.");
                }
            } else {
                break;
            }
        }

        return node;
    }

    /**
     * 基础表达式
     * @return
     * @throws Exception
     */
    private SimpleASTNode primary(TokenReader tokens) throws Exception {
        SimpleASTNode node = null;
        Token token = tokens.peek();
        if (token != null) {
            if (token.getType() == TokenType.IntLiteral) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.IntLiteral, token.getText());
            } else if (token.getType() == TokenType.Identifier) {
                token = tokens.read();
                node = new SimpleASTNode(ASTNodeType.Identifier, token.getText());
            } else if (token.getType() == TokenType.LeftParen) {
                tokens.read();
                node = additive(tokens);
                if (node != null) {
                    token = tokens.peek();
                    if (token != null && token.getType() == TokenType.RightParen) {
                        tokens.read();
                    } else {
                        throw new Exception("expecting right parenthesis");
                    }
                } else {
                    throw new Exception("expecting an additive expression inside parenthesis");
                }
            }
        }
        return node;  //这个方法也做了AST的简化，就是不用构造一个primary节点，直接返回子节点。因为它只有一个子节点。
    }

    /**
     * 打印输出AST的树状结构
     * @param node
     * @param indent 缩进字符，由tab组成，每一级多一个tab
     */
    void dumpAST(ASTNode node, String indent) {
        System.out.println(indent + node.getType() + " " + node.getText());
        for (ASTNode child : node.getChildren()) {
            dumpAST(child, indent + "\t");
        }
    }
}
