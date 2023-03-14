package jacks.ast;

public class PrintVisitor implements Visitor {
    private int depth = 0;

    @Override
    public void visit(Tree.Node node) {
        int previousDepth = depth;
        System.out.println(" ".repeat(depth) + node);

        if (node.nodeType == NodeType.BINARY) {
            depth += 4;
            Tree.BinaryNode nodeBinary = (Tree.BinaryNode) node;
            visit(nodeBinary.getLeft());
            System.out.println(" ".repeat(depth) + nodeBinary.getSymbol());
            visit(nodeBinary.getRight());
            depth = previousDepth;
        }

        for (Tree.Node child : node.children) {
            depth += 4;
            visit(child);
            depth = previousDepth;
        }
    }
}
