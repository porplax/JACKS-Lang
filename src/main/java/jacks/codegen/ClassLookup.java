package jacks.codegen;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.ASM7;

public class ClassLookup extends ClassVisitor {
    String methodName = "";
    String methodDesc;
    String methodSignature;
    String[] methodException;
    int methodAccess;

    String fieldName = "";
    String fieldDesc;
    String fieldSignature;
    Object fieldValue;
    int fieldAccess;

    protected ClassLookup() {
        super(ASM7);
    }

    public void getMethod(String methodName) {
        this.methodName = methodName;
    }

    public void getField(String fieldName) {
        this.fieldName = fieldName;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
    }

    public ModuleVisitor visitModule(String name, int access, String version) {
        return super.visitModule(name, access, version);
    }

    public void visitNestHost(String nestHost) {
        super.visitNestHost(nestHost);
    }

    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(owner, name, descriptor);
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    public void visitNestMember(String nestMember) {
        super.visitNestMember(nestMember);
    }

    public void visitPermittedSubclass(String permittedSubclass) {
        super.visitPermittedSubclass(permittedSubclass);
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        super.visitInnerClass(name, outerName, innerName, access);
    }

    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return super.visitRecordComponent(name, descriptor, signature);
    }

    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (fieldName.equals(name)) {
            fieldAccess = access;
            fieldDesc = descriptor;
            fieldSignature = signature;
            fieldValue = value;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        if (methodName.equals(name)) {
            methodAccess = access;
            methodDesc = descriptor;
            methodSignature = signature;
            methodException = exceptions;
        }

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public void visitEnd() {
        super.visitEnd();
    }
}
