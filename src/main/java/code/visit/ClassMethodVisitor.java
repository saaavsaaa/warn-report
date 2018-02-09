package code.visit;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.commons.AnalyzerAdapter;
import jdk.internal.org.objectweb.asm.commons.LocalVariablesSorter;
import util.ResourceUtil;

import java.io.IOException;

import static jdk.internal.org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-2-6.
 */
public class ClassMethodVisitor {
    
    public static ClassWriter add(ClassReader cr, ClassWriter cw) throws IOException {
//        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        
        ClassVisitor cv = new ChangeVersionAdapter(cw);
//        ClassVisitor cv = new MethodCheckVisitor(cw);
        
        cv.visit(V1_7, ACC_PUBLIC, "code/record/WaitClearCode", null, "java/lang/Object", null);
//        MethodVisitor get = cv.visitMethod(ACC_PUBLIC, "getTTT", "()Ljava/lang/String;", null, null); // ClassWriterTest
        MethodVisitor get = TraceVisitor.visitMethod(cv, ACC_PUBLIC, "getTTT", "()Ljava/lang/String;", null, null); //跟踪
    
        get.visitCode();
        get.visitVarInsn(ALOAD, 0); //读取局部变量 0(它在为这个方法调用创建帧期间被初始化为 this),并将这个值压入操作数栈中
        //从栈中弹出这个值,即 this,并将这个对象的 ttt 字段(this.ttt)压入栈中
        get.visitFieldInsn(GETFIELD, "code/record/WaitClearCode", "ttt", "I");
        get.visitInsn(ARETURN);  //从栈中弹出这个值,并将其返回给调用者
    
        // 并不一定要给出最优操作数栈大小。任何大于或等于这个最优值的值都可以,尽管这样可能会浪费 该线程执行栈上的内存
        //一个局部变量和一个操作数栈空间,ClassWriter使用0初始化，所以参数有效
        get.visitMaxs(1, 1);
        get.visitEnd();
        
//        MethodVisitor set = cv.visitMethod(ACC_PUBLIC, "setTTT", "(Ljava/lang/String;)V", null, null);
        MethodVisitor set = TraceVisitor.visitMethod(cv, ACC_PUBLIC, "setTTT", "(Ljava/lang/String;)V", null, null); //跟踪
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
//        cr.accept(add, EXPAND_FRAMES);

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
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (!isInterface && mv != null && name.equals("setTTT")) {
//            mv = new AddTimerMethodAdapter(mv);
//            mv = new AddTimerMethodAdapter2("code/record/WaitClearCode", ACC_PUBLIC, name, desc, mv);
//            mv = new AddTimerLocalAdapter(ACC_PUBLIC, desc, mv);
            mv = new AddTimerLocalAndAnalyzerAdapter(ACC_PUBLIC, name, desc, mv);
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
        super(ASM5, mv);
    }
    @Override
    public void visitCode() {
        mv.visitCode();
        mv.visitFieldInsn(GETSTATIC, owner, "exec", "J");
        //最后一个参数,INVOKEINTERFACE时应该是true，其他时候false，否则throw new IllegalArgumentException("INVOKESPECIAL/STATIC on interfaces require ASM 5");
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
        /*
        * 1.更新操作数栈的最大大小。添加的指令压入两个 long 值,因此需要在操作数栈增加四个slot
        * 2.在此方法的开头,操作数栈初始为空,所以我们知道在开头添加的四条指令需要
            一个大小为 4 的栈。还知道所插入的代码不会改变栈的状态(因为它弹出的值的数目与压入的数
            目相同)。因此,如果原代码需要一个大小为 s 的栈,那转换后的方法所需栈的最大大小为 max(4,
            s)。遗憾的是,我们还在返回指令前面添加了四条指令,我们并不知道操作数栈恰在执行这些指
            令之前时的大小。只知道它小于或等于 s。因此,我们只能说,在返回指令之前添加的代码可能
            要求操作数栈的大小达到 s+4。这种最糟情景在实际中很少发生:使用常见编译器时,RETURN
            之前的操作数栈仅包含返回值,即,它的大小最多为 0、1 或 2。但如果希望处理所有可能情景,
            那就需要考虑最糟情景。
        * 3.并不一定要给出最优操作数栈大小。任何大于或等于这个最优值的值都可以,尽管这样可能会浪费 该线程栈上的内存
        */
        mv.visitMaxs(maxStack + 4, maxLocals);
    }
}

/*
可用于获得操作数栈恰在 RETURN 指令之前的大小,从而允许为 visitMaxs 中的 maxStack 计算一个最优的已转换值(事实上,在
实践中并不建议使用这一方法,因为它的效率要远低于使用 COMPUTE_MAXS)
*/
class AddTimerMethodAdapter1 extends AnalyzerAdapter {
    private String owner;
    private int maxStack;
    public AddTimerMethodAdapter1(String owner, int access, String name, String desc, MethodVisitor mv) {
        super(ASM5, owner, access, name, desc, mv);
        this.owner = owner;
    }
    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitFieldInsn(GETSTATIC, owner, "exec", "J");
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitInsn(LSUB);
        mv.visitFieldInsn(PUTSTATIC, owner, "exec", "J");
        maxStack = 4;
    }
    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitFieldInsn(GETSTATIC, owner, "exec", "J");
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitInsn(LADD);
            mv.visitFieldInsn(PUTSTATIC, owner, "exec", "J");
            maxStack = Math.max(maxStack, stack.size() + 4);
        }
        
                
        /*
        stack 字段在 AnalyzerAdapter 类中定义,包含操作数栈中的类型。更准确地说,在一个 visitXxx Insn 中,且在调用被重写的方法之前,
        它会列出操作数栈正好在这条指令之前的状态。注意,必须调用被重写的方法,使 stack 字段被正确更新(因此,用 super 代替源代码中的 mv)
        */
        super.visitInsn(opcode);
    }
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(Math.max(this.maxStack, maxStack), maxLocals);
    }
}

