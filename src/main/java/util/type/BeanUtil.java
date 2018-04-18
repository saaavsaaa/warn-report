package util.type;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by aaa on 17-6-26.
 */
public class BeanUtil {
    public static Map<Integer, String> get(Object instance) {
        Class clazz = instance.getClass();
        try {
            Field[] valueFields = clazz.getDeclaredFields();
            Map<Integer, String> pairs = new HashMap();
            for (Field one : valueFields) {
                one.setAccessible(true);
                Integer value;
                try {
                    value = one.getInt(instance);
                }catch (Exception ee){
                    value = Integer.valueOf(String.valueOf(one.get(instance)));
                }
                
                String key = one.getName();
                pairs.put(value, key);
            }
            return pairs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Object get(Object instance, String key) {
        Class clazz = instance.getClass();
        try {
            Field valueField = clazz.getDeclaredField(key);
            valueField.setAccessible(true);
            return valueField.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object set(Object instance, String key, Object value) {
        Class clazz = instance.getClass();
        try {
            Field valueField = clazz.getDeclaredField(key);
            valueField.setAccessible(true);
            valueField.set(instance, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }
    
    public static <T> T toBean(T t, Class<?> type){
        Class<?> clazz = t.getClass();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for(PropertyDescriptor property:props){
                String field = property.getName();
                Method method = property.getWriteMethod();
                method.invoke(t, getStaticValue(field, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
    
    public static Object getStaticValue(String fieldName, Class<?> clazz) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            String name = field.getName();
            if(fieldName.equals(name)){
                return field.get(clazz);
            }
        }
        return null;
    }
    
    
    public static void main(String[] args) throws Exception{
        load(Thread.currentThread().getContextClassLoader());
    }
    
    public static void load(final ClassLoader classLoader) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, InterruptedException {
        final CountDownLatch connected = new CountDownLatch(1);
        Class<?> watchCls = classLoader.loadClass("org.apache.zookeeper.Watcher");
        Object watcher = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ watchCls } , new ProxyListener(connected));
        Class<?> zkCls = classLoader.loadClass("org.apache.zookeeper.ZooKeeper");
        Constructor constructor = zkCls.getConstructor(String.class, int.class, watchCls);
        Object instance = constructor.newInstance("192.168.2.44:2181", 200000, watcher);
        connected.await();
//        Thread.sleep(10000);
        Class<?> authCls = classLoader.loadClass("org.apache.zookeeper.data.Id");
        List authorities = buildACL(classLoader, authCls, buildId(authCls));
        
        Class<?> createModeCls = classLoader.loadClass("org.apache.zookeeper.CreateMode");
        Method method = zkCls.getMethod("create",String.class, byte[].class, List.class, createModeCls);//方法名和对应的参数类型
        Object o = method.invoke(instance, "/config", new byte[0], authorities, BeanUtil.getStaticValue("PERSISTENT", createModeCls));//调用得到的上边的方法method
        System.out.println(String.valueOf(o));
    }
    
    private static Object buildId(Class<?> cls) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = cls.getConstructor(String.class, String.class);
        return constructor.newInstance("world", "anyone");
    
    }
    
    private static List buildACL(final ClassLoader classLoader, Class<?> type, Object typeObject) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> cls = classLoader.loadClass("org.apache.zookeeper.data.ACL");
        Constructor constructor = cls.getConstructor(int.class, type);
//        return constructor.newInstance(31, typeObject);
        return new ArrayList(Collections.singletonList(constructor.newInstance(31, typeObject)));
    }
}
class ProxyListener implements java.lang.reflect.InvocationHandler {
    private final CountDownLatch CONNECTED;
    
    public ProxyListener(final CountDownLatch connected) {
        CONNECTED = connected;
    }
    
    public static void main(String[] args) {
        Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ InvocationHandler.class } , new ProxyListener(new CountDownLatch(1)));
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Object result = null;
        try {
            printArgs(method, args);
            // if the method name equals some method's name then call your method
            if (method.getName().equals("process")) {
                Object keeperState = BeanUtil.get(args[0], "KeeperState");
                if (keeperState.toString().equals("SyncConnected")){
                    Object eventType = BeanUtil.get(args[0], "eventType");
                    if (eventType.toString().equals("None")){
                        CONNECTED.countDown();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        } finally {
            System.out.println("end method " + method.getName());
        }
        return result;
    }
    
    private void printArgs(Method m, Object[] args){
        if (args == null){
            return;
        }
        // Prints the method being invoked
        System.out.print("begin method "+ m.getName() + "(");
        for(int i=0; i < args.length;i++){
            if( i > 0) System.out.print(",");
            System.out.print(" " +args[i].toString());
        }
        System.out.println(" )");
    }
}