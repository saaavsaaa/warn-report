package code.visit;

import cn.tellwhy.code.visit.VisitClassLoader;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import cn.tellwhy.util.ResourceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-1-5.
 */
public class ClassVisitorTest {
    
    // 转换所有类,则须将转换放在 ClassFileTransformer 内部
    // https://github.com/saaavsaaa/AttachAgent/blob/master/src/main/java/cn/tellwhy/report/agent/AgentTest.java #premain
    
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        changeVersion();
//        visit();
    }
    
    private static void visit() throws IOException {
        String path = "code.record.WaitClearCode";
        ClassReader cr = new ClassReader(path);
        
        
        ClassWriter cw = new ClassWriter(0);
        // cv 将所有事件转发给 cw
        ClassVisitor cv = new ClassVisitor(ASM5, cw) { };
//        ClassReader cr = new ClassReader(b1);
        cr.accept(cv, 0);
        byte[] b2 = cw.toByteArray();
    }
    
    public static void changeVersion() throws IOException {
        String path = "code.record.WaitClearCode";

        VisitClassLoader classLoader = new VisitClassLoader(Thread.currentThread().getContextClassLoader());
    
        ClassReader cr = new ClassReader(path);
        ClassWriter cw = new ClassWriter(cr, 0);
        ChangeVersionAdapter ca = new ChangeVersionAdapter(cw);     //如 果ClassReader 检测到这个 ClassVisitor 返回的 MethodVisitor 来自一个ClassWriter,
                                                                    // 这意味着这个方法的内容将不会被转换,事实上,应用程序甚至不会看到其内容。
        cr.accept(ca, 0);                                           //在 ClassReader 组 件 的 accept 方 法 参 数 中 传 送 了 ClassVisitor
                                                                    //在这种情况下,ClassReader 组件不会分析这个方法的内容,不会生成相应事件,只
                                                                    //是复制 ClassWriter 中表示这个方法的字节数组
/*
        执行这一优化后,由于 ChangeVersionAdapter 没有转换任何方法,所以以上代码的
        速度可以达到之前代码的两倍。对于转换部分或全部方法的常见转换,这一速度提升幅度可能要
        小一些,但仍然是很可观的:实际上在 10%到 20%的量级。遗憾的是,这一优化需要将原类中
        定义的所有常量都复制到转换后的类中。对于那些增加字段、方法或指令的转换来说,这一点不
        成问题,但对于那些要移除或重命名许多类成员的转换来说,这一优化将导致类文件大于未优化
        时的情况。因此,建议仅对“增加性”转换应用这一优化。
*/
        byte[] b = cw.toByteArray();
    
        VisitClassLoader visitClassLoader = new VisitClassLoader();
        Class<?> c = visitClassLoader.defineClass("code.record.WaitClearCode", b);
        System.out.println(c.getTypeName());
    
        ResourceUtil.write("ClassCode.class", b);
    }
}


class ChangeVersionAdapter extends ClassVisitor {
    public ChangeVersionAdapter(ClassVisitor cv) {
        super(ASM5, cv);
    }
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(V1_7, access, name, signature, superName, interfaces); //major version
    }
    
/*
    $ javap -verbose ClassCode.class
    Classfile /home/aaa/Github/warn-report/ClassCode.class
    Last modified Jan 29, 2018; size 4903 bytes
    MD5 checksum e9145f40a4cf70856ea765257bbc512c
    Compiled from "WaitClearCode.java"
    public class code.record.WaitClearCode
    minor version: 0
    major version: 51
    
    $ javap -verbose ClearedCode.class
    Classfile /home/aaa/Github/warn-report/target/classes/code/record/ClearedCode.class
    Last modified Oct 26, 2017; size 5450 bytes
    MD5 checksum 07893778b3ba9f191307495a6b3a0855
    Compiled from "ClearedCode.java"
    public class code.record.ClearedCode
    minor version: 0
    major version: 52
*/
    
}

class MultiClassAdapter extends ClassVisitor {
    protected ClassVisitor[] cvs;
    
    public MultiClassAdapter(ClassVisitor[] cvs) {
        super(ASM5);
        this.cvs = cvs;
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        for (ClassVisitor cv : cvs) {
            cv.visit(version, access, name, signature, superName, interfaces);
        }
    }
}