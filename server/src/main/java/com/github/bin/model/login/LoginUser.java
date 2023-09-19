package com.github.bin.model.login;

import com.github.bin.entity.master.SysUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    @ToString.Include
    private String token;
    private String remoteAddr;
    private String remoteHost;
    private String userAgent;

    @ToString.Include
    private Long id;
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

    public static Optional<LoginUser> getUser() {
        return Optional.ofNullable(USER.get());
    }

    public static void setUser(LoginUser user) {
        USER.set(user);
    }

    public static void remove() {
        USER.remove();
    }

    public static Long getUserId() {
        return Optional.ofNullable(USER.get())
                .map(LoginUser::getId)
                .orElse(null);
    }

}
