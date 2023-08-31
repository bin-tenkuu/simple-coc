package com.github.bin.util;

/**
 * @author bin
 * @since 2023/08/25
 */
public interface NumberUtil {
    static int toIntOr(String s, int or) {
        if (s == null || s.isEmpty()) {
            return or;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return or;
        }
    }
}
