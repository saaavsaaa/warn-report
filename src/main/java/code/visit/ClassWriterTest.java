package code.visit;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Created by aaa on 18-1-15.
 */
public class ClassWriterTest {
    
    /*
        package pkg;
        public interface Comparable extends Mesurable {
        int LESS = -1;
        int EQUAL = 0;
        int GREATER = 1;
        int compareTo(Object o);
        }
        对 visit 方法的调用定义了类的标头。
        V1_5 参数是一个常数,与所有其他 ASM 常量一样,在 ASM Opcodes 接口中定义。它指明了类的版本——Java 1.5。
        ACC_XXX 常量是与 Java 修饰符对应的标志。这里规定这个类是一个接口,而且它是 public 和 abstract 的(因为它不能被实例化)。
        下一个参数以内部形式规定了类的名字(见 2.1.2 节)。回忆一下,已编译类不包含Package 和 Import 部分,因此,所有类名都必须是完全限定的。
        下一个参数对应于泛型(见4.1 节)。在我们的例子中,这个参数是 null,因为这个接口并没有由类型变量进行参数化。
        第五个参数是内部形式的超类(接口类隐式继承自 Object)。
        最后一个参数是一个数组,其中是被扩展的接口,这些接口由其内部名指定
        用于定义三个接口字段。
        第一个参数是一组标志,对应于 Java 修饰符。这里规定这些字段是 public、final 和 static 的。
        第二个参数是字段的名字,与它在源代码中的显示相同。
        第三个参数是字段的类型,采用类型描述符形式。这里,这些字段是 int 字段,它们的描述符是 I。
        第四个参数对应于泛型。在我们的例子中,它是 null,因为这些字段类型没有使用泛型。
        最后一个参数是字段的常量值:这个参数必须仅用于真正的常量字段,也就是 final static 字段。对于其他字段,它必须为 null。由于此处没 有 注 释 , 所 以 立 即 调 用 所 返 回 的 FieldVisitor 的 visitEnd 方 法 , 即 对 其visitAnnotation 或 visitAttribute 方法没有任何调用。
    */
    public static void main(String[] args){
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "code/visit/VisitTest", null, "java/lang/Object", new String[] { "code/record/ClearedCode" });
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I", null, new Integer(0)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I", null, new Integer(1)).visitEnd();
        /*
        visitMethod 调用用于定义 compareTo 方法,bao
        同样,第一个参数是一组对应于 Java 修饰符的标志。
        第二个参数是方法名,与其在源代码中的显示一样。
        第三个参数是方法的描述符。
        第四个参数对应于泛型。在我们的例子中,它是 null,因为这个方法没有使用泛型。最后一个参
        数是一个数组,其中包括可由该方法抛出的异常,这些异常由其内部名指明。它在这里为 null,
                因为这个方法没有声明任何异常。visitMethod 方法返回 MethodVisitor(见图 3.4),可用
                于定义该方法的注释和属性,最重要的是这个方法的代码。这里,由于没有注释,而且这个方法
                是抽象的,所以我们立即调用所返回的 MethodVisitor 的 visitEnd 方法。
        */
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "run", "(Ljava/lang/Object;)I", null, null).visitEnd();
        
        /*
        对 visitEnd 的最后一个调用是为了通知 cw:这个类已经结束,对 toByteArray 的调用用于以字节数组的形式提取它。
        */
        cw.visitEnd();
        byte[] b = cw.toByteArray();
    }
    
    private static void print(){
        ClassWriter cw = new ClassWriter(0);
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        TraceClassVisitor cv = new TraceClassVisitor(cw, printWriter);
        cv.visit(V1_5, ACC_PUBLIC, "third/rocketDoubleWrite/ProducerDouble", null, "java/lang/Object", new String[] {"third/rocketDoubleWrite/IProducer"});
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd();
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I", null, new Integer(0)).visitEnd();
        cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I", null, new Integer(1)).visitEnd();
    
        cv.visitMethod(ACC_PUBLIC, "run", "(Ljava/lang/Object;)I", null, null).visitEnd();
        cv.visitEnd();
        System.out.println(sw.toString());
    }
}

