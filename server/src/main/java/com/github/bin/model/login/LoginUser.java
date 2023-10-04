package com.github.bin.model.login;

import com.github.bin.entity.master.SysUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/16
 */
@Getter
@Setter
@ToString(includeFieldNames = false)
public class LoginUser {
    public static final Long DEFAULT_ID = 0L;
    @ToString.Include
    private String token;
    private String remoteAddr;
    private String remoteHost;
    private String userAgent;

    @ToString.Include
    private Long id = DEFAULT_ID;
    @ToString.Include
    private String username;
    private String nickname;

    public void setSysUser(SysUser user) {
        if (user == null) {
            id = null;
            username = null;
            nickname = null;
        } else {
            id = user.getId();
            username = user.getUsername();
            nickname = user.getNickname();
        }
    }

    private static final ThreadLocal<LoginUser> USER = new ThreadLocal<>();

    @Contract(pure = true)
    public static Optional<LoginUser> getUser() {
        return Optional.ofNullable(USER.get());
    }

    @Contract(pure = true)
    public static boolean isLogin() {
        return USER.get() != null;
    }

    public static void setUser(LoginUser user) {
        USER.set(user);
    }

    public static void remove() {
        USER.remove();
    }

    @NotNull
    @Contract(pure = true)
    public static Long getUserId() {
        return Optional.ofNullable(USER.get())
                .map(LoginUser::getId)
                .orElse(DEFAULT_ID);
    }

}
