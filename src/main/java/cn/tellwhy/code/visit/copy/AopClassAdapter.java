package cn.tellwhy.code.visit.copy;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

/**
 * Created by aaa on 18-1-4.
 */
public class AopClassAdapter extends ClassVisitor{
    public AopClassAdapter(int api, ClassVisitor cv) {
        super(api, cv);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        // 对test开头的方法进行特殊处理
        if (name.startsWith("test")) {
            mv = new AopMethodVisitor(this.api, mv);
        }
        return mv;
    }
}
