package com.inuker.bluetooth.library.utils.hook.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by dingjikerbo on 2016/8/27.
 */
public class HookUtils {

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        return MethodUtils.getAccessibleMethod(clazz, name, parameterTypes);
    }

    public static Field getField(Class<?> clazz, String name) {
        if (clazz != null) {
            return FieldUtils.getDeclaredField(clazz, name, true);
        }
        return null;
    }

    public static <T> T getValue(Field field) {
        return getValue(field, null);
    }

    public static <T> T getValue(Field field, Object object) {
        try {
            if (field != null) {
                return (T) field.get(object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T invoke(Method method, Object object, Object... parameters) {
        try {
            return (T) method.invoke(object, parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
