package code.visit.unchecked;

import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM5;

/**
 * Created by aaa on 18-3-7.
 * 这个类因为一些原因暂时没测试
 */
public class DebugTest {
}

/*
        源代码行编号与字节代码指令之间的映射存储为一个由(line number, label)对组成的列表
        中,放在方法的已编译代码部分中。例如,如果 l1、l2 和 l3 是按此顺序出现的三个标记,则下
        面各对:
        (n1, l1)
        (n2, l2)
        (n3, l3)
        意味着 l1 和 l2 之间的指令来自行 n1,l2 和 l3 之间的指令来自 n2,l3 之后的指令来自行 n3。
        注意,一个给定行号可以出现在几个对中。这是因为,对于出现在一个源代码行中的表达式,其
        在字节代码中的相应指令可能不是连续的。例如,for (init; cond; incr) statement;通常是按以下
        顺序编译的:
        init statement incr cond
        
        源代码中局部变量名与字节代码中局部变量槽之间的映射,以(name, type descriptor, type
        signature, start, end, index)等多元组列表的形式存储在该方法的已编译代码节中。这样一个多元组
        的含义是:在两个标记 start 和 end 之间,槽 index 中的局部变量对应于源代码中的局部变量,其
        名字和类型由多元组的前三个元素组出。注意,编译器可以使用相同的局部变量槽来存储具有不
        同作用范围的不同源局部变量。反之,同一个源代码局部变量可能被编译为一个具有非连续作用
        范围的局部变量槽。例如,有可能存在一种类似如下的情景:
        l1:
        ... // 这里的槽 1 包含局部变量 i
        l2:
        ... // 这里的槽 1 包含局部变量 j
        l3:
        ... // 这里的槽 1 再次包含局部变量 i
        end:
        相应的多元组为:
        ("i", "I", null, l1, l2, 1)
        ("j", "I", null, l2, l3, 1)
        ("i", "I", null, l3, end, 1)
        
        visitLineNumber(n1, l1);
        visitLineNumber(n2, l2);
        visitLineNumber(n3, l3);
        visitLocalVariable("i", "I", null, l1, l2, 1);
        visitLocalVariable("j", "I", null, l2, l3, 1);
        visitLocalVariable("i", "I", null, l3, end, 1);
        
        可以在 ClassReader.accept 方法中使用 SKIP_DEBUG 选项。有了这
        一选项,类读取器不会访问调试信息,不会为它创建人为标记。当然,调试信息会从类中删除,
        因此,只有在不会为应用程序造成问题时才能使用这一选项。
        
        ClassReader 类提供了其他一些选项,比如: SKIP_CODE ,用于跳过对已编译代码的访问(如
        果只需要类的结构,那这个选项是很有用的); SKIP_FRAMES ,用于跳过栈映射帧; EXPAND_FRAMES ,
        用于解压缩这些帧。
*/
class MyAdapter extends MethodVisitor {
    int currentLine;
    public MyAdapter(MethodVisitor mv) {
        super(ASM5, mv);
    }
    
    //visitLineNumber 方法必须在已经访问了作为参数传送的标记之后进行调用。在实践中就是在访问这一标记后立即调用它,
    // 从而可以非常容易地知道一个方法访问器中当前指令的源代码行
    @Override
    public void visitLineNumber(int line, Label start) {
        mv.visitLineNumber(line, start);
        currentLine = line;
    }
    
    
    //类似地,visitLocalVariable 方法方法必须在已经访问了作为参数传送的标记之后调用。下面给出一些方法调用示例,它们对应于上一节给出的名称值对和多元组:
    @Override
    public void visitLocalVariable(String var1, String var2, String var3, Label var4, Label var5, int var6){
        
    }
}
