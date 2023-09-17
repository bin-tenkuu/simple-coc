package com.github.bin.controller;

import com.github.bin.config.UserLoginFilter;
import com.github.bin.entity.master.SysUser;
import com.github.bin.mapper.master.SysUserMapper;
import com.github.bin.model.login.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/16
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public boolean login(@RequestBody SysUser user) {
        val sysUser = sysUserMapper.findByUsername(user.getUsername());
        if (sysUser == null) {
            return false;
        }
        if (passwordEncoder.matches(user.getPassword(), sysUser.getPassword())) {
            UserLoginFilter.getUser().ifPresent(loginUser -> {
                loginUser.setSysUser(sysUser);
                UserLoginFilter.refreshUser(loginUser);
            });
            return true;
        }
        return false;
    }

    @GetMapping("/logout")
    public boolean logout() {
        UserLoginFilter.getUser().ifPresent(loginUser -> {
            loginUser.setSysUser(null);
            UserLoginFilter.refreshUser(loginUser);
        });
        return true;
    }

    @GetMapping("/user")
    public String user() {
        return UserLoginFilter.getUser().map(LoginUser::getNickname).orElse(null);
    }
}
