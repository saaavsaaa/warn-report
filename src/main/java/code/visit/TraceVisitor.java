package code.visit;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.util.ASMifier;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import jdk.internal.org.objectweb.asm.util.Textifier;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;
import util.ResourceUtil;

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
        
        cv.visit(V1_7, ACC_PUBLIC, "code/record/WaitClearCode", null, "java/lang/Object", null); // ClassWriterTest
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ttt", "Ljava/lang/String;", "Ljava/lang/String;" , "AAA");
        cv.visitEnd();
    }
}
