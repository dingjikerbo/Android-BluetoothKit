package com.inuker.bluetooth.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<T> getInterfaceImplClasses(Class<?> clazz,
			String interfaceName) {
		List<T> interfazes = new ArrayList<T>();

		Class<?>[] classes = clazz.getDeclaredClasses();

		if (classes == null) {
			return interfazes;
		}

		for (Class<?> rclazz : classes) {
			boolean flag = false;

			Class<?>[] interfaces = rclazz.getInterfaces();

			if (interfaces != null) {
				for (Class<?> intf : interfaces) {
					if (intf.getSimpleName().equals(interfaceName)) {
						flag = true;
						break;
					}
				}
			}

			if (flag) {
				try {
					Constructor constructor = rclazz.getDeclaredConstructor();
					constructor.setAccessible(true);
					interfazes.add((T) constructor.newInstance());
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return interfazes;
	}
}
