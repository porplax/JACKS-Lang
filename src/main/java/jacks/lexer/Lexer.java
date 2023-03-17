package jacks.lexer;

import java.util.LinkedList;

public class Lexer {
    public final LinkedList<Token> tokens = new LinkedList<>();
    private boolean running = false;

    private char cursor = ' ';
    private int index = 0;
    private int line = 1;

    private String program = "";
    private StringBuilder buffer = new StringBuilder();

    /*
    private final String[] keywords = {
            "if",
            "else",
            "while",
            "do",
            "for",
            "return",
            "break",
            "continue",
            "function",
            "in",
            "true",
            "false"
    };

     */

    private void initialize() {
        index = 0;
        cursor = program.charAt(index);
    }

    public void scan(String s) {
        if (s.isBlank()) {
            line += 1;
            return;
        }
        program = s;
        running = true;
        initialize();

        start();
        if (tokens.size() != 0 && tokens.get(tokens.size() - 1).type != TokenType.EOF) {
            tokens.add(new Token("", TokenType.EOF, line));
        }
        line += 1;
    }

    private void consume() {
        buffer.append(cursor);
        next();
    }

    private void start() {
        while (running) {
            TokenType matchType = match(cursor);
            switch (matchType) {
                case BLANK:
                    break;
                case DOLLAR:
                    running = false;
                    return;
                case IDENTIFIER: {
                    while (match(cursor) == TokenType.IDENTIFIER) {
                        consume();
                    }

                    switch (buffer.toString()) {
                        case "namespace" -> matchType = TokenType.NAMESPACE;
                        case "extends" -> matchType = TokenType.EXTENDS;
                        case "def" -> matchType = TokenType.FUNCTION_DEF;
                        case "use" -> matchType = TokenType.USE;
                    }

                    tokens.add(new Token(buffer.toString(), matchType, line));
                    buffer = new StringBuilder();
                    continue;
                }
                case NUMBER: {
                    while (match(cursor) == TokenType.NUMBER) {
                        consume();
                    }
                    tokens.add(new Token(buffer.toString(), matchType, line));
                    buffer = new StringBuilder();
                    continue;
                }
                case RAW_STRING: {
                    next();
                    while (!(match(cursor) == TokenType.RAW_STRING)) {
                        consume();
                    }
                    tokens.add(new Token(buffer.toString(), matchType, line));
                    buffer = new StringBuilder();
                    next();
                    continue;
                }
                default:
                    tokens.add(new Token(String.valueOf(cursor), matchType, line));
                    break;
            }
            next();
        }
    }

    private void next() {
        if (index + 1 >= program.length()) {
            cursor = ' ';
            running = false;
            return;
        }
        index += 1;
        cursor = program.charAt(index);
    }

    private TokenType match(char cursor) {
        switch (cursor) {
            case ' ', '\t', '\n' -> {
                return TokenType.BLANK;
            }
            case '(' -> {
                return TokenType.LPAREN;
            }
            case ')' -> {
                return TokenType.RPAREN;
            }
            case '{' -> {
                return TokenType.LBRACE;
            }
            case '}' -> {
                return TokenType.RBRACE;
            }
            case '[' -> {
                return TokenType.LBRACKET;
            }
            case ']' -> {
                return TokenType.RBRACKET;
            }
            case ',' -> {
                return TokenType.COMMA;
            }
            case ';' -> {
                return TokenType.SEMICOLON;
            }
            case '.' -> {
                return TokenType.DOT;
            }
            case '+' -> {
                return TokenType.PLUS;
            }
            case '-' -> {
                return TokenType.MINUS;
            }
            case '*' -> {
                return TokenType.TIMES;
            }
            case '/' -> {
                return TokenType.DIVIDE;
            }
            case '%' -> {
                return TokenType.MODULO;
            }
            case '^' -> {
                return TokenType.POWER;
            }
            case '&' -> {
                return TokenType.AND;
            }
            case '|' -> {
                return TokenType.OR;
            }
            case '=' -> {
                return TokenType.EQ;
            }
            case '<' -> {
                return TokenType.LESSER;
            }
            case '>' -> {
                return TokenType.GREATER;
            }
            case '!' -> {
                return TokenType.EXCLAIM;
            }
            case ':' -> {
                return TokenType.COLON;
            }
            case '\'' -> {
                return TokenType.RAW_STRING;
            }
            case '$' -> {
                return TokenType.DOLLAR;
            }
            default -> {
                if (Character.isAlphabetic(cursor) || cursor == '_') {
                    return TokenType.IDENTIFIER;
                }
                if (Character.isDigit(cursor)) {
                    return TokenType.NUMBER;
                }
            }
        }
        return TokenType.NULL;
    }
}
