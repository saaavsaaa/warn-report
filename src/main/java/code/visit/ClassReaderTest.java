package code.visit;

import jdk.internal.org.objectweb.asm.*;

import java.io.IOException;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM4;

/**
 * Created by aaa on 18-1-15.
 */
public class ClassReaderTest {
    public static void main(String[] args) throws IOException {
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader("code.record.WaitClearCode");
        cr.accept(cp, 0);
    }
}

class ClassPrinter extends ClassVisitor {
    public ClassPrinter() {
        super(ASM4);
    }
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println(name + " extends " + superName + " {");
    }
    
    public void visitSource(String source, String debug) {
    }
    
    public void visitOuterClass(String owner, String name, String desc){
    }
    
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }
    
    public void visitAttribute(Attribute attr) {
    }
    
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println(" " + desc + " " + name);
        return null;
    }
    
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println(" " + name + desc);
        return null;
    }
    
    public void visitEnd() {
        System.out.println("}");
    }
}
