package com.github.bin.model.login;

import com.github.bin.entity.master.SysUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class LoginUser {
    public static final Long DEFAULT_ID = 0L;
    @ToString.Include
    private String token;

    @ToString.Include
    private Long id;
    @ToString.Include
    private String username;
    private String nickname;

    public LoginUser(String token, SysUser user) {
        this.token = token;
        setSysUser(user);
    }

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
