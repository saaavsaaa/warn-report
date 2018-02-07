package code.visit;

import jdk.internal.org.objectweb.asm.*;
import util.ResourceUtil;

import java.io.IOException;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-2-6.
 */
public class ClassMethodVisitor {
    
    public static ClassWriter add(ClassReader cr, ClassWriter cw) throws IOException {
//        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        
        ClassVisitor cv = new ChangeVersionAdapter(cw);
        
        cv.visit(V1_7, ACC_PUBLIC, "code/record/WaitClearCode", null, "java/lang/Object", null);
        MethodVisitor get = cv.visitMethod(ACC_PUBLIC, "getTTT", "()Ljava/lang/String;", null, null); // ClassWriterTest
    
        get.visitCode();
        get.visitVarInsn(ALOAD, 0);     //读取局部变量 0(它在为这个方法调用创建帧期间被初始化为 this),并将这个值压入操作数栈中
    
        //从栈中弹出这个值,即 this,并将这个对象的 ttt 字段压入栈中, 即 this.ttt
        get.visitFieldInsn(GETFIELD, "code/record/WaitClearCode", "ttt", "I");
        get.visitInsn(ARETURN);  //从栈中弹出这个值,并将其返回给调用者
        get.visitMaxs(1, 1);
        get.visitEnd();
        
        MethodVisitor set = cv.visitMethod(ACC_PUBLIC, "setTTT", "(Ljava/lang/String;)V", null, null);
        set.visitCode();
        set.visitVarInsn(ALOAD, 1);  //将初始化为 ttt 的局部变量 1 压入操作数栈
        Label label = new Label();
        set.visitJumpInsn(IFNULL, label); //从栈中弹出值,判断是否为null，如果是null,则跳转到由 label 标记指定的指令,否则不做任何事情
        set.visitVarInsn(ALOAD, 0); //将 this 压入操作数栈
        set.visitVarInsn(ALOAD, 1); //压入局部变量 1,在为这个方法调用创建帧期间,以 ttt 参数初始化该变量
        //弹出这两个值,并将 int 值存储在被引用对象的 ttt 字段中,即存储在 this.ttt 中
        set.visitFieldInsn(PUTFIELD, "code/record/WaitClearCode", "AAA", "Ljava/lang/String;");
        Label end = new Label();
        set.visitJumpInsn(GOTO, end); //无条件跳转到由end 标记指定的指令,也就是 RETURN 指令
        set.visitLabel(label);
        set.visitFrame(F_SAME, 0, null, 0, null);
        set.visitTypeInsn(NEW, "java/lang/IllegalArgumentException"); //创建一个异常对象,并将它压入操作数栈中
        set.visitInsn(DUP); //DUP 指令在栈中创建一个该对象引用的副本并入栈，为了在INVOKESPECIAL指令将对象引用出栈后还可以使用这个对象的引用
        //INVOKESPECIAL 指令弹出这两个引用之一,并对其调用异常构造器
        set.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
        set.visitInsn(ATHROW); //ATHROW 指令弹出剩下的副本,并将它作为异常抛出
        set.visitLabel(end);
        set.visitFrame(F_SAME, 0, null, 0, null);
        set.visitInsn(RETURN);
        set.visitMaxs(2, 2);
        set.visitEnd();
        cv.visitEnd();
    
        cr.accept(cv, 0);
        return cw;
    }
}
