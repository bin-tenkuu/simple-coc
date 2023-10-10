package com.github.bin.controller;

import com.github.bin.entity.master.SysUser;
import com.github.bin.model.ResultModel;
import com.github.bin.model.login.ChangePassword;
import com.github.bin.model.login.LoginModel;
import com.github.bin.model.login.LoginUser;
import com.github.bin.model.login.RegisterUser;
import com.github.bin.service.SysUserService;
import com.github.bin.util.IdWorker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/16
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class LoginController {
    private final SysUserService sysUserService;
    private final PasswordEncoder passwordEncoder;

    private static final IdWorker ID_WORKER = new IdWorker(0L);

    @PostMapping("/login")
    public ResultModel<String> login(@RequestBody LoginModel user) {
        val sysUser = sysUserService.findByUsername(user.getUsername());
        if (sysUser == null) {
            return ResultModel.fail("用户不存在");
        }
        if (passwordEncoder.matches(user.getPassword(), sysUser.getPassword())) {
            val token = String.valueOf(ID_WORKER.nextId());
            val loginUser = new LoginUser(token, sysUser);
            LoginUser.refreshUser(loginUser);
            return ResultModel.success(token);
        }
        return ResultModel.fail("用户名或密码错误");
    }

    @GetMapping("/logout")
    public ResultModel<?> logout() {
        LoginUser.removeByToken(LoginUser.getUser());
        return ResultModel.success();
    }

    @GetMapping("/userInfo")
    public ResultModel<LoginUser> userInfo() {
        val user = LoginUser.getUser();
        return ResultModel.success(user != null ? user : new LoginUser());
    }

    @GetMapping("/info")
    public ResultModel<SysUser> info() {
        val sysUser = sysUserService.getById(LoginUser.getUserId());
        return ResultModel.success(sysUser);
    }

    @PostMapping("/changePassword")
    public ResultModel<?> changePassword(@RequestBody @Valid @NotNull ChangePassword changePassword) {
        val sysUser = sysUserService.getById(LoginUser.getUserId());
        if (sysUser == null) {
            return ResultModel.fail("未登录");
        }
        if (!passwordEncoder.matches(changePassword.getOldPassword(), sysUser.getPassword())) {
            return ResultModel.fail("原密码错误");
        }
        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            return ResultModel.fail("两次密码不一致");
        }
        sysUser.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        sysUserService.updatePassword(sysUser);
        return ResultModel.success();
    }

    @PostMapping("/register")
    public ResultModel<?> register(@RequestBody @Valid @NotNull RegisterUser registerUser) {
        var sysUser = sysUserService.findByUsername(registerUser.getUsername());
        if (sysUser != null) {
            return ResultModel.fail("用户名已存在");
        }
        if (!registerUser.getPassword().equals(registerUser.getConfirmPassword())) {
            return ResultModel.fail("两次密码不一致");
        }
        sysUser = new SysUser();
        sysUser.setUsername(registerUser.getUsername());
        sysUser.setPassword(passwordEncoder.encode(registerUser.getPassword()));
        val now = LocalDateTime.now();
        sysUser.setNickname("user_" + now);
        sysUserService.save(sysUser);
        return ResultModel.success();
    }
}
