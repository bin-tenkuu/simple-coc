package com.github.bin.controller;

import com.github.bin.config.UserLoginFilter;
import com.github.bin.entity.master.SysUser;
import com.github.bin.mapper.master.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/16
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping()
    public boolean login(@RequestBody SysUser user) {
        val sysUser = sysUserMapper.findByUsername(user.getUsername());
        if (sysUser == null) {
            return false;
        }
        if (passwordEncoder.matches(user.getPassword(), sysUser.getPassword())) {
            UserLoginFilter.getUser().setSysUser(sysUser);
            UserLoginFilter.refreshUser();
            return true;
        }
        return false;
    }
}
