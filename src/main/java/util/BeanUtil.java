package util;

import java.lang.reflect.Field;

/**
 * Created by aaa on 17-6-26.
 */
public class BeanUtil {
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
