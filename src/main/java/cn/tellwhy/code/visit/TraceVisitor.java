package cn.tellwhy.code.visit;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-1-31.
 */
public class TraceVisitor {
    
    /*
    注意,可以在生成链或转换链的任意位置使用 TraceClassVisitor,以查看在链中这一点发生了什么
    */
    public static void main(String[] args) throws IOException {
        print();
        System.out.println("--------------------------------------------------------");
        printWithVisit();
    }
    
    private static void print(){
        ClassWriter cw = new ClassWriter(0);
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        TraceClassVisitor cv = new TraceClassVisitor(cw, printWriter);
    
//        Textifier text = new Textifier();
//        TraceClassVisitor cv = new TraceClassVisitor(cw, text, printWriter);
        
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ttt", "Ljava/lang/String;", "Ljava/lang/String;" , "AAA");
        cv.visitEnd();
        System.out.println(sw.toString());
    }
    
    private static void printWithVisit(){
        ClassWriter cw = new ClassWriter(0);
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        ASMifier as = new ASMifier();        //java -classpath asm.jar:asm-util.jar /org.objectweb.asm.util.ASMifier \java.lang.Runnable
        TraceClassVisitor cv = new TraceClassVisitor(cw, as, printWriter);
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ttt", "Ljava/lang/String;", "Ljava/lang/String;" , "AAA");
        cv.visitEnd();
        System.out.println(sw.toString());
    }
    
    public static MethodVisitor visitMethod(ClassVisitor cv, int access, String name, String desc, String signature, String[] exceptions) {
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) { // 如果必须跟踪此方法
            Printer p = new Textifier(ASM5) {
                @Override
                public void visitMethodEnd() {
                    print(printWriter); // 在其被访问后输出它
                    System.out.println(sw.toString());
                }
            };
            mv = new TraceMethodVisitor(mv, p);
        }
        return mv;
    }
}

class CheckVisitor{
    
    /*
    * 可以在一个生成链或转换链的任意位置使用CheckClassAdapter
    * */
    public static void main(String[] args) throws IOException {
        ClassWriter cw = new ClassWriter(0);
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        TraceClassVisitor tcv = new TraceClassVisitor(cw, printWriter);
        CheckClassAdapter cv = new CheckClassAdapter(tcv);
    
//        ClassWriter cw = new ClassWriter(0);
//        CheckClassAdapter cca = new CheckClassAdapter(cw);
//        TraceClassVisitor cv = new TraceClassVisitor(cca, printWriter);
        
        cv.visit(V1_7, ACC_PUBLIC, "cn/tellwhy/code/record/WaitClearCode", null, "java/lang/Object", null); // ClassWriterTest
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ttt", "Ljava/lang/String;", "Ljava/lang/String;" , "AAA");
        cv.visitEnd();
    }
}

class MethodCheckVisitor extends ClassVisitor{
    public MethodCheckVisitor(ClassVisitor cv) {
        super(ASM5, cv);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            /*
            这一适配器并没有验证字节代码是正确的:例如,它没有检测出 ISTORE 1 ALOAD 1 是无效的。实际上,
            如果使用 CheckMethodAdapter 的其他构造器(见 Javadoc),并且在 visitMaxs
            中提供有效的 maxStack 和 maxLocals 参数,那这种错误是可以被检测出来的。
            */
            mv = new CheckMethodAdapter(mv);
            ((CheckMethodAdapter)mv).version = V1_7;
        }
//        return new WaitCheckAdapter(mv);
        return mv;
    }
}

class CheckMethodTestAdapter extends CheckMethodAdapter{
    public CheckMethodTestAdapter(MethodVisitor methodVisitor) {
        super(methodVisitor);
        this.version = V1_7;
    }
}