package com.inuker.bluetooth.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
	public static boolean isEmpty(List<?> list) {
		return list == null || list.size() <= 0;
	}

	public static <E> List<E> getEmptyList() {
		return new ArrayList<E>();
	}
}
