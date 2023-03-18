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
                    if (child.getNodeType() == NodeType.METHOD && variable.getChildren().size() == 1) {
                        int previous_scope = SymbolTableManager.CurrentScope;
                        SymbolTableManager.setSymbolTable(-1);
                        String child_name = child.getSymbol().value;
                        String qualifier = String.valueOf(SymbolTableManager.get_attr(child_name, "qualifier"));
                        SymbolTableManager.setSymbolTable(previous_scope);

                        ClassLookup cp = new ClassLookup();
                        ClassReader cr = null;

                        cp.getMethod("<init>");
                        try {cr = new ClassReader(qualifier);} catch (IOException e) {throw new RuntimeException(e);}
                        cr.accept(cp, 0);


                        mv.visitTypeInsn(NEW, qualifier);
                        mv.visitInsn(DUP);
                        mv.visitMethodInsn(
                                INVOKESPECIAL,
                                qualifier,
                                "<init>",
                                "()V",
                                false);
                        continue;
                    }
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
            case IMPORT_STMT -> {
                /*
                Tree.Node child = node.children.get(0);
                ClassLookup cp = new ClassLookup();
                cp.getMethod("exit");
                ClassReader cr = null;
                try {
                    cr = new ClassReader(child.getSymbol().value);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                cr.accept(cp, 0);

                 */
            }
            case CLASS -> {
                int previous_scope = SymbolTableManager.CurrentScope;
                SymbolTableManager.setSymbolTable(-1);

                ClassLookup cp = new ClassLookup();
                ClassReader cr = null;

                String name = node.getSymbol().value;
                String qualifier = String.valueOf(SymbolTableManager.get_attr(name, "qualifier"));
                SymbolTableManager.setSymbolTable(previous_scope);

                /*
                if (SymbolTableManager.lookup(name) == null) {
                    int scope = previous_scope;
                    while (!(scope - 1 > SymbolTableManager.getSymbolTable().size())) {
                        SymbolTableManager.setSymbolTable(scope);
                        try {
                            if (SymbolTableManager.lookup(name) != null) {
                                qualifier = String.valueOf(SymbolTableManager.get_attr(name, "qualifier"));
                                break;
                            }
                        } catch (NullPointerException ignored) {;}
                        scope += 1;
                    }
                } else {
                }
                System.err.println(name);
                */

                Label l0 = new Label();
                mv.visitLabel(l0);

                for (Tree.Node child : node.children) {
                    if (child.getNodeType() == NodeType.METHOD) {
                        String method_name = child.getSymbol().value;
                        cp.getMethod(method_name);
                        try {cr = new ClassReader(qualifier);} catch (IOException e) {throw new RuntimeException(e);}
                        cr.accept(cp, 0);


                        for (Tree.Node params : child.children) {
                            params.accept(this);
                        }
                        mv.visitMethodInsn((cp.methodAccess == 1) ? INVOKEVIRTUAL : INVOKESTATIC,
                                qualifier,
                                method_name,
                                cp.methodDesc,
                                false);

                    } else if (child.getNodeType() == NodeType.FIELD) {
                        String field_name = child.getSymbol().value;
                        cp.getField(field_name);
                        try {
                            cr = new ClassReader(qualifier);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        cr.accept(cp, 0);
                        Type type = Type.getType(cp.fieldDesc);
                        mv.visitFieldInsn(GETSTATIC,
                                qualifier,
                                field_name,
                                cp.fieldDesc);
                        qualifier = type.getInternalName();

                    }
                }

                SymbolTableManager.setSymbolTable(previous_scope);

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

        mv.visitLabel(l0);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);

        mv.visitEnd();

        cw.visitEnd();

        try {
            FileUtils.writeByteArrayToFile(new File(pathName), cw.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
