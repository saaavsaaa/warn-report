package code.visit;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import util.ResourceUtil;
import util.UnicodeUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-1-30.
 */
public class ClassVisitorAddTest {
    public static void main(String[] args) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        byte[] data = ResourceUtil.loadFile("ClassCode.class");
        ClassReader cr = new ClassReader(data);
        ClassWriter cw = new ClassWriter(0);
    
        cw = deleteField(cr, cw);
//        cw = addField(cr, cw);
//        cw = ClassMethodVisitor.add(cr, cw);
        byte[] b = cw.toByteArray();
        ResourceUtil.write("ClassCode.class", b);
    }
    
    private static ClassWriter deleteField(ClassReader cr, ClassWriter cw){
        DeleteMethodAdapter cv = new DeleteMethodAdapter(cw, "ttt", "Ljava/lang/String;"); //Type.getObjectType("java/lang/String").getDescriptor()
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ttt", "Ljava/lang/String;", "Ljava/lang/String;" , "AAA");
        cv.visitEnd();
        cr.accept(cv, 0);
//        byte[] b = cw.toByteArray();
//        ResourceUtil.write("ClassCode.class", b);
        return cw;
    }
    
    private static ClassWriter addField(ClassReader cr, ClassWriter cw){
        AddFieldAdapter cv = new AddFieldAdapter(cw, ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ttt", "Ljava/lang/String;"); //Type.getObjectType("java/lang/String").getDescriptor()
        cv.visitField("Ljava/lang/String;", "AAA");
        cv.visitEnd();
        cr.accept(cv, 0);
//        byte[] b = cw.toByteArray();
//        ResourceUtil.write("ClassCode.class", b);
        return cw;
    }
}

class DeleteMethodAdapter extends ClassVisitor {
    private String initName;
    private String initDesc;
    public DeleteMethodAdapter(ClassVisitor cv, String initName, String initDesc) {
        super(ASM4, cv);
        this.initName = initName;
        this.initDesc = initDesc;
    }
    
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (name.equals(initName) && desc.equals(initDesc)) {
            // 不要委托至下一个访问器 -> 这样将移除该方法
            return null;
        }
        return cv.visitField(access, name, desc, signature, value);
    }
}

class AddFieldAdapter extends ClassVisitor {
    private int initAcc;
    private String initName;
    private String initDesc;
    private boolean isFieldPresent;
    public AddFieldAdapter(ClassVisitor cv, int initAcc, String initName, String initDesc) {
        super(ASM5, cv);
        this.initAcc = initAcc;
        this.initName = initName;
        this.initDesc = initDesc;
    }
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (name.equals(initName)) {
            isFieldPresent = true;
            return cv.visitField(initAcc, initName, initDesc, signature, value);
        }
        return cv.visitField(access, name, desc, signature, value);
    }
    
    public FieldVisitor visitField(String signature, Object value) {
        isFieldPresent = true;
        return cv.visitField(initAcc, initName, initDesc, signature, value);
    }
    
    @Override
    public void visitEnd() {
        if (!isFieldPresent) {
            FieldVisitor fv = cv.visitField(initAcc, initName, initDesc, null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        cv.visitEnd();
    }
}
