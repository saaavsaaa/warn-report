package code.visit;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;
import util.ResourceUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static jdk.internal.org.objectweb.asm.Opcodes.ACC_FINAL;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static jdk.internal.org.objectweb.asm.Opcodes.ACC_STATIC;

/**
 * Created by aaa on 18-1-31.
 */
public class TraceVisitor {
    
    /*
    注意,可以在生成链或转换链的任意位置使用 TraceClassVisitor,以查看在链中这一
    点发生了什么,并非一定要恰好在 ClassWriter 之前使用。还要注意,有了这个适配器生成
    的类的文本表示形式,可能很轻松地用 String.equals()来对比两个类。
    */
    public static void main(String[] args) throws IOException {
        ClassWriter cw = new ClassWriter(0);
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        TraceClassVisitor cv = new TraceClassVisitor(cw, printWriter);
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "ttt", "Ljava/lang/String;", "Ljava/lang/String;" , "AAA");
        cv.visitEnd();
        System.out.println(sw.toString());
    }
}
