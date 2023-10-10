package com.github.bin.model.login;

import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.SysUser;
import com.github.bin.service.RedisService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
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

    private static final Duration TIMEOUT = Duration.ofDays(1);

    @Nullable
    public static LoginUser getByToken(String token) {
        return RedisService.getValue("token:" + token, LoginUser.class, TIMEOUT);
    }

    public static void removeByToken(LoginUser user) {
        if (user != null) {
            RedisService.remove("token:" + user.token);
        }
    }

    public static void refreshUser(final LoginUser user) {
        if (user != null) {
            RedisService.setValue("token:" + user.token, user, TIMEOUT);
        }
    }

    @Contract(pure = true)
    public static LoginUser getUser() {
        return USER.get();
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
                .orElse(Room.ALL_USER);
    }

}
