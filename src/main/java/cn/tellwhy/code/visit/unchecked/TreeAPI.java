package cn.tellwhy.code.visit.unchecked;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-3-7.
 * 都是外面的另一种实现方式
 */
public class TreeAPI {
    
    public static void main(String[] args){
        checkAndSetT();
    }
    
    //ClassMethodVisitor.add p71
    private static void checkAndSetT(){
        MethodNode mn = new MethodNode(ASM5);
        InsnList il = mn.instructions;
        il.add(new VarInsnNode(ILOAD, 1));
        LabelNode label = new LabelNode();
        il.add(new JumpInsnNode(IFLT, label));
        il.add(new VarInsnNode(ALOAD, 0));
        il.add(new VarInsnNode(ILOAD, 1));
        il.add(new FieldInsnNode(PUTFIELD, "cn/tellwhy/code/record/WaitClearCode", "AAA", "Ljava/lang/String;"));
        LabelNode end = new LabelNode();
        il.add(new JumpInsnNode(GOTO, end));
        il.add(label);
        il.add(new FrameNode(F_SAME, 0, null, 0, null));
        il.add(new TypeInsnNode(NEW, "java/lang/IllegalArgumentException"));
        il.add(new InsnNode(DUP));
        il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false));
        il.add(new InsnNode(ATHROW));
        il.add(end);
        il.add(new FrameNode(F_SAME, 0, null, 0, null));
        il.add(new InsnNode(RETURN));
        mn.maxStack = 2;
        mn.maxLocals = 2;
    }
}

class ClassTransformer {
    protected ClassTransformer ct;
    public ClassTransformer(ClassTransformer ct) {
        this.ct = ct;
    }
    public void transform(ClassNode cn) {
        if (ct != null) {
            ct.transform(cn);
        }
    }
}

class RemoveMethodTransformer extends ClassTransformer {
    private String methodName;
    private String methodDesc;
    
    public RemoveMethodTransformer(ClassTransformer ct, String methodName, String methodDesc) {
        super(ct);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }
    
    @Override
    public void transform(ClassNode cn) {
        Iterator<MethodNode> i = cn.methods.iterator();
        while (i.hasNext()) {
            MethodNode mn = i.next();
            if (methodName.equals(mn.name) && methodDesc.equals(mn.desc)) {
                i.remove();
            }
        }
        super.transform(cn);
    }
}

class AddFieldTransformer extends ClassTransformer {
    private int fieldAccess;
    private String fieldName;
    private String fieldDesc;
    
    public AddFieldTransformer(ClassTransformer ct, int fieldAccess, String fieldName, String fieldDesc) {
        super(ct);
        this.fieldAccess = fieldAccess;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }
    
    @Override
    public void transform(ClassNode cn) {
        boolean isPresent = false;
        for (FieldNode fn : cn.fields) {
            if (fieldName.equals(fn.name)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            cn.fields.add(new FieldNode(fieldAccess, fieldName, fieldDesc, null, null));
        }
        super.transform(cn);
    }
}

class AddTimerTransformer extends ClassTransformer {
    public AddTimerTransformer(ClassTransformer ct) {
        super(ct);
    }
    @Override public void transform(ClassNode cn) {
        for (MethodNode mn : (List<MethodNode>) cn.methods) {
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList insns = mn.instructions;
            if (insns.size() == 0) {
                continue;
            }
            Iterator<AbstractInsnNode> j = insns.iterator();
            while (j.hasNext()) {
                AbstractInsnNode in = j.next();
                int op = in.getOpcode();
                if ((op >= IRETURN && op <= RETURN) || op == ATHROW) {
                    InsnList il = new InsnList();
                    il.add(new FieldInsnNode(GETSTATIC, cn.name, "timer", "J"));
                    il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System",
                            "currentTimeMillis", "()J"));
                    il.add(new InsnNode(LADD));
                    il.add(new FieldInsnNode(PUTSTATIC, cn.name, "timer", "J"));
                    insns.insert(in.getPrevious(), il);
                }
            }
            InsnList il = new InsnList();
            il.add(new FieldInsnNode(GETSTATIC, cn.name, "timer", "J"));
            il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false));
            il.add(new InsnNode(LSUB));
            il.add(new FieldInsnNode(PUTSTATIC, cn.name, "timer", "J"));
            insns.insert(il);
            mn.maxStack += 4;
        }
        int acc = ACC_PUBLIC + ACC_STATIC;
        cn.fields.add(new FieldNode(acc, "timer", "J", null, null));
        super.transform(cn);
    }
}

class TreeMethodTransformer extends ClassTransformer{
    TreeMethodTransformer mt;
    public TreeMethodTransformer(TreeMethodTransformer mt) {
        super(mt);
    }
    
    protected void transform(MethodNode mn){
    }
}

