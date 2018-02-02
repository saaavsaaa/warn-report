package util.type;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
}
