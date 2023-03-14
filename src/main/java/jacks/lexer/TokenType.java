package jacks.lexer;

public enum TokenType {
    NAMESPACE(-2), EXTENDS(-2),

    EOF(-1), NULL(-1),

    BLANK(0), DOLLAR(0), // IGNORE.

    LPAREN(1), RPAREN(2),
    LBRACE(3), RBRACE(4),
    LBRACKET(5), RBRACKET(6),


    COMMA(7), SEMICOLON(8),
    DOT(9), PLUS(10), MINUS(11), TIMES(12),
    DIVIDE(13), MODULO(14), POWER(15),

    AND(16), OR(17), EQ(18), LESSER(19), GREATER(20),

    EXCLAIM(21),

    IDENTIFIER(22), NUMBER(23),

    COLON(24),

    RAW_STRING(25); /* 25 is a QUOTE, but then it is parsed and represented as a string. */

    final int priority;

    TokenType(int i) {
        this.priority = i;
    }

    public int getPriority() {
        return this.priority;
    }
}
