package jacks.codegen;

import jacks.ast.Tree;
import jacks.parser.Semantics;
import org.objectweb.asm.MethodVisitor;

import java.util.LinkedList;

import static org.objectweb.asm.Opcodes.*;

public class Builtin {
    public static CodeGeneration generator;

    // https://gist.github.com/onejmi/0a202854a5ef1fe7542adf2af088fae1 (Credit to onejmi and thanks!)
    public static void appendString(MethodVisitor mv, LinkedList<Tree.Node> nodes) {
        mv.visitTypeInsn(NEW,"java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL,"java/lang/StringBuilder","<init>","()V",false);

        StringBuilder descriptor = new StringBuilder();
        for (Tree.Node node : nodes) {
            node.accept(generator);
            mv.visitMethodInsn(INVOKEVIRTUAL,"java/lang/StringBuilder","append",
                    "(" + Semantics.fetchType(node.getSymbol()).getDescriptor() + ")L" +"java/lang/StringBuilder;",false);
            descriptor.append(Semantics.fetchType(node.getSymbol()).getDescriptor());
        }
        mv.visitMethodInsn(INVOKEVIRTUAL,"java/lang/StringBuilder",
                "toString", "()Ljava/lang/String;",false);
    }
}
