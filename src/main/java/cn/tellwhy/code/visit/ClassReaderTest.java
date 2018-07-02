package cn.tellwhy.code.visit;

import jdk.internal.org.objectweb.asm.*;

import java.io.IOException;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM4;

/**
 * Created by aaa on 18-1-15.
 */
public class ClassReaderTest {
    public static void main(String[] args) throws IOException {
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader("code.record.ClearedCode");
        cr.accept(cp, 0);
    }
}

class ClassPrinter extends ClassVisitor {
    public ClassPrinter() {
        super(ASM4);
    }
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("signature :" + signature + ", version : " + version + ", interfaces: " + interfaces);
        System.out.println("visit =====name :" + name + " extends superName:" + superName + " {");
    }
    
    public void visitSource(String source, String debug) {
        System.out.println("visitSource =====================source : " + source + ", debug" + debug);
    }
    
    public void visitOuterClass(String owner, String name, String desc){
        System.out.println("visitOuterClass =====================owner : " + owner + ", name" + name + ", desc" + desc);
    }
    
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        System.out.println("visitAnnotation ===============================desc:" + desc);
        return null;
    }
    
    public void visitAttribute(Attribute attr) {
        System.out.println("visitAttribute ======================" + attr.type);
    }
    
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }
    
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println("visitField ===============================desc:" + desc + ", name :" + name + ", signature :" + signature + ", value :" + value);
        return null;
    }
    
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("visitMethod ===============================desc:" + desc + ", name :" + name + ", signature :" + signature);
        return null;
    }
    
    public void visitEnd() {
        System.out.println("visitEnd ===============================}");
    }
}
