package cn.tellwhy.code.visit.unchecked;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.AnalyzerAdapter;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.internal.org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jdk.internal.org.objectweb.asm.Opcodes.*;
import static jdk.internal.org.objectweb.asm.tree.analysis.BasicValue.REFERENCE_VALUE;

/**
 * Created by aaa on 18-3-12.
 */
public class AnalyzerTest {
}

/*
BasicInterpreter （此处书上应该是写错了）利用在 BasicValue类中定义的七个值集来模拟字节代码指令的效果：
    UNINITIALIZED_VALUE 指“所有可能值”。
    INT_VALUE 指“所有 int、short、byte、boolean 或 char 值”。
    FLOAT_VALUE 指“所有 float 值”。
    LONG_VALUE 指“所有 long 值”。
    DOUBLE_VALUE 指“所有 double 值”。
    REFERENCE_VALUE 指“所有对象和数组值”。
    RETURNADDRESS_VALUE 用于子例程
这个解释器可以用作一个“空的”Interpreter 实现,以构建一个 Analyzer。这个分析器可用于检测方法中的不可及代码。事实上,即使是沿着跳转指令的两条分支,
也不可能到达那些不能由第一条指令到达的代码。

结合OptimizeJump
// 在 OptimizeJump 之后      在 Remove之后
ILOAD 1                     ILOAD 1
IFLT label                  IFLT label
ALOAD 0                     ALOAD 0
ILOAD 1                     ILOAD 1
PUTFIELD ...                PUTFIELD ...
RETURN                      RETURN
label:                      label:
F_SAME                      F_SAME
NEW ...                     NEW ...
DUP                         DUP
INVOKESPECIAL ...           INVOKESPECIAL ...
ATHROW                      ATHROW
end:                        end:
F_SAME
RETURN
标记未被移除:它实际上没有改变最终代码,避免可能会在比如 LocalVariableNode 中引用的情况。
*/
class RemoveUnreachableCodeAdapter extends MethodVisitor {
    String owner;
    MethodVisitor next;
    
    public RemoveUnreachableCodeAdapter(String owner, int access, String name, String desc, MethodVisitor mv) {
        super(ASM5, new MethodNode(access, name, desc, null, null));
        this.owner = owner;
        next = mv;
    }
    
    @Override
    public void visitEnd() {
        MethodNode mn = (MethodNode) mv;
        Analyzer<BasicValue> a = new Analyzer<BasicValue>(new BasicInterpreter());
        try {
            a.analyze(owner, mn);
            Frame<BasicValue>[] frames = a.getFrames();
            AbstractInsnNode[] insns = mn.instructions.toArray();
            for (int i = 0; i < frames.length; ++i) {
                //在分析之后,无论什么样的 Interpreter 实现,由Analyzer.getFrames 方法返回的计算帧,对于不可到达的指令都是 null
                //这一特性可用于非常轻松地实现一个 RemoveUnreachableCodeAdapter 类还有一些更高效的方法,但它们需要编写的代码也更多)
                if (frames[i] == null && !(insns[i] instanceof LabelNode)) {
                    mn.instructions.remove(insns[i]);
                }
            }
        } catch (AnalyzerException ignored) {
        }
        mn.accept(next);
    }
}

/*
BasicVerifier 类扩展 BasicInterpreter 类。它使用的事件集相同,但它会验证对指令的使用是否正确。例如,它会验证 IADD 指令的操
作数为 INTEGER_VALUE 值(而 BasicInterpreter 只是返回结果,即 INTEGER_VALUE)。这个类可在开发类生成器或适配器时进行调试
例如,这个类可以检测出ISTORE 1 ALOAD 1 序列是无效的。它可以包含在像下面这样一个实用工具适配器中
(在实践中,使用 CheckMethodAdapter 类要更简单一些,可以将其配置为使用 BasicVerifier)
*/
class BasicVerifierAdapter extends MethodVisitor {
    String owner;
    MethodVisitor next;
    
    public BasicVerifierAdapter(String owner, int access, String name, String desc, MethodVisitor mv) {
        super(ASM5, new MethodNode(access, name, desc, null, null));
        this.owner = owner;
        next = mv;
    }
    
    @Override
    public void visitEnd() {
        MethodNode mn = (MethodNode) mv;
        Analyzer<BasicValue> a = new Analyzer<BasicValue>(new BasicVerifier());
        try {
            a.analyze(owner, mn);
        } catch (AnalyzerException e) {
            throw new RuntimeException(e.getMessage());
        }
        mn.accept(next);
    }
}


