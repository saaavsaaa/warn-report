package object;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by aaa on 17-12-8.
 */
public class StringTest {
    
    @Test
    public void testBytesCatch(){
        int count = 0;
        byte [] data = {1, 2, 3, 4, 'a', 'b', 'c', 'd', 'e'};
        boolean result = contrastBytes(data);
        if (result){
            count++;
        }
        System.out.println(count);
    }
    
    private boolean contrastBytes(byte [] data){
        //        byte [] data = {1, 2, 3, 4, 97, 98, 99, 100};
        byte [] newData;
        newData = Arrays.copyOfRange(data, 0, 8);

//        for(byte i:newData) {
//            System.out.print(i + " ");
//        }
        if (1 == newData[0] && 2 == newData[1] && 3 == newData[2] && 4 == newData[3]
                && 97 == newData[4] && 98 == newData[5] && 99 == newData[6] && 100 == newData[7]){
            return true;
        } else {
            return false;
        }
    }
    
    @Test
    public void testChar(){
        char c = 99;
        System.out.println("c" + 1 + c);
        System.out.println(c + 1 + "c");
        System.out.println(c + (1 + "c"));
        System.out.println('c');
        System.out.println('c' + 1);
        System.out.println((char) 1);
        System.out.println('c' + (char) 1);
    }
    
    @Test
    public void testStringLength(){
        String str = "123456";
        change(str);
        System.out.println(str);
        str = change(str);
        System.out.println(str);
    }
    
    private String change(String str){
        Class clazz = String.class;
        try {
            Field valueField = clazz.getDeclaredField("value");
            valueField.setAccessible(true);
            valueField.set(str, new char[]{'1','2','3','4','5','6','7'});
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        str = "1234";
        return str;
    }
    
    /*private void change(Method method){
        try {
            Class<?> clazz = method.getDeclaringClass();
            ClassPool pool = ClassPool.getDefault();
            CtClass clz = pool.get(clazz.getName());
            CtClass[] params = new CtClass[method.getParameterTypes().length];
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                params[i] = pool.getCtClass(method.getParameterTypes()[i].getName());
            }
            CtMethod ctMethod = clz.getDeclaredMethod(method.getName(), params);
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            ConstPool constPool = methodInfo.getConstPool();
            AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
//            AnnotationsAttribute  annotationsAttribute = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
            Annotation annotation = annotationsAttribute.getAnnotation(Transactional.class.getName());
            if (annotation == null){
                Annotation methodAnnot = new Annotation(Transactional.class.getTypeName(), constPool);
                annotationsAttribute.setAnnotation(methodAnnot);
//                methodInfo.addAttribute(annotationsAttribute);
                ctMethod.getMethodInfo().addAttribute(annotationsAttribute);
            }
            clz.addMethod(ctMethod);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }*/
    
    @Test
    public void testString() throws UnsupportedEncodingException {
        byte[] bytes = new byte[] { 50, 0, -1, 11, -12 };
        String sendString = new String(bytes , "ISO-8859-1" );
        byte[] sendBytes = sendString.getBytes(  "ISO-8859-1" );
        System.out.println(Arrays.toString(sendBytes));
    }
    
    @Test
    public void testOptional(){
        String aaa = "5";
        Optional<String> property = Optional.ofNullable(aaa);
        int i = property.map(p -> Integer.valueOf(p)).orElse(3);
        System.out.println(i);
    }
    
    @Test
    public void testPattern(){
        BigDecimal money = new BigDecimal(211592.9999911119111);
        DecimalFormat pattern = new DecimalFormat(",###.##");
        pattern.setMaximumFractionDigits(15);
        String result = pattern.format(money);
        System.out.print(result);
    }
}
