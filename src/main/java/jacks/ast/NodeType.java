package jacks.ast;

public enum NodeType {
    PROGRAM,

    CLASS,
    METHOD,
    FIELD,

    IDENTIFIER,
    LITERAL,

    BINARY,
    FUNCTION, FUNCTION_PROTOTYPE, IMPORT_STMT, QUALIFIER, VARIABLE
}
