package cn.tellwhy.code.visit.copy;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * Created by aaa on 18-1-4.
 */
public class AopMethodVisitor extends MethodVisitor {
    public AopMethodVisitor(int api, MethodVisitor mv) {
        super(api, mv);
    }
    
    @Override
    public void visitCode() {
        super.visitCode();
        this.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/tellwhy/code/visit/AopInteceptor", "before", "()V", false);
    }
    
    @Override
    public void visitInsn(int opcode) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)// 在返回之前安插after 代码。
            this.visitMethodInsn(Opcodes.INVOKESTATIC, "cn/tellwhy/code/visit/AopInteceptor", "after", "()V", false);
        super.visitInsn(opcode);
    }
    
}
