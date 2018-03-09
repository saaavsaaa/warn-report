package code.visit.unchecked;

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
        il.add(new FieldInsnNode(PUTFIELD, "code/record/WaitClearCode", "AAA", "Ljava/lang/String;"));
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

class MethodTransformer extends ClassTransformer{
    public MethodTransformer(RemoveMethodTransformer mt) {
        super(mt);
    }
    
    protected void transform(MethodNode mn){}
}

class RemoveGetFieldPutFieldTransformer extends MethodTransformer {
    public RemoveGetFieldPutFieldTransformer(RemoveMethodTransformer mt) {
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
class RemoveGetFieldPutFieldTransformer2 extends MethodTransformer {
    public RemoveGetFieldPutFieldTransformer2(RemoveMethodTransformer mt) {
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