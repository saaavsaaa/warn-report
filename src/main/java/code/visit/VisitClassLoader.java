package code.visit;

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
    * */
    public Class<?> defineClass(String name, byte[] b){
        return super.defineClass(name, b, 0, b.length);
    }
}