class RemoveGetFieldPutFieldTransformer extends TreeMethodTransformer {
    public RemoveGetFieldPutFieldTransformer(TreeMethodTransformer mt) {
        super(mt);
    }
    
    @Override
    public void transform(MethodNode mn) {
        InsnList insns = mn.instructions;
        Iterator<AbstractInsnNode> i = insns.iterator();
        while (i.hasNext()) {
            AbstractInsnNode i1 = i.next();
            if (isALOAD0(i1)) {
                AbstractInsnNode i2 = getNext(i1);
                if (i2 != null && isALOAD0(i2)) {
                    AbstractInsnNode i3 = getNext(i2);
                    if (i3 != null && i3.getOpcode() == GETFIELD) {
                        AbstractInsnNode i4 = getNext(i3);
                        if (i4 != null && i4.getOpcode() == PUTFIELD) {
                            if (sameField(i3, i4)) {
                                while (i.next() != i4) {
                                    /*
                                    必须将迭代器放在必须删除的指令之后(因为不可能删除恰在当前
                                    指令之后的指令)。基于访问器和基于树的实现都可以在被检测序列的中部检测到标记和帧,在
                                    这种情况下,不要删除它。但要忽略序列中的行号(见 getNext 方法)
                                    ,使用基于树的 API 时
                                    的代码数量要多于使用核心 API 的情况。但是,这两种实现之间的主要区别是:在使用树 API
                                    时,不需要状态机。特别是有三个或更多个连续 ALOAD 0 指令的特殊情景(它很容易被忽视)
                                    ,不再成为问题了。
                                    */
                                }
                                insns.remove(i1);
                                insns.remove(i2);
                                insns.remove(i3);
                                insns.remove(i4);
                            }
                        }
                    }
                }
            }
        }
        super.transform(mn);
    }
    private static AbstractInsnNode getNext(AbstractInsnNode insn) {
        do {
            insn = insn.getNext();
            if (insn != null && !(insn instanceof LineNumberNode)) {
                break;
            }
        } while (insn != null);
        return insn;
    }
    private static boolean isALOAD0(AbstractInsnNode i) {
        return i.getOpcode() == ALOAD && ((VarInsnNode) i).var == 0;
    }
    private static boolean sameField(AbstractInsnNode i,
                                     AbstractInsnNode j) {
        return ((FieldInsnNode) i).name.equals(((FieldInsnNode) j).name);
    }
}


/*
    利用上述实现,一条给定指令可能会被查看多次,这是因为在 while 循环中的每一步, i2、
    i3 和 i4 也可能会在这一迭代中被查看(在未来迭代中还会查看它们)
    。事实上,有可能使用一
    种更高效的实现,使每条指令最多被查看一次:
    
    与上一个实现的区别在于 getNext 方法,它现在是对列表迭代器进行操作。当序列被识别
    出来时,迭代器恰好位于它的后面,所以不再需要 while (i.next() != i4)循环。但这里
    再次出现了三个或多个连续 ALOAD 0 指令的特殊情况(见 while (i3 != null)循环)。
*/
class RemoveGetFieldPutFieldTransformer2 extends TreeMethodTransformer {
    public RemoveGetFieldPutFieldTransformer2(TreeMethodTransformer mt) {
        super(mt);
    }
    
    @Override
    public void transform(MethodNode mn) {
        InsnList insns = mn.instructions;
        Iterator i = insns.iterator();
        while (i.hasNext()) {
            AbstractInsnNode i1 = (AbstractInsnNode) i.next();
            if (isALOAD0(i1)) {
                AbstractInsnNode i2 = getNext(i);
                if (i2 != null && isALOAD0(i2)) {
                    AbstractInsnNode i3 = getNext(i);
                    while (i3 != null && isALOAD0(i3)) {
                        i1 = i2;
                        i2 = i3;
                        i3 = getNext(i);
                    }
                    if (i3 != null && i3.getOpcode() == GETFIELD) {
                        AbstractInsnNode i4 = getNext(i);
                        if (i4 != null && i4.getOpcode() == PUTFIELD) {
                            if (sameField(i3, i4)) {
                                insns.remove(i1);
                                insns.remove(i2);
                                insns.remove(i3);
                                insns.remove(i4);
                            }}
                    }
                }
            }
        }
        super.transform(mn);
    }
    
    //auto create
    private boolean sameField(AbstractInsnNode i3, AbstractInsnNode i4) {
        return false;
    }
    
    //auto create
    private boolean isALOAD0(AbstractInsnNode i1) {
        return false;
    }
    
    private static AbstractInsnNode getNext(Iterator i) {
        while (i.hasNext()) {
            AbstractInsnNode in = (AbstractInsnNode) i.next();
            if (!(in instanceof LineNumberNode)) {
                return in;
            }
        }
        return null;
    }
}

