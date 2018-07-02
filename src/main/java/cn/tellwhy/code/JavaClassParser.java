package cn.tellwhy.code;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import cn.tellwhy.util.ResourceUtil;

import java.io.IOException;

/**
 * Created by aaa on 18-1-30.
 */
public class JavaClassParser {
    
    public static void main(String[] args) throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        ClassParser classParser = new ClassParser(ResourceUtil.getInput("/home/aaa/Github/warn-report/target/classes/code/record/WaitClearCode.class"), "WaitClearCode");
        JavaClass javaClass = classParser.parse();
        System.out.println(javaClass.toString());
    }
}
