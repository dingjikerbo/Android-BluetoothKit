package com.inuker.bluetooth.library.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwentian on 2016/4/14.
 */
public class ListUtils {

    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() <= 0;
    }

    public static <E> List<E> getEmptyList() {
        return new ArrayList<E>();
    }
}
