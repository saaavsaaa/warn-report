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
        get.visitVarInsn(ALOAD, 0); //读取局部变量 0(它在为这个方法调用创建帧期间被初始化为 this),并将这个值压入操作数栈中
        //从栈中弹出这个值,即 this,并将这个对象的 ttt 字段(this.ttt)压入栈中
        get.visitFieldInsn(GETFIELD, "code/record/WaitClearCode", "ttt", "I");
        get.visitInsn(ARETURN);  //从栈中弹出这个值,并将其返回给调用者
    
        // 并不一定要给出最优操作数栈大小。任何大于或等于这个最优值的值都可以,尽管这样可能会浪费 该线程执行栈上的内存
        //一个局部变量和一个操作数栈空间,ClassWriter使用0初始化，所以参数有效
        get.visitMaxs(1, 1);
        get.visitEnd();
        
        MethodVisitor set = cv.visitMethod(ACC_PUBLIC, "setTTT", "(Ljava/lang/String;)V", null, null);
        set.visitCode();
        set.visitVarInsn(ALOAD, 1);  //将初始化为 ttt 的局部变量 1 压入操作数栈
        Label label = new Label();
        set.visitJumpInsn(IFNULL, label); //从栈中弹出值,判断是否为null，如果是null,则跳转到由 label 标记指定的指令,否则不做任何事情
        set.visitVarInsn(ALOAD, 0); //将 this 压入操作数栈
        set.visitVarInsn(ALOAD, 1); //压入局部变量 1,在为这个方法调用创建帧期间,以 ttt 参数初始化该变量
        //弹出这两个值,并将 int 值存储在被引用对象的 ttt 字段(this.ttt)中
        set.visitFieldInsn(PUTFIELD, "code/record/WaitClearCode", "AAA", "Ljava/lang/String;");
        Label end = new Label();
        set.visitJumpInsn(GOTO, end); //无条件跳转到由end label 指定的指令,也就是 RETURN 指令
        set.visitLabel(label);
        set.visitFrame(F_SAME, 0, null, 0, null);
        set.visitTypeInsn(NEW, "java/lang/IllegalArgumentException"); //创建一个异常对象,并将它压入操作数栈中
        set.visitInsn(DUP); //DUP 指令在栈中创建一个该对象引用的副本并入栈，为了在INVOKESPECIAL指令将对象引用出栈后还可以使用这个对象的引用
        //INVOKESPECIAL 指令弹出这两个引用之一,并对其调用异常构造器
        set.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
        set.visitInsn(ATHROW); //ATHROW 指令弹出剩下的引用,并将它作为异常抛出
        set.visitLabel(end);
        set.visitFrame(F_SAME, 0, null, 0, null);
        set.visitInsn(RETURN);
        set.visitMaxs(2, 2); //两个局部变量和两个操作数栈空间
        set.visitEnd();
        cv.visitEnd();
    
        cr.accept(cv, 0);
        return cw;
    }
    
    public static void main(String[] args) throws IOException {
        byte[] data = ResourceUtil.loadFile("ClassCode.class");
        ClassReader cr = new ClassReader(data);
        ClassWriter cw = new ClassWriter(0);
    
        ClassVisitor add = new AddTimerAdapter(cw);

        cr.accept(add, 0);

        byte[] b = cw.toByteArray();
        ResourceUtil.write("ClassCode.class", b);
    }
}

class AddTimerAdapter extends ClassVisitor {
    private String owner;
    private boolean isInterface;
    
    public AddTimerAdapter(ClassVisitor cv) {
        super(ASM5, cv);
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        isInterface = (access & ACC_INTERFACE) != 0;
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature,
                exceptions);
        if (!isInterface && mv != null && name.equals("setTTT")) {
            mv = new AddTimerMethodAdapter(mv);
        }
        return mv;
    }
    
    @Override
    public void visitEnd() {
        if (!isInterface) {
            FieldVisitor fv = cv.visitField(ACC_PUBLIC + ACC_STATIC, "exec", "J", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        cv.visitEnd();
    }
}

class AddTimerMethodAdapter extends MethodVisitor {
    String owner = "code/record/WaitClearCode";
    public AddTimerMethodAdapter(MethodVisitor mv) {
        super(ASM4, mv);
    }
    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, owner, "exec", "J");
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitInsn(LSUB);
        mv.visitFieldInsn(PUTSTATIC, owner, "exec", "J");
    }
    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitFieldInsn(GETSTATIC, owner, "exec", "J");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitInsn(LADD);
            mv.visitFieldInsn(PUTSTATIC, owner, "exec", "J");
        }
        mv.visitInsn(opcode);
    }
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitMaxs(maxStack + 4, maxLocals);
    }
}

//方法可以像类一样进行转换,也就是使用一个方法适配器将它收到的方法调用转发出去,并进行一些修改:改变参数可用于改变各具体指令;不转发某一收到的调用
//将删除一条指令;在接收到的调用之间插入调用,将增加新的指令。MethodVisitor 类提供了这样一种方法适配器的基本实现,它只是转发它接收到的所有方法,而未做任何其他事情。
class RemoveNopClassAdapter extends ClassVisitor {
    public RemoveNopClassAdapter(ClassVisitor cv) {
        super(ASM5, cv);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv;
        mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && !name.equals("<init>")) { //不移除构造器中的该指令
            mv = new RemoveNopAdapter(mv);
        }
        return mv;
    }
}

class RemoveNopAdapter extends MethodVisitor {
    public RemoveNopAdapter(MethodVisitor mv) {
        super(ASM4, mv);
    }
    
    @Override
    public void visitInsn(int opcode) {
        if (opcode != NOP) { //删除方法中的 NOP指令(因为它们不做任何事情,所以删除它们没有任何问题)
            mv.visitInsn(opcode);
        }
    }
}
