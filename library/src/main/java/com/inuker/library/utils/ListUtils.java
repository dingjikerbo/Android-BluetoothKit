package com.inuker.library.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwentian on 2016/5/13.
 */
public class ListUtils {

    public ListUtils() {
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() <= 0;
    }

    public static <E> List<E> getEmptyList() {
        return new ArrayList();
    }
}
