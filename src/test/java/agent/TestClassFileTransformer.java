package agent;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.*;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;
import jdk.nashorn.internal.runtime.Timing;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * Created by aaa on 17-12-19.
 * consult : http://blog.csdn.net/productshop/article/details/50623626
 */

/*
consult : http://blog.csdn.net/zheng12tian/article/details/40617345
参数：
loader - 定义要转换的类加载器；如果是引导加载器，则为 null
className - 完全限定类内部形式的类名称和 The Java Virtual Machine Specification 中定义的接口名称。例如，"java/util/List"。
classBeingRedefined - 如果是被重定义或重转换触发，则为重定义或重转换的类；如果是类加载，则为 null
protectionDomain - 要定义或重定义的类的保护域
classfileBuffer - 类文件格式的输入字节缓冲区（不得修改）
返回：
一个格式良好的类文件缓冲区（转换的结果），如果未执行转换,则返回 null。
抛出：
IllegalClassFormatException - 如果输入不表示一个格式良好的类文件
另请参见：
Instrumentation.redefineClasses(java.lang.instrument.ClassDefinition...)

https://www.ibm.com/developerworks/cn/java/j-lo-jse61/index.html
* */
public class TestClassFileTransformer implements ClassFileTransformer {
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.equals("TransClass")) {
            return null;
        }
//        return getBytesFromFile(classNumberReturns2);
    
    
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        for (Object obj : cn.methods) {
            MethodNode md = (MethodNode) obj;
            if ("<init>".endsWith(md.name) || "<clinit>".equals(md.name)) {
                continue;
            }
            InsnList insns = md.instructions;
            InsnList il = new InsnList();
            il.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System",
                    "out", "Ljava/io/PrintStream;"));
            il.add(new LdcInsnNode("Enter method-> " + cn.name+"."+md.name));
            il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", true));
            insns.insert(il);
            md.maxStack += 3;
        
        }
        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
    
    private String methodName;
    

    private byte[] get(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
        try {
            ClassParser cp = new ClassParser(new java.io.ByteArrayInputStream(
                    classfileBuffer), className + ".java");
            JavaClass jclas = cp.parse();
            ClassGen cgen = new ClassGen(jclas);
            Method[] methods = jclas.getMethods();
            int index;
            for (index = 0; index < methods.length; index++) {
                if (methods[index].getName().equals(methodName)) {
                    break;
                }
            }
            if (index < methods.length) {
                addTimer(cgen, methods[index]);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                cgen.getJavaClass().dump(bos);
                return bos.toByteArray();
            }
            System.err.println("Method " + methodName + " not found in "
                    + className);
            System.exit(0);
        
        } catch (IOException e) {
            System.err.println(e);
            System.exit(0);
        }
        return null; // No transformation required
    }
    
    
    public static final String classNumberReturns2 = "TransClass.class.2";
    
    public static byte[] getBytesFromFile(String fileName) {
        try {
            // precondition
            File file = new File(fileName);
            InputStream is = new FileInputStream(file);
            long length = file.length();
            byte[] bytes = new byte[(int) length];
            
            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset <bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            is.close();
            return bytes;
        } catch (Exception e) {
            System.out.println("error occurs in _ClassTransformer!"
                    + e.getClass().getName());
            return null;
        }
    }
    
    
    
    private static void addTimer(ClassGen cgen, Method method) {
        
        // set up the construction tools
        InstructionFactory ifact = new InstructionFactory(cgen);
        InstructionList ilist = new InstructionList();
        ConstantPoolGen pgen = cgen.getConstantPool();
        String cname = cgen.getClassName();
        MethodGen wrapgen = new MethodGen(method, cname, pgen);
        wrapgen.setInstructionList(ilist);
        
        // rename a copy of the original method
        MethodGen methgen = new MethodGen(method, cname, pgen);
        cgen.removeMethod(method);
        String iname = methgen.getName() + "_timing";
        methgen.setName(iname);
        cgen.addMethod(methgen.getMethod());
        Type result = methgen.getReturnType();
        
        // compute the size of the calling parameters
        Type[] parameters = methgen.getArgumentTypes();
        int stackIndex = methgen.isStatic() ? 0 : 1;
        for (int i = 0; i < parameters.length; i++) {
            stackIndex += parameters[i].getSize();
        }
        
        // save time prior to invocation
        ilist.append(ifact.createInvoke("java.lang.System",
                "currentTimeMillis", Type.LONG, Type.NO_ARGS,
                Constants.INVOKESTATIC));
        ilist.append(InstructionFactory.
                createStore(Type.LONG, stackIndex));
        
        // call the wrapped method
        int offset = 0;
        short invoke = Constants.INVOKESTATIC;
        if (!methgen.isStatic()) {
            ilist.append(InstructionFactory.
                    createLoad(Type.OBJECT, 0));
            offset = 1;
            invoke = Constants.INVOKEVIRTUAL;
        }
        for (int i = 0; i < parameters.length; i++) {
            Type type = parameters[i];
            ilist.append(InstructionFactory.
                    createLoad(type, offset));
            offset += type.getSize();
        }
        ilist.append(ifact.createInvoke(cname,
                iname, result, parameters, invoke));
        
        // store result for return later
        if (result != Type.VOID) {
            ilist.append(InstructionFactory.
                    createStore(result, stackIndex+2));
        }
        
        // print time required for method call
        ilist.append(ifact.createFieldAccess("java.lang.System",
                "out",  new ObjectType("java.io.PrintStream"),
                Constants.GETSTATIC));
        ilist.append(InstructionConstants.DUP);
        ilist.append(InstructionConstants.DUP);
        String text = "Call to method " + methgen.getName() +
                " took ";
        ilist.append(new PUSH(pgen, text));
        ilist.append(ifact.createInvoke("java.io.PrintStream",
                "print", Type.VOID, new Type[] { Type.STRING },
                Constants.INVOKEVIRTUAL));
        ilist.append(ifact.createInvoke("java.lang.System",
                "currentTimeMillis", Type.LONG, Type.NO_ARGS,
                Constants.INVOKESTATIC));
        ilist.append(InstructionFactory.
                createLoad(Type.LONG, stackIndex));
        ilist.append(InstructionConstants.LSUB);
        ilist.append(ifact.createInvoke("java.io.PrintStream",
                "print", Type.VOID, new Type[] { Type.LONG },
                Constants.INVOKEVIRTUAL));
        ilist.append(new PUSH(pgen, " ms."));
        ilist.append(ifact.createInvoke("java.io.PrintStream",
                "println", Type.VOID, new Type[] { Type.STRING },
                Constants.INVOKEVIRTUAL));
        
        // return result from wrapped method call
        if (result != Type.VOID) {
            ilist.append(InstructionFactory.
                    createLoad(result, stackIndex+2));
        }
        ilist.append(InstructionFactory.createReturn(result));
        
        // finalize the constructed method
        wrapgen.stripAttributes(true);
        wrapgen.setMaxStack();
        wrapgen.setMaxLocals();
        cgen.addMethod(wrapgen.getMethod());
        ilist.dispose();
    }
    
    public static void premain(String options, Instrumentation ins) {
        if (options != null) {
//            ins.addTransformer(new Timing(options));
        } else {
            System.out
                    .println("Usage: java -javaagent:Timing.jar=\"class:method\"");
            System.exit(0);
        }
        
    }
}
