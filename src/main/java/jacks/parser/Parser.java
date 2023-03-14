package jacks.parser;

import jacks.SymbolTableManager;
import jacks.ast.NodeType;
import jacks.ast.Tree;
import jacks.lexer.Lexer;
import jacks.lexer.Token;
import jacks.lexer.TokenType;
import org.objectweb.asm.Type;

import java.util.LinkedList;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class Parser {
    private final Tree.ProgramNode programNode = new Tree.ProgramNode("main", ACC_PUBLIC + ACC_STATIC, "([Ljava/lang/String;)V"); // TODO: come back and change.
    private final Lexer lexer;

    private int index = 0;
    private Token current = null;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private void initialize() {
        index = 0;
        current = lexer.tokens.get(index);
    }

    public void parse() {
        if (lexer.tokens.size() == 0) {return;}
        initialize();

        _ruleProgram(programNode);

        SymbolTableManager.allocate(-1);
        SymbolTableManager.allocate(0);
        SymbolTableManager.insert("args");
        SymbolTableManager.set_attr("args", "index", 0);
        SymbolTableManager.set_attr("args", "type", Type.getType("([Ljava/lang/String;)V"));
        programNode.accept(new Semantics()); // (PASS 1) Check for semantic errors.
        programNode.accept(new Usage()); // (PASS 2) Check usage of symbols.
    }

    private void _ruleProgram(Tree.Node params) {
        Tree.Node statement = _ruleStatement();
        if (current.type == TokenType.EOF || current.type == TokenType.SEMICOLON) {
            while (true) {
                next(1);
                if (!(index + 1 >= lexer.tokens.size()) && (current.type == TokenType.EOF || current.type == TokenType.SEMICOLON)) {
                    continue;
                }
                break;
            }
        }
        params.children.add(statement);
        if (!(index + 1 >= lexer.tokens.size())) {
            _ruleProgram(params);
        }
    }

    private Tree.Node _ruleStatement() {
        Tree.Node primary;
        primary = _ruleSimpleStmt();
        if (primary != null) return primary;
        return null;
    }

    private Tree.Node _ruleSimpleStmt() {
        Tree.Node primary;
        primary = _ruleFunctionCall();
        if (primary!= null) return primary;
        primary = _ruleAssignment();
        if (primary!= null) return primary;

        return null;
    }

    private Tree.Node _ruleFunctionCall() {
        if (expect(TokenType.IDENTIFIER, 0) && expect(TokenType.LPAREN, 1)) {
            Token name = current;
            next(1);
            LinkedList<Tree.Node> args = _ruleFunctionCallArgs();
            Tree.FunctionCallNode call = new Tree.FunctionCallNode(name);
            call.children = args;
            return call;
        }
        return null;
    }

    private LinkedList<Tree.Node> _ruleFunctionCallArgs() {
        if (expect(TokenType.LPAREN, 0)) {
            next(1);
            LinkedList<Tree.Node> args = new LinkedList<>();
            while (true) {
                if (expect(TokenType.COMMA, 0)) {
                    next(1);
                    continue;
                }
                if (expect(TokenType.RPAREN, 0)) {
                    break;
                }
                args.add(_ruleExpression());
            }
            next(1);
            return args;

        }

        return null;
    }

    private Tree.VariableNode _ruleAssignment() {
        if (expect(TokenType.IDENTIFIER, 0) && expect(TokenType.COLON, 1) && expect(TokenType.IDENTIFIER, 2)) {
            // if (!current.value.equals("let")) error("Syntax error");
            Token name = current;
            next(2);
            Token type = current;
            next(2);

            Tree.Node expr = _ruleExpression();

            Tree.VariableNode assign = new Tree.VariableNode(name, type);
            assign.children.add(expr);

            return assign;
        }
        return null;
    }

    private Tree.Node _ruleExpression() {

        Tree.Node expr = _tokenPrimary();
        next(1);

        while (expect(TokenType.PLUS, 0) || expect(TokenType.MINUS, 0) || expect(TokenType.TIMES, 0) || expect(TokenType.DIVIDE, 0)) {
            Token operator = current;
            next(1);
            Tree.Node right = _tokenPrimary();

            expr = new Tree.BinaryNode(expr, operator, right);
            next(1);
        }
        return expr;
    }

    private Tree.Node _tokenPrimary() {
        Tree.Node primary;
        primary = _ruleFunctionCall();
        if (primary != null) return primary;
        primary = _tokenID();
        if (primary != null) return primary;
        primary = _tokenNumber();
        if (primary != null) return primary;
        primary = _tokenRawString();
        if (primary != null) return primary;
        error("Syntax error #01");
        return null;
    }

    private Tree.Node _tokenNumber() {
        if (expect(TokenType.NUMBER, 0)) {
            return new Tree.Node(current, NodeType.LITERAL);
        }
        return null;
    }

    private Tree.Node _tokenRawString() {
        if (expect(TokenType.RAW_STRING, 0)) {
            return new Tree.Node(current, NodeType.LITERAL);
        }
        return null;
    }

    private Tree.Node _tokenID() {
        if (expect(TokenType.IDENTIFIER, 0)) {
            return new Tree.Node(current, NodeType.IDENTIFIER);
        }
        return null;
    }

    private void error(String syntaxError) {
        System.err.println(syntaxError);
        System.exit(1);
    }

    private boolean expect(TokenType type, int increment) {
        if (index + (increment + 1) >= lexer.tokens.size()) return false;
        Token alter = lexer.tokens.get(index + increment);
        return alter.type == type;
    }

    private void next(int increment) {
        if (index + increment >= lexer.tokens.size()) return;
        index += increment;
        current = lexer.tokens.get(index);
    }

    public Tree.Node getProgramNode() {
        return programNode;
    }
}
