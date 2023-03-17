package jacks.parser;

import jacks.Color;
import jacks.Main;
import jacks.SymbolTableManager;
import jacks.ast.PrintVisitor;
import jacks.ast.Tree;
import jacks.ast.Visitor;
import jacks.lexer.Token;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Semantics implements Visitor {
    int marker = 1;

    @Override
    public void visit(Tree.Node node) {
        switch (node.getNodeType()) {
            case FUNCTION -> {
                int previous_scope = SymbolTableManager.CurrentScope;
                SymbolTableManager.allocate(previous_scope + 1);
                Tree.FunctionNode functionNode = (Tree.FunctionNode) node;
                Tree.FunctionPrototypeNode prototypeNode = functionNode.getPrototypeNode();

                for (Tree.Node parameter : prototypeNode.getChildren()) {
                    parameter.accept(this);
                }

                for (Tree.Node child : node.getChildren()) {
                    child.accept(this);
                }
                SymbolTableManager.setSymbolTable(previous_scope);
            }
            case VARIABLE -> {
                Tree.VariableNode variableNode = null;
                if (!(node instanceof Tree.VariableNode)) {
                    if (node.getChildren().size() == 0) {
                        return;
                    }
                    for (Tree.Node child : node.getChildren()) {
                        if (child instanceof Tree.VariableNode) {
                            variableNode = (Tree.VariableNode) child;
                            break;
                        }
                    }
                } else {
                    variableNode = (Tree.VariableNode) node;
                }
                if (variableNode == null) return;
                String name = variableNode.getSymbol().value;

                if (SymbolTableManager.lookup(name) != null) {
                    System.err.println("Duplicate symbol: " + name);
                    System.exit(1);
                }

                // Handle primitive types.
                SymbolTableManager.insert(name);

                switch (variableNode.getVarType().value) {
                    case "int" -> SymbolTableManager.set_attr(name, "type", Type.getType("I"));
                    case "str" -> SymbolTableManager.set_attr(name, "type", Type.getType("Ljava/lang/String;"));
                    case "char" -> SymbolTableManager.set_attr(name, "type", Type.getType("C"));
                    case "bool" -> SymbolTableManager.set_attr(name, "type", Type.getType("Z"));
                    case "double" -> SymbolTableManager.set_attr(name, "type", Type.getType("D"));
                    default -> {
                        int previous_scope = SymbolTableManager.CurrentScope;

                        String typeName = variableNode.getVarType().value;

                        SymbolTableManager.setSymbolTable(-1);
                        if (SymbolTableManager.lookup(typeName) == null) {
                            error(node, "Unknown type: " + typeName, "");
                        } else {
                            typeName = (String) SymbolTableManager.get_attr(typeName, "qualifier");
                        }
                        SymbolTableManager.setSymbolTable(previous_scope);

                        SymbolTableManager.set_attr(name, "type", Type.getObjectType(typeName));
                        SymbolTableManager.set_attr(name, "qualifier", Type.getObjectType(typeName));
                    }
                }

                SymbolTableManager.set_attr(name, "index", marker++);
                SymbolTableManager.set_attr(name, "uses", new ArrayList<Tree.Node>());
                for (Tree.Node child : node.getChildren()) {
                    if (child instanceof Tree.VariableNode) {
                        if (child.getSymbol().value.equals(name)) continue;
                    }
                    child.accept(this);
                }
            }
            case IDENTIFIER -> {
                int previous_scope = SymbolTableManager.CurrentScope;
                SymbolTableManager.setSymbolTable(-1);
                Object PkgSymbol = SymbolTableManager.lookup(node.getSymbol().value);
                SymbolTableManager.setSymbolTable(previous_scope);
                Object Symbol = SymbolTableManager.lookup(node.getSymbol().value);
                if (PkgSymbol == null && Symbol == null) {
                    error(node, "Undefined symbol: '" + node.getSymbol().value + "', was it declared?", node.getSymbol().value + ": (type) = (value)");
                } else {
                    ArrayList<Tree.Node> uses = (ArrayList<Tree.Node>) SymbolTableManager.get_attr(node.getSymbol().value, "uses");
                    uses.add(node);
                }
            }
            case IMPORT_STMT -> {
                Tree.Node child = node.getChildren().get(0);
                String name = child.getSymbol().value.substring(child.getSymbol().value.lastIndexOf('/') + 1, child.getSymbol().value.length());
                int previous_scope = SymbolTableManager.CurrentScope;
                SymbolTableManager.setSymbolTable(-1);
                SymbolTableManager.insert(name);
                SymbolTableManager.set_attr(name, "qualifier", child.getSymbol().value);
                SymbolTableManager.setSymbolTable(previous_scope);


            }
        }
        for (Tree.Node child : node.getChildren()) {
            child.accept(this);
        }
    }

    public static Type fetchType(Token token) {
        switch (token.type) {
            case NUMBER, PLUS, MINUS, MODULO, TIMES, DIVIDE, POWER -> {
                return Type.getType("I");
            }
            case RAW_STRING -> {
                return Type.getType("Ljava/lang/String;");
            }
            case IDENTIFIER -> {
                return (Type) SymbolTableManager.get_attr(token.value, "type");
            }
        }
        return null;
    }

    private void error(Tree.Node node, String info, String solution) {
        String line;
        try {
            line = Files.readAllLines(Paths.get(Main.filename)).get(node.getSymbol().line - 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(Color.RED.getColor() + info + Color.RESET.getColor());
        System.out.println(" ".repeat(4) + Color.GRAY.getColor() + node.getSymbol().line + " | " + Color.RESET.getColor() + Color.UNDERLINE.getColor() + Color.BOLD.getColor() + line + Color.RESET.getColor());
        System.out.println("=".repeat(40 + line.length()));
        if (solution != null || !solution.isBlank()) {
            System.out.println(Color.GRAY.getColor() + Color.ITALIC.getColor() + "Possible Solution: " + Color.RESET.getColor() + Color.GREEN.getColor() + Color.BOLD.getColor() + solution + Color.RESET.getColor());
        }
        System.exit(1);
    }
}
