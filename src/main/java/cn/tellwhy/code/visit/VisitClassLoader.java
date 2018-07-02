package cn.tellwhy.code.visit;

import jdk.internal.org.objectweb.asm.ClassWriter;

/**
 * Created by aaa on 18-1-4.
 */
public class VisitClassLoader extends ClassLoader {
    public VisitClassLoader(){
        super();
    }
    public VisitClassLoader(ClassLoader base) {
        super(base);
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
    
    /*
    * public
    * 字节数组可以存储在一个 .class 文件中,供以后使用。或者,也可以用 ClassLoader 动态加载它
    * */
    public Class<?> defineClass(String name, byte[] b){
        return super.defineClass(name, b, 0, b.length);
    }
    
    @Override
    protected Class findClass(String name)
            throws ClassNotFoundException {
        if (name.endsWith("_Stub")) {
            ClassWriter cw = new ClassWriter(0);
            
            /*
            在运行过程中生成所请求的类
            */
            
            byte[] b = cw.toByteArray();
            return defineClass(name, b, 0, b.length);
        }
        return super.findClass(name);
    }
}
