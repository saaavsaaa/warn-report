package code.visit;

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