/*
SimpleVerifier 类扩展了 BasicVerifier 类。它使用更多的集合来模拟字节代码指令的执行:事实上,每个类都由它自己的集合表示,这个集合表示了这个类的所有可能对象。因此,
它可以检测出更多的错误,比如如下情况:一个对象的可能值为“所有 Thread 类型的对象”,却对这个对象调用在 String 类中定义的方法。
这个类使用 Java 反射 API,以执行与类层次结构有关的验证和计算。然后,它将一个方法引用的类加载到 JVM 中。这一默认行为可以通过重写这个类的受保护方法来改变。
和 BasicVerifier 一样,这个类也可以在开发类生成器或适配器时使用,以便更轻松地找出 Bug。但它也可以用于其他目的。下面这个转换就是一个例子,它会删除方法中不必要的类
型转换:如果这个分析器发现 CHECKCAST to 指令的操作数是“所有 from 类型的对象”值集,如果 to 是 from 的一个超类,那 CHECKCAST 指令就是不必要的,可以删除。
*/
class MethodTransformer extends ClassTransformer{
    MethodTransformer mt;
    public MethodTransformer(MethodTransformer mt) {
        super(mt);
    }
    
    protected MethodNode transform(MethodNode mn){
        return mn;
    }
}
class RemoveUnusedCastTransformer extends MethodTransformer {
    String owner;
    public RemoveUnusedCastTransformer(String owner, MethodTransformer mt) {
        super(mt);
        this.owner = owner;
    }
    @Override public MethodNode transform(MethodNode mn) {
        Analyzer<BasicValue> a =
                new Analyzer<BasicValue>(new SimpleVerifier());
        try {
            a.analyze(owner, mn);
            Frame<BasicValue>[] frames = a.getFrames();
            AbstractInsnNode[] insns = mn.instructions.toArray();
            for (int i = 0; i < insns.length; ++i) {
                AbstractInsnNode insn = insns[i];
                if (insn.getOpcode() == CHECKCAST) {
                    Frame f = frames[i];
                    if (f != null && f.getStackSize() > 0) {
                        Object operand = f.getStack(f.getStackSize() - 1);
                        Class<?> to = getClass(((TypeInsnNode) insn).desc);
                        Class<?> from = getClass(((BasicValue) operand).getType());
                        if (to.isAssignableFrom(from)) {
                            mn.instructions.remove(insn);
                        }
                    }
                }
            }
        } catch (AnalyzerException ignored) {
        }
        return mt == null ? mn : mt.transform(mn);
    }
    private static Class<?> getClass(String desc) {
        try {
            return Class.forName(desc.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.toString());
        }
    }
    private static Class<?> getClass(Type t) {
        if (t.getSort() == Type.OBJECT) {
            return getClass(t.getInternalName());
        }
        return getClass(t.getDescriptor());
    }
}
//但对于 Java 6 类(或者用 COMPUTE_FRAMES 升级到 Java 6 的类),用 AnalyzerAdapter以核心 API 来完成这一任务要更简单一些,效率要高得多:
class RemoveUnusedCastAdapter extends MethodVisitor {
    public AnalyzerAdapter aa;
    public RemoveUnusedCastAdapter(MethodVisitor mv) {
        super(ASM5, mv);
    }
    @Override public void visitTypeInsn(int opcode, String desc) {
        if (opcode == CHECKCAST) {
            Class<?> to = getClass(desc);
            if (aa.stack != null && aa.stack.size() > 0) {
                Object operand = aa.stack.get(aa.stack.size() - 1);
                if (operand instanceof String) {
                    Class<?> from = getClass((String) operand);
                    if (to.isAssignableFrom(from)) {
                        return;
                    }
                }
            }
        }
        mv.visitTypeInsn(opcode, desc);
    }
    private static Class getClass(String desc) {
        try {
            return Class.forName(desc.replace('/', '.'));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.toString());
        }
    }
}

/*
假定我们希望检测出一些字段访问和方法调用的对象可能是 null,比如在下面的源代码段
中(其中,第一行防止一些编译器检测 Bug,否则它可能会被认作一个“o 可能尚未初始化”错
误):
Object o = null;
while (...) {
o = ...;
}
o.m(...); // 潜在的 NullPointerException!
于是需要一个数据流分析,在对应于最后一行的 INVOKEVIRTUAL 指令处,与 o 对应的底部栈值可能为 null。为此,需要为引用值区分三个集合:包含 null
值的 NULL 集,包含所有非 null 引用值的 NONNULL 集,以及包含所有引用值的 MAYBENULL集。只需要考虑 ACONST_NULL 将 NULL 集压入操作数栈,而所有其他在栈中压入引
用值的指令将压入 NONNULL 集(换句话说,我们考虑任意字段访问或方法调用的结果都不是null,如果不对程序的所有类进行全局分析,那就不可能得到更好的结果)。为表示 NULL 和
NONNULL 集的并集,MAYBENULL 集合是必需的。
也可以通过扩展 BasicInterpreter 类来实现它,如果考虑 BasicValue.REFERENCE_VALUE 对应于 NONNULL 集,那只需重写模拟 ACONST_NULL
执行的方法,使它返回 NULL,还有计算并集的方法:
*/
class IsNullInterpreter extends BasicInterpreter {
    public final static BasicValue NULL = new BasicValue(null);
    public final static BasicValue MAYBENULL = new BasicValue(null);
    public IsNullInterpreter() {
        super(ASM5);
    }
    @Override
    public BasicValue newOperation(AbstractInsnNode insn) throws AnalyzerException {
        if (insn.getOpcode() == ACONST_NULL) {
            return NULL;
        }
        return super.newOperation(insn);
    }
    @Override
    public BasicValue merge(BasicValue v, BasicValue w) {
        if (isRef(v) && isRef(w) && v != w) {
            return MAYBENULL;
        }
        return super.merge(v, w);
    }
    private boolean isRef(Value v) {
        return v == REFERENCE_VALUE || v == NULL || v == MAYBENULL;
    }
}
//检测那些可能导致潜在 null 指针异常的指令
class NullDereferenceAnalyzer {
    
