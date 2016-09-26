package com.inuker.bluetooth.library.utils.hook.utils;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * Created by dingjikerbo on 2016/9/26.
 */
public class MemberUtils {
    static boolean isAccessible(final Member m) {
        return m != null && Modifier.isPublic(m.getModifiers()) && !m.isSynthetic();
    }
}
