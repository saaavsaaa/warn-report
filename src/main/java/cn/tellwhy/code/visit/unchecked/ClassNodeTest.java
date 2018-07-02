package cn.tellwhy.code.visit.unchecked;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

/**
 * Created by aaa on 18-3-7.
 */
public class ClassNodeTest {
    public static void main(String[] args) throws IOException {
        ClassNode cn = new ClassNode(ASM5);
        ClassReader cr = new ClassReader("...");
        cr.accept(cn, 0);
        
        // 可以在这里根据需要转换 cn
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        byte[] b = cw.toByteArray();
    }
    
    //与 ChangeVersionAdapter 的程序图进行对比,可以看出,ca 和 cw 之间的事
    // 件发生在 cr 和 ca 之间的事件之后,而不是像正常类适配器一样同时进行。事实上,对于所有
    // 基于树的转换都是如此,同时还解释了为什么它们受到的限制要少于基于事件的转换。
    private void extendsRun() throws IOException {
        ClassWriter cw = new ClassWriter(0);
        ClassVisitor ca = new ExtendsClassAdapter(cw);
        ClassReader cr = new ClassReader("...");
        cr.accept(ca, 0);
        byte[] b = cw.toByteArray();
    }
}

class ExtendsClassAdapter extends ClassNode {
    public ExtendsClassAdapter(ClassVisitor cv) {
        super(ASM5);
        this.cv = cv;
    }
    @Override public void visitEnd() {
        // put your transformation code here
        accept(cv);
    }
}

/*
         这一模式使用两个对象而不是一个,但其工作方式完全与第一种模式相同:接收到的事件用
        于构造一个 ClassNode,它被转换,并在接收到最后一个事件后,变回一个基于事件的表示。
        这两种模式都允许用基于事件的适配器来编写基于树的类适配器。它们也可用于将基于树的
        适配器组合在一起,但如果只需要组合基于树的适配器,那这并非最佳解决方案:在这种情况下,
        使用诸如 ClassTransformer 的类将会避免在两种表示之间进行不必要的转换。
*/
class DelegageClassAdapter extends ClassVisitor {
    ClassVisitor next;
    public DelegageClassAdapter(ClassVisitor cv) {
        super(ASM5, new ClassNode());
        next = cv;
    }
    @Override public void visitEnd() {
        ClassNode cn = (ClassNode) cv;
// 将转换代码放在这里
        cn.accept(next);
    }
}
