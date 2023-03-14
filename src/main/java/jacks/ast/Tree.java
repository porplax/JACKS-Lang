package jacks.ast;

import jacks.lexer.Token;

import java.util.LinkedList;

public class Tree {
    public static class Node implements VisitElement {
        public Token symbol;
        public NodeType nodeType;
        public LinkedList<Node> children = new LinkedList<>();

        public Node(Token symbol, NodeType nodeType) {
            this.symbol = symbol;
            this.nodeType = nodeType;
        }

        public Token getSymbol() {
            return symbol;
        }

        public void setSymbol(Token symbol) {
            this.symbol = symbol;
        }

        public NodeType getNodeType() {
            return nodeType;
        }

        public void setNodeType(NodeType nodeType) {
            this.nodeType = nodeType;
        }

        public LinkedList<Node> getChildren() {
            return children;
        }

        public void setChildren(LinkedList<Node> children) {
            this.children = children;
        }

        public String toString() {
            String s;
            if (symbol != null) {
                s = symbol.toString();
            } else {
                s = "null";
            }
            return "Node(<symbol>='" + s + "', <type>=" + nodeType + ", <children>=" + children + ")";
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visit(this);
        }
    }

    public static class ProgramNode extends Node {
        String name;
        int modifiers;
        String descriptor;

        public ProgramNode(String name, int modifiers, String descriptor) {
            super(null, NodeType.PROGRAM);
            this.name = name;
            this.modifiers = modifiers;
            this.descriptor = descriptor;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getModifiers() {
            return modifiers;
        }

        public void setModifiers(int modifiers) {
            this.modifiers = modifiers;
        }

        public String getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }
    }

    public static class VariableNode extends Node {
        String name;
        Token varType;

        public VariableNode(Token symbol, Token varType) {
            super(symbol, NodeType.VARIABLE);
            this.varType = varType;
            name = symbol.value;
        }

        public Token getVarType() {
            return varType;
        }

        public void setVarType(Token varType) {
            this.varType = varType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String toString() {
            return "Variable(<name>='" + symbol + "', <type>=" + varType + ", <children>=" + children + ")";
        }
    }

    public static class BinaryNode extends Node {
        public Node left;
        public Node right;

        public BinaryNode(Node left, Token symbol, Node right) {
            super(symbol, NodeType.BINARY);
            this.left = left;
            this.right = right;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        @Override
        public String toString() {
            return "Binary(<left>=" + left + ", <symbol>=" + symbol + ", <right>=" + right + ")";
        }
    }

    public static class FunctionCallNode extends Node {
        public FunctionCallNode(Token symbol) {
            super(symbol, NodeType.FUNCTION_CALL);
        }
    }
}
