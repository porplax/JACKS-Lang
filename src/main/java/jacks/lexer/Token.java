package jacks.lexer;

public class Token {
    public String value;
    public TokenType type;
    public int line;

    public Token(String value, TokenType type, int line) {
        this.value = value;
        this.type = type;
        this.line = line;
    }

    public String toString() {
        return "Token('" + value + "', " + type + ")";
    }
}
