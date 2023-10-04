package com.github.bin.model.login;

import lombok.Getter;
import lombok.val;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/10/4
 */
@Getter
public class InviteCode {
    private static final int RADIX = 36;
    private final long time;
    private final long userId;

    public InviteCode(long userId) {
        this.time = System.currentTimeMillis() / 1000;
        this.userId = userId;
    }

    private InviteCode(long time, long userId) {
        this.time = time;
        this.userId = userId;
    }

    public static InviteCode parse(String code) {
        if (code.length() < 8) {
            return null;
        }
        val timeLen = code.charAt(code.length() - 1) - '0';
        try {
            val time = Long.parseLong(code, 0, timeLen, RADIX);
            val userId = Long.parseLong(code, timeLen, code.length() - 1, RADIX);
            return new InviteCode(time, userId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        val timeStr = Long.toString(time, RADIX);
        // 千年以内不会超过 6
        val timeLen = (char) (timeStr.length() + '0');
        return timeStr + Long.toString(userId, RADIX) + timeLen;
    }

}