    /*
    用一个 IsNullInterpreter 分析给定方法节点。对于每条指令,检测其引用操作数(如果有的话)的可能值集是不是 NULL 集或 NONNULL 集。
    若是,则这条指令可能导致一个 null 指针异常,将它添加到此类指令的列表中。
    */
    public List<AbstractInsnNode> findNullDereferences(String owner, MethodNode mn) throws AnalyzerException {
        List<AbstractInsnNode> result = new ArrayList<AbstractInsnNode>();
        Analyzer<BasicValue> a =
                new Analyzer<BasicValue>(new IsNullInterpreter());
        a.analyze(owner, mn);
        Frame<BasicValue>[] frames = a.getFrames();
        AbstractInsnNode[] insns = mn.instructions.toArray();
        for (int i = 0; i < insns.length; ++i) {
            AbstractInsnNode insn = insns[i];
            if (frames[i] != null) {
                Value v = getTarget(insn, frames[i]);
                if (v == IsNullInterpreter.NULL || v == IsNullInterpreter.MAYBENULL) {
                    result.add(insn);
                }
            }
        }
        return result;
    }
    //getTarget 方法在帧 f 中返回与 insn 对象操作数相对应的 Value,如果 insn 没有对象
    //操作数,则返回 null。它的主要任务就是计算这个值相对于操作数栈顶端的偏移量,这一数量取决于指令类型
    private static BasicValue getTarget(AbstractInsnNode insn,Frame<BasicValue> f) {
        switch (insn.getOpcode()) {
            case GETFIELD:
            case ARRAYLENGTH:
            case MONITORENTER:
            case MONITOREXIT:
                return getStackValue(f, 0);
            case PUTFIELD:
                return getStackValue(f, 1);
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKEINTERFACE:
                String desc = ((MethodInsnNode) insn).desc;
                return getStackValue(f, Type.getArgumentTypes(desc).length);
        }
        return null;
    }
    private static BasicValue getStackValue(Frame<BasicValue> f, int index) {
        int top = f.getStackSize() - 1;
        return index <= top ? f.getStack(top - index) : null;
    }
}

/*
用于计算圈复杂度的算法可以用 ASM 分析框架来实现(还有仅基于核心 API 的更高效方法,
只是它们需要编写更多的代码)。第一步是构建控制流图。我们在本章开头曾经说过,可以通过
重写 Analyzer 类的 newControlFlowEdge 方法来构建。这个类将节点表示为 Frame 对象。
*/
class Node<V extends Value> extends Frame<V> {
    Set< Node<V> > successors = new HashSet< Node<V> >();
    public Node(int nLocals, int nStack) {
        super(nLocals, nStack);
    }
    public Node(Frame<? extends V> src) {
        super(src);
    }
}
//可以提供一个 Analyzer 子类,用来构建控制流图,并用它的结果来计算边数、节点数,最终计算出圈复杂度:
class CyclomaticComplexity {
    public int getCyclomaticComplexity(String owner, MethodNode mn)
            throws AnalyzerException {
        Analyzer<BasicValue> a =
                new Analyzer<BasicValue>(new BasicInterpreter()) {
                    protected Frame<BasicValue> newFrame(int nLocals, int nStack) {
                        return new Node<BasicValue>(nLocals, nStack);
                    }
                    protected Frame<BasicValue> newFrame(Frame<? extends BasicValue> src) {
                        return new Node<BasicValue>(src);
                    }
                    protected void newControlFlowEdge(int src, int dst) {
                        Node<BasicValue> s = (Node<BasicValue>) getFrames()[src];
                        s.successors.add((Node<BasicValue>) getFrames()[dst]);
                    }
                };
        a.analyze(owner, mn);
        Frame<BasicValue>[] frames = a.getFrames();
        int edges = 0;
        int nodes = 0;
        for (int i = 0; i < frames.length; ++i) {
            if (frames[i] != null) {
                edges += ((Node<BasicValue>) frames[i]).successors.size();
                nodes += 1;
            }
        }
        return edges - nodes + 2;
    }
}