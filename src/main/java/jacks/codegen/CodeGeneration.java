package jacks.codegen;

import jacks.SymbolTableManager;
import jacks.ast.NodeType;
import jacks.ast.Tree;
import jacks.ast.Visitor;
import jacks.parser.Semantics;
import org.apache.commons.io.FileUtils;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.*;

public class CodeGeneration implements Visitor {
    String className;

    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
    FieldVisitor fv;
    MethodVisitor mv;

    public CodeGeneration(String className) {
        this.className = className;

        cw.visit(50,
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                "java/lang/Object",
                null);

        /*
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL,
                    "java/lang/Object",
                    "<init>",
                    "()V",
                    false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
         */
    }

    @Override
    public void visit(Tree.Node node) {
        NodeType nodeType = node.getNodeType();

        switch (nodeType) {
            case PROGRAM -> {
                Tree.ProgramNode program = (Tree.ProgramNode) node;
                Label l0 = new Label();
                mv = cw.visitMethod(program.getModifiers(),
                        program.getName(),
                        program.getDescriptor(),
                        null,
                        null);
                Label l1 = new Label();
                for (Tree.Node child : program.getChildren()) {
                    child.accept(this);
                }
                mv.visitLocalVariable("args",
                        "[Ljava/lang/String;",
                        null,
                        l0, l1, 0);
            }
            case VARIABLE -> {
                Tree.VariableNode variable = (Tree.VariableNode) node;
                String name = variable.getSymbol().value;

                Type type = (Type) SymbolTableManager.get_attr(name, "type");
                int index = (int) SymbolTableManager.get_attr(name, "index");

                Label l0 = new Label();
                mv.visitLabel(l0);

                for (Tree.Node child : variable.getChildren()) {
                    child.accept(this);
                }

                mv.visitVarInsn(type.getOpcode(ISTORE), index);

                Label l1 = new Label();
                mv.visitLabel(l1);

                mv.visitLocalVariable(name,
                        type.getDescriptor(),
                        null,
                        l0, l1,
                        index);
            }
            case FUNCTION_CALL -> {
                handleFunctionCall(node);
            }
            case LITERAL -> {
                switch (node.getSymbol().type) {
                    case NUMBER -> mv.visitIntInsn(BIPUSH, Integer.parseInt(node.getSymbol().value)); // TODO: Optimization 01 (use CONST instead of BIPUSH)
                    case RAW_STRING -> mv.visitLdcInsn(node.getSymbol().value);
                }
            }
            case BINARY -> {
                Tree.BinaryNode binary = (Tree.BinaryNode) node;
                visit(binary.getLeft());
                visit(binary.getRight());

                switch (binary.getSymbol().type) {
                    case PLUS -> mv.visitInsn(IADD);
                    case MINUS -> mv.visitInsn(ISUB);
                    case TIMES -> mv.visitInsn(IMUL);
                    case DIVIDE -> mv.visitInsn(IDIV);
                }
            }
            case IDENTIFIER -> {
                String name = node.getSymbol().value;
                Type type = (Type) SymbolTableManager.get_attr(name, "type");
                int index = (int) SymbolTableManager.get_attr(name, "index");
                mv.visitVarInsn(type.getOpcode(ILOAD), index);
            }
        }
    }

    public void end(String pathName) {
        Label l0 = new Label();
        mv.visitInsn(RETURN);
        Label l1 = new Label();

        mv.visitLabel(l1);
        mv.visitMaxs(0, 0);

        mv.visitEnd();

        cw.visitEnd();

        try {
            FileUtils.writeByteArrayToFile(new File(pathName), cw.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleFunctionCall(Tree.Node node) {
        String name = node.getSymbol().value;
        StringBuilder descriptor = new StringBuilder();

        int previous_table = SymbolTableManager.CurrentScope;
        SymbolTableManager.setSymbolTable(-1);
        // TODO: Lookup class.
        SymbolTableManager.setSymbolTable(previous_table);
        // BUILT-IN FUNCTIONS.
        if (name.equals("puts")) {
            mv.visitFieldInsn(GETSTATIC,
                    "java/lang/System",
                    "out",
                    "Ljava/io/PrintStream;");

            /*
            ClassPrinter cp = new ClassPrinter();
            ClassReader cr = null;
            try {
                cr = new ClassReader("java.lang.System");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            cr.accept(cp, ClassReader.SKIP_CODE);
             */

            for (Tree.Node child : node.getChildren()) {
                visit(child);
                descriptor.append(Objects.requireNonNull(Semantics.fetchType(child.getSymbol())).getDescriptor());
            }
            mv.visitMethodInsn(INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    "println",
                    "(" + descriptor.toString() + ")V");
        } else if (name.equals("concat")) {
            Builtin.appendString(mv, node.getChildren());
        }
    }
}
