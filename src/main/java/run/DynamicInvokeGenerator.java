package run;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import java.lang.invoke.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 17-9-13.
 * http://blog.csdn.net/xtayfjpk/article/details/42043977
 */
public class DynamicInvokeGenerator {//启动方法定义
    public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type, String value) throws Exception {
        MethodHandle handle = lookup.findVirtual(StringBuilder.class, name, MethodType.methodType(StringBuilder.class)).bindTo(new StringBuilder(value));
        return new ConstantCallSite(handle);
    }
    
    //ASM中定义的方法句柄
    private static final Handle BSM = new Handle(
            H_INVOKESTATIC,
            DynamicInvokeGenerator.class.getName().replace('.', '/'),
            "bootstrap",
            MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class, String.class).toMethodDescriptorString());
    
    public static void main(String[] args) throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_7, ACC_PUBLIC|ACC_SUPER, "StringReverser", null, "java/lang/Object", null);
        //生成main方法
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC|ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        //调用StringBuilder的reverse方法
        mv.visitInvokeDynamicInsn("reverse", "()Ljava/lang/StringBuilder;", BSM, "Hello Dynamic Invoke");//生成invokedynamic指令
        //调用System.out.println(Object x)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        
        
        Files.write(Paths.get("StringReverser.class"), cw.toByteArray());
        
    }
}