/*
下面的转换就是这样一个例子:用向 label 的跳转代替向 GOTO label 指令的跳转,然后
用一个 RETURN 指令代替指向这个 RETURN 指令的 GOTO。实际中,一个跳转指令的目标与这条
指令的距离可能为任意远,可能在它的前面,也可能在其之后。
 */
class OptimizeJumpTransformer extends TreeMethodTransformer {
    public OptimizeJumpTransformer(TreeMethodTransformer mt) {
        super(mt);
    }
    @Override
    public void transform(MethodNode mn) {
        InsnList insns = mn.instructions;
        Iterator<AbstractInsnNode> i = insns.iterator();
        while (i.hasNext()) {
            AbstractInsnNode in = i.next();
            if (in instanceof JumpInsnNode) { //当找到一条跳转指令 in 时
                LabelNode label = ((JumpInsnNode) in).label; // //当找到一条跳转指令 in 时
                AbstractInsnNode target;
                // 当 target == goto l,用 l 代替 label
                while (true) {
                    target = label;
                    
                    while (target != null && target.getOpcode() < 0) {
                        //查找紧跟在这个标记之后出现的指令(不代表实际指令的AbstractInsnNode对象,比如 FrameNode 或 LabelNode,其“操作码”为负)
                        target = target.getNext();
                    }
                    //只要这条指令是 GOTO,就用这条指令的目标代替 label,然后重复上述步骤
                    if (target != null && target.getOpcode() == GOTO) {
                        label = ((JumpInsnNode) target).label;
                    } else {
                        break;
                    }
                }
// 更新目标
                ((JumpInsnNode) in).label = label; //用这个更新后的label 值来代替 in 的目标标记
// 在可能时,用目标指令代替跳转
                if (in.getOpcode() == GOTO && target != null) { //如果 in 本身是一个 GOTO
                    int op = target.getOpcode();
                    if ((op >= IRETURN && op <= RETURN) || op == ATHROW) { //更新后的目标是一条 RETURN指令
// replace ’in’ with clone of ’target’
                        insns.set(in, target.clone(null)); //in 用这个返回指令的克隆副本代替(回想一下,一个指令对象在一个指令列表中不能出现一次以上)。
                    }
                }
            }
        }
        super.transform(mn);
    }
    
    /*
    // 之前
    ILOAD 1
    IFLT label
    ALOAD 0
    ILOAD 1
    PUTFIELD ...
    GOTO end
    label:
    F_SAME
    NEW ...
    DUP
    INVOKESPECIAL ...
    ATHROW
    end:
    F_SAME
    RETURN
    // 之后
    ILOAD 1
    IFLT label
    ALOAD 0
    ILOAD 1
    PUTFIELD ...
    RETURN
    label:
    F_SAME
    NEW ...
    DUP
    INVOKESPECIAL ...
    ATHROW
    end:
    F_SAME
    RETURN
    */
}

//用于类的两种模式实际上对于方法也是有效的,其工作方式完全相同。基于继承的模式
/*
继承模式的一种变体是直接在 ClassAdapter 的 visitMethod 中将它与一个匿名内部类一起使用:
MethodVisitor visitMethod(int access, String name,String desc, String signature, String[] exceptions) {
    return new MethodNode(ASM4, access, name, desc, signature, exceptions)
    {
    @Override public void visitEnd() {
    //将你的转换代码放在这儿
    accept(cv);
    }
    };
}

AnnotationNode 类扩展了 AnnotationVisitor 类,还提供了一个 accept方法,它以一个这种类型的对象为参数,比如具有这个类和方法访问器类的 ClassNode 和
MethodNode 类。可进行“匿名内部类”的变体,使其适用于注释
public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return new AnnotationNode(ASM4, desc) {
    @Override public void visitEnd() {
    // 将注释转换代码放在这里
    accept(cv.visitAnnotation(desc, visible));
    }
};

*/
class MethodAdapter1 extends MethodNode {
    public MethodAdapter1(int access, String name, String desc, String signature, String[] exceptions, MethodVisitor mv) {
        super(ASM5, access, name, desc, signature, exceptions);
        this.mv = mv;
    }
    @Override public void visitEnd() {
// 将你的转换代码放在这儿
        accept(mv);
    }
}
//基于委托的模式
class MethodAdapter2 extends MethodVisitor {
    MethodVisitor next;
    public MethodAdapter2(int access, String name, String desc, String signature, String[] exceptions, MethodVisitor mv) {
        super(ASM5, new MethodNode(access, name, desc, signature, exceptions));
        next = mv;
    }
    @Override public void visitEnd() {
        MethodNode mn = (MethodNode) mv;
//将你的转换代码放在这儿
        mn.accept(next);
    }
}