/*
通 过 调 用超 类 中 的 方法 来 插 入 新指 令 : 其 方法 就 是 这 些指 令 的 帧 将 由
AnalyzerAdapter 计算,由于这个适配器会根据它计算的帧来更新 visitMaxs 的参数,所
以我们不需要自己来更新它们
*/
class AddTimerMethodAdapter2 extends AnalyzerAdapter {
    private String owner;
    public AddTimerMethodAdapter2(String owner, int access, String name, String desc, MethodVisitor mv) {
        super(ASM5, owner, access, name, desc, mv);
        this.owner = owner;
    }
    @Override
    public void visitCode() {
        super.visitCode();
        super.visitFieldInsn(GETSTATIC, owner, "exec", "J");
        super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        super.visitInsn(LSUB);
        super.visitFieldInsn(PUTSTATIC, owner, "exec", "J");
    }
    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            super.visitFieldInsn(GETSTATIC, owner, "exec", "J");
            super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            super.visitInsn(LADD);
            super.visitFieldInsn(PUTSTATIC, owner, "exec", "J");
        }
        super.visitInsn(opcode);
    }
}

/*
在对局部变量重新编号后,与该方法相关联的原帧变为无效,在插入新局部变量后更不必说了。
幸好,还是可能避免从头重新计算这些帧的:事实上,并不存在必须添加或删除的帧,
只需对原帧中局部变量的内容进行重新排序,为转换后的方法获得帧就“足够”了。
LocalVariablesSorter 会自动负责完成。如果还需要为你的方法适配器进行增量栈映射帧
更新,可以由这个类的源代码中获得灵感。
*/
class AddTimerLocalAdapter extends LocalVariablesSorter {
    private String owner = "code/record/WaitClearCode";
    private int time;
    public AddTimerLocalAdapter(int access, String desc, MethodVisitor mv) {
        super(ASM5, access, desc, mv);
    }
    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        time = newLocal(Type.LONG_TYPE); //生成一个局部变量存储，而不是直接在exec中减
        mv.visitVarInsn(LSTORE, time);
    }
    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LLOAD, time);
            mv.visitInsn(LSUB);
            mv.visitFieldInsn(GETSTATIC, owner, "exec", "J");
            mv.visitInsn(LADD);
            mv.visitFieldInsn(PUTSTATIC, owner, "exec", "J");
        }
        super.visitInsn(opcode);
    }
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + 4, maxLocals);
    }
}

class AddTimerLocalAndAnalyzerAdapter extends MethodVisitor {
    private String owner = "code/record/WaitClearCode";
    public LocalVariablesSorter lvs;
    public AnalyzerAdapter analyzer;
    private int time;
    private int maxStack;
    public AddTimerLocalAndAnalyzerAdapter(int access, String name, String desc, MethodVisitor mv) {
        super(ASM5, mv);
        lvs = new LocalVariablesSorter(access, desc, mv);
        analyzer = new AnalyzerAdapter(owner, access, name, desc, mv);
    }
    @Override public void visitCode() {
        mv.visitCode();
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        time = lvs.newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(LSTORE, time);
        maxStack = 4;
    }
    @Override public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LLOAD, time);
            mv.visitInsn(LSUB);
            mv.visitFieldInsn(GETSTATIC, owner, "timer", "J");
            mv.visitInsn(LADD);
            mv.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
            maxStack = Math.max(analyzer.stack.size() + 4, maxStack);
        }
        mv.visitInsn(opcode);
    }
    @Override public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitMaxs(Math.max(this.maxStack, maxStack), maxLocals);
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
        super(ASM5, mv);
    }
    
    @Override
    public void visitInsn(int opcode) {
        if (opcode != NOP) { //删除方法中的 NOP指令(因为它们不做任何事情,所以删除它们没有任何问题)
            mv.visitInsn(opcode);
        }
    }
}