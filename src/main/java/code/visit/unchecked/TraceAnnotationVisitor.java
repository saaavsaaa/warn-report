package code.visit.unchecked;

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

/*
默认情况下,ClassReader 类会为它找到的每个标准属性创建一个 Attribute 实例,并
以这个实例为参数,调用 visitAttribute 方法(至于是 ClassVisitor、FieldVisitor,
还是 MethodVisitor 类的该方法,则取决于上下文)。这个实例中包含了属性的原始内容,其形式
为私有字节数组。在访问这种未知属性时,ClassWriter 类就是将这个原始字节数组复制到它
构建的类中。这一默认行为只有在使用 2.2.4 节介绍的优化时才是安全的(除了提高性能外,这
是使用该优化的另一原因)。没有这一选项,原内容可能会与类编写器创建的新常量池不一致,
从而导致类文件被损坏。
默认情况下,非标准属性会以它在已转换类中的形式被复制,它的内容对 ASM 和用户来说
是完全不透明的。如果需要访问这一内容,必须首先定义一个 Attribute 子类,能够对原内容
进行解码并重新编码。还必须在 ClassReader.accept 方法中传送这个类的一个原型实例,使
这个类可以解码这一类型的属性。让我们用一个例子来说明这一点。下面的类可用于运行一个设
想的“注释”特性,它的原始内容是一个 short 值,引用存储在常量池中的一个 UTF8 字符串:

最重要的方法是 read 和 write 方法。read 方法对这一类型的属性的原始内容进行解码,
write 方法执行逆操作。注意,read 方法必须返回一个新的属性实例。为了在读取一个类时实
现这种属性的解码,必须使用:
ClassReader cr = ...;
ClassVisitor cv = ...;
cr.accept(cv, new Attribute[] { new CommentAttribute("") }, 0);
这个“注释”属性将被识别,并为它们中的每一个都创建一个 CommentAttribute 实例
(而未知属性仍将用 Attribute 实例表示)
*/
class CommentAttribute extends Attribute {
    private String comment;
    public CommentAttribute(final String comment) {
        super("Comment");
        this.comment = comment;
    }
    public String getComment() {
        return comment;
    }
    @Override
    public boolean isUnknown() {
        return false;
    }
    @Override
    protected Attribute read(ClassReader cr, int off, int len,char[] buf, int codeOff, Label[] labels) {
        return new CommentAttribute(cr.readUTF8(off, buf));
    }
    @Override
    protected ByteVector write(ClassWriter cw, byte[] code, int len,int maxStack, int maxLocals) {
        return new ByteVector().putShort(cw.newUTF8(comment));
    }
}