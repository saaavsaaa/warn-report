package cn.tellwhy.code.visit;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.util.CheckClassAdapter;
import cn.tellwhy.report.btrace.read.ReaderCode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * Created by aaa on 18-1-30.
 */
public class VisitUtil {
    
    public static void main(String[] args){
        System.out.println(getRightOpcode(Type.LONG_TYPE, Opcodes.IADD));
    }
    
    /*
    * 在不确定类型是否匹配操作码时，获取当前操作码对应当前类型的正确的操作码
    */
    public static String getRightOpcode(Type t, int opcode){
        return ReaderCode.Instance.get(t.getOpcode(opcode));
    }
    
    public static void display(byte[] data){
        ClassReader reader = new ClassReader(data);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        CheckClassAdapter.verify(reader, true, pw);
        System.out.println(sw.toString());
    }
    
    public static void getInInstance(byte[] data) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        VisitClassLoader visitClassLoader = new VisitClassLoader();
        Class<?> c = visitClassLoader.defineClass("code.record.WaitClearCode", data);
        Object instance = c.newInstance();
        Field ttt = c.getDeclaredField("ttt");
        System.out.println(ttt.get(instance));
    }
}
