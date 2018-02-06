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
        MethodVisitor get = cv.visitMethod(ACC_PUBLIC, "get", "()V", null, null); // ClassWriterTest
    
        get.visitCode();
        get.visitVarInsn(ALOAD, 0);
        get.visitFieldInsn(GETFIELD, "code/record/WaitClearCode", "AAA", "I");
        get.visitInsn(IRETURN);
        get.visitMaxs(1, 1);
        get.visitEnd();
    
    
        MethodVisitor set = cv.visitMethod(ACC_PUBLIC, "set", "(I)V", null, null);
        set.visitCode();
        set.visitVarInsn(ILOAD, 1);
        Label label = new Label();
        set.visitJumpInsn(IFLT, label);
        set.visitVarInsn(ALOAD, 0);
        set.visitVarInsn(ILOAD, 1);
        set.visitFieldInsn(PUTFIELD, "code/record/WaitClearCode", "AAA", "I");
        Label end = new Label();
        set.visitJumpInsn(GOTO, end);
        set.visitLabel(label);
        set.visitFrame(F_SAME, 0, null, 0, null);
        set.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
        set.visitInsn(DUP);
        set.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
        set.visitInsn(ATHROW);
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
