package code.visit;

import jdk.internal.org.objectweb.asm.*;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

/**
 * Created by aaa on 18-3-7.
 * 这个类因为一些原因暂时没测试
 */
public class TraceAnnotationVisitor {
}

class RemoveAnnotationAdapter extends ClassVisitor {
    private String annDesc;
    public RemoveAnnotationAdapter(ClassVisitor cv, String annDesc) {
        super(ASM5, cv);
        this.annDesc = annDesc;
    }
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean vis) {
        if (desc.equals(annDesc)) {
            return null;
        }
        return cv.visitAnnotation(desc, vis);
    }
}

class AddAnnotationAdapter extends ClassVisitor {
    private String annotationDesc;
    private boolean isAnnotationPresent;
    public AddAnnotationAdapter(ClassVisitor cv, String annotationDesc) {
        super(ASM5, cv);
        this.annotationDesc = annotationDesc;
    }
    @Override public void visit(int version, int access, String name,String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
    }
    @Override public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (visible && desc.equals(annotationDesc)) {
            isAnnotationPresent = true;
        }
        return cv.visitAnnotation(desc, visible);
    }
    @Override public void visitInnerClass(String name, String outerName, String innerName, int access) {
        addAnnotation();
        cv.visitInnerClass(name, outerName, innerName, access);
    }
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        addAnnotation();
        return cv.visitField(access, name, desc, signature, value);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        addAnnotation();
        return cv.visitMethod(access, name, desc, signature, exceptions);
    }
    @Override public void visitEnd() {
        addAnnotation();
        cv.visitEnd();
    }
    private void addAnnotation() {
        if (!isAnnotationPresent) {
            AnnotationVisitor av = cv.visitAnnotation(annotationDesc, true);
            if (av != null) {
                av.visitEnd();
            }
            isAnnotationPresent = true;
        }
    }
}

//此代码说明如何用 ACC_ANNOTATION 标志创建一个注释类,并说明如何创建两个类注释,一个没有值,一个具有枚举值。方法注释和参数注释可以采用 MethodVisitor 类中定义的
//visitAnnotation 和 visitParameterAnnotation 方法以类似方式创建。
class DeprecatedDump implements Opcodes {
    public static byte[] dump() throws Exception {
        ClassWriter cw = new ClassWriter(0);
        AnnotationVisitor av;
        cw.visit(V1_5, ACC_PUBLIC + ACC_ANNOTATION + ACC_ABSTRACT
                        + ACC_INTERFACE, "java/lang/Deprecated", null,
                "java/lang/Object",
                new String[] { "java/lang/annotation/Annotation" });
        {
            av = cw.visitAnnotation("Ljava/lang/annotation/Documented;",
                    true);
            av.visitEnd();
        }
        {
            av = cw.visitAnnotation("Ljava/lang/annotation/Retention;", true);
            av.visitEnum("value", "Ljava/lang/annotation/RetentionPolicy;",
                    "RUNTIME");
            av.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }
}