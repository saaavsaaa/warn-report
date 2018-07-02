package cn.tellwhy.code.visit;

import jdk.internal.org.objectweb.asm.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-2-7.
 */
public abstract class PatternMethodAdapter extends MethodVisitor {
    protected final static int SEEN_NOTHING = 0;
    protected int state;
    public PatternMethodAdapter(int api, MethodVisitor mv) {
        super(api, mv);
    }
    
    @Override
    public void visitInsn(int opcode) {
        visitInsn();
        mv.visitInsn(opcode);
    }
    @Override
    public void visitIntInsn(int opcode, int operand) {
        visitInsn();
        mv.visitIntInsn(opcode, operand);
    }

    protected abstract void visitInsn();
    
    /*
    如果访问介于两条指令之间的一个栈映射帧,那就不
    能删除它们。要处理这两种情况,可以将标记和帧看作是模式匹配算法中的指令。这一点可以在
    PatternMethodAdapter 中完成(注意,visitMaxs 也会调用公用的 visitInsn 方法;它
    用于处理的情景是:方法的末尾是必须被检测序列的一个前缀)
    
    编译后的方法中可能包含有关源文件行号的信息,比如用于异常栈轨迹。
    这一信息用 visitLineNumber 方法访问,它也与指令同时被调用。但是,在一个指令序列的
    中间给出行号,对于转换或删除该指令的可能性不会产生任何影响。解决方法是在模式匹配算法
    中完全忽略它们。
    */
    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        visitInsn();
        mv.visitFrame(type, nLocal, local, nStack, stack);
    }
    @Override
    public void visitLabel(Label label) {
        visitInsn();
        mv.visitLabel(label);
    }
    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        visitInsn();
        mv.visitMaxs(maxStack, maxLocals);
    }
}

/*
    删除对字段
    进行自我赋值的操作,这种操作通常是因为键入错误,比如 ttt = ttt;,或者是在字节代码中, ALOAD0 ALOAD 0 GETFIELD ttt PUTFIELD ttt。
    在实现这一转换之前,将状态机设计为能够识别这一序列
*/
class RemoveGetFieldPutFieldAdapter extends PatternMethodAdapter {
    private final static int SEEN_ALOAD_0 = 1;
    private final static int SEEN_ALOAD_0ALOAD_0 = 2;
    private final static int SEEN_ALOAD_0ALOAD_0GETFIELD = 3;
    private String fieldOwner;
    private String fieldName;
    private String fieldDesc;
    public RemoveGetFieldPutFieldAdapter(MethodVisitor mv) {
        super(ASM5, mv);
    }
    
    /*
    每个转换都标有一个条件(当前指令的值)和一个操作(必须发出的指令序列)。
    找到状态机之后,相应方法适配器的编写就简单了,状态机图片:
    https://github.com/saaavsaaa/saaavsaaa.github.io/blob/master/ppp/ss112713.jpg
    */
    @Override
    public void visitVarInsn(int opcode, int var) {
        switch (state) {
            case SEEN_NOTHING: // S0 -> S1
                if (opcode == ALOAD && var == 0) { //初始状态时，判断当前指令是否是 ALOAD 0
                    state = SEEN_ALOAD_0; //第一步条件符合，初始状态变为状态一
                    return;
                }
                break;
            case SEEN_ALOAD_0: // S1 -> S2
                if (opcode == ALOAD && var == 0) { //状态一时，判断当前指令是否是 ALOAD 0
                    state = SEEN_ALOAD_0ALOAD_0; //第二步条件符合，初始状态变为状态二
                    return;
                }
            case SEEN_ALOAD_0ALOAD_0: // S2 -> S2
                if (opcode == ALOAD && var == 0) { //三个或三个以上的连续 ALOAD 0 时
                    mv.visitVarInsn(ALOAD, 0); //只需要保留两个ALOAD 0，多余的ALOAD 0直接转发执行就可以了
                    return;
                }
                break;
        }
        visitInsn();
        mv.visitVarInsn(opcode, var);
    }
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        switch (state) {
            case SEEN_ALOAD_0ALOAD_0: // S2 -> S3
                if (opcode == GETFIELD) { //状态二时,判断指令并记录对象、属性及标记
                    state = SEEN_ALOAD_0ALOAD_0GETFIELD;
                    fieldOwner = owner;
                    fieldName = name;
                    fieldDesc = desc;
                    return;
                }
                break;
            case SEEN_ALOAD_0ALOAD_0GETFIELD: // S3 -> S0
                //状态三时，判断待赋值的字段如果是值的来源字段则忽略所有状态对应的指令，并将状态重置回初始状态
                if (opcode == PUTFIELD && name.equals(fieldName)) {
                    state = SEEN_NOTHING;
                    return;
                }
                break;
        }
        visitInsn();
        mv.visitFieldInsn(opcode, owner, name, desc);
    }
    @Override protected void visitInsn() {
        //各状态下不符合条件，直接返回初始状态需要将拦截的指令转发出去
        switch (state) {
            case SEEN_ALOAD_0: // S1 -> S0
                mv.visitVarInsn(ALOAD, 0);
                break;
            case SEEN_ALOAD_0ALOAD_0: // S2 -> S0
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                break;
            case SEEN_ALOAD_0ALOAD_0GETFIELD: // S3 -> S0
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, fieldOwner, fieldName, fieldDesc);
                break;
        }
        state = SEEN_NOTHING;
    }
}

/*
在访问 ICONST_0 时,只有当下一条指令是 IADD 时才必须将其删除。将是否删除它的决定推迟到下一
条指令:如果下一指令是 IADD,则删除两条指令,否则,发出 ICONST_0 和当前指令

如果某一指令可能跳转到 ICONST_0 ,这意味着有一个指定这一指令的标记。在删除
了这两条指令后,这个标记将指向跟在被删除 IADD 之后的指令,这正是我们希望的。但如
果某一指令可能跳转到 IADD ,我们就不能删除这个指令序列(不能确保在这一跳转之前,
已经在栈中压入了一个 0)。幸好,在这种情况下, ICONST_0 和 IADD 之间必然有一个标
记,可以很轻松地检测到它。
*/
class RemoveAddZeroAdapter extends PatternMethodAdapter {
    private static int SEEN_ICONST_0 = 1;
    
    public RemoveAddZeroAdapter(MethodVisitor mv) {
        super(ASM5, mv);
    }
    
    @Override
    public void visitInsn(int opcode) {
        if (state == SEEN_ICONST_0) {
            if (opcode == IADD) {
                state = SEEN_NOTHING;
                return;
            }
        }
        visitInsn();
        if (opcode == ICONST_0) {
            state = SEEN_ICONST_0;
            return;
        }
        mv.visitInsn(opcode);
    }
    @Override
    protected void visitInsn() {
        if (state == SEEN_ICONST_0) {
            mv.visitInsn(ICONST_0);
        }
        state = SEEN_NOTHING;
    }
}