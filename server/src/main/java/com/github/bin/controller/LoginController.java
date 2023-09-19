package com.github.bin.controller;

import com.github.bin.config.UserLoginFilter;
import com.github.bin.entity.master.SysUser;
import com.github.bin.mapper.master.SysUserMapper;
import com.github.bin.model.ResultModel;
import com.github.bin.model.login.LoginUser;
import com.github.bin.util.IdWorker;
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
    public ResultModel<?> login(@RequestBody SysUser user) {
        val sysUser = sysUserMapper.findByUsername(user.getUsername());
        if (sysUser == null) {
            return ResultModel.fail("用户不存在");
        }
        if (passwordEncoder.matches(user.getPassword(), sysUser.getPassword())) {
            LoginUser.getUser().ifPresent(loginUser -> {
                loginUser.setSysUser(sysUser);
                UserLoginFilter.refreshUser(loginUser);
            });
            return ResultModel.success();
        }
        return ResultModel.fail("用户名或密码错误");
    }

    @GetMapping("/logout")
    public ResultModel<?> logout() {
        LoginUser.getUser().ifPresent(loginUser -> {
            loginUser.setSysUser(null);
            UserLoginFilter.refreshUser(loginUser);
        });
        return ResultModel.success();
    }

    @GetMapping("/userInfo")
    public ResultModel<String> userInfo() {
        return LoginUser.getUser()
                .map(LoginUser::getNickname)
                .map(ResultModel::success)
                .orElse(ResultModel.fail(2, "未登录"));
    }

    private static final IdWorker ID_WORKER = new IdWorker(0L);

    @GetMapping("/id")
    public ResultModel<Long> id() {
        return ResultModel.success(ID_WORKER.nextId());
    }
}
