package jacks.codegen;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.ASM7;

public class ClassPrinter extends ClassVisitor {
    protected ClassPrinter() {
        super(ASM7);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println(name);
    }

    public void visitSource(String source, String debug) {
        super.visitSource(source, debug);
        System.out.println(source);
    }

    public ModuleVisitor visitModule(String name, int access, String version) {
        return super.visitModule(name, access, version);
    }

    public void visitNestHost(String nestHost) {
        super.visitNestHost(nestHost);
    }

    public void visitOuterClass(String owner, String name, String descriptor) {
        super.visitOuterClass(owner, name, descriptor);
        System.out.println("OuterClass: \n  owner: " + owner + "\n  name:" + name + "\n  descriptor: " + descriptor);
    }

    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println("Annotation: \n  descriptor: " + descriptor);
        return super.visitAnnotation(descriptor, visible);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    public void visitAttribute(Attribute attribute) {
        System.out.println("Attribute: \n  attribute: " + attribute);
        super.visitAttribute(attribute);
    }

    public void visitNestMember(String nestMember) {
        super.visitNestMember(nestMember);
    }

    public void visitPermittedSubclass(String permittedSubclass) {
        super.visitPermittedSubclass(permittedSubclass);
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        System.out.println("InnerClass: \n  name: " + name + "\n  outer name: " + outerName + "\n  inner name: " + innerName + "\n  access: " + access);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    public RecordComponentVisitor visitRecordComponent(String name, String descriptor, String signature) {
        return super.visitRecordComponent(name, descriptor, signature);
    }

    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }

    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("Method: \n  name: " + name + "\n  descriptor: " + descriptor + "\n  signature: " + signature + "\n  exceptions: " + exceptions);
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public void visitEnd() {
        super.visitEnd();
    }
}
