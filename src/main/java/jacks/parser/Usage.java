package jacks.parser;

import jacks.Color;
import jacks.Main;
import jacks.SymbolTableManager;
import jacks.ast.NodeType;
import jacks.ast.Tree;
import jacks.ast.Visitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Usage implements Visitor {
    @Override
    public void visit(Tree.Node node) {
        if (node.getNodeType() == NodeType.VARIABLE) {
            HashMap<String, Object> symbol = SymbolTableManager.lookup(node.getSymbol().value);
            ArrayList<Tree.Node> usage = (ArrayList<Tree.Node>) symbol.get("uses");
            if (usage.size() == 0) {
                info(node, "Unused variable: " + node.getSymbol().value);
            }
        }

        for (Tree.Node child : node.getChildren()) {
            child.accept(this);
        }
    }

    private void info(Tree.Node node, String msg) {
        String line;
        try {
            line = Files.readAllLines(Paths.get(Main.filename)).get(node.getSymbol().line - 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Color.YELLOW.getColor() + msg + Color.RESET.getColor());
        System.out.println(" ".repeat(4) + Color.GRAY.getColor() + node.getSymbol().line + " | " + Color.RESET.getColor() + Color.UNDERLINE.getColor() + Color.BOLD.getColor() + line + Color.RESET.getColor());

    }
}
