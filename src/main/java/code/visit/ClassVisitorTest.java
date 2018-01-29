package code.visit;

import code.record.WaitClearCode;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;

import java.io.IOException;

import static jdk.internal.org.objectweb.asm.Opcodes.ASM4;
import static jdk.internal.org.objectweb.asm.Opcodes.V1_5;

/**
 * Created by aaa on 18-1-5.
 */
public class ClassVisitorTest {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
//        byte[] b1 = new byte[]{};
        String path = "code.record.WaitClearCode";
        VisitClassLoader classLoader = new VisitClassLoader(Thread.currentThread().getContextClassLoader());

        ClassReader cr = new ClassReader(path);
        ClassWriter cw = new ClassWriter(cr, 0);
        ChangeVersionAdapter ca = new ChangeVersionAdapter(cw);
        cr.accept(ca, 0);
        byte[] b2 = cw.toByteArray();
    
        VisitClassLoader visitClassLoader = new VisitClassLoader();
        Class c = visitClassLoader.defineClass("code.record.WaitClearCode", b2);
        System.out.println(c.getTypeName());
        
        System.out.println(b2);
    }
}


class ChangeVersionAdapter extends ClassVisitor {
    public ChangeVersionAdapter(ClassVisitor cv) {
        super(ASM4, cv);
    }
    @Override
    public void visit(int version, int access, String name,
                      String signature, String superName, String[] interfaces) {
        cv.visit(V1_5, access, name, signature, superName, interfaces);
    }
}