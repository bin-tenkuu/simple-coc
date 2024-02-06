package com.github.bin.service;

import com.github.bin.entity.master.SysUser;
import com.github.bin.model.login.InviteCode;
import com.github.bin.model.login.LoginUser;
import com.github.bin.repository.master.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/29
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SysUserService {
    private final SysUserRepository sysUserRepository;
    private static final String INVITE_CODE = "inviteCode";

    public SysUser findByUsername(String username) {
        return sysUserRepository.findByUsername(username);
    }

    public void updatePassword(SysUser user) {
        sysUserRepository.updatePasswordById(user.getId(), user.getPassword());
    }

    public String generateInviteCode() {
        long userId = LoginUser.getUserId();
        val inviteCode = new InviteCode(userId);
        val code = inviteCode.toString();
        RedisService.setHash(INVITE_CODE, Long.toString(userId), code);
        return code;
    }

    public boolean checkInviteCodeAndRemove(String inviteCode) {
        val code = InviteCode.parse(inviteCode);
        if (code == null) {
            log.warn("邀请码格式错误: '{}'", inviteCode);
            return false;
        }
        val storeCode = RedisService.getHash(INVITE_CODE, Long.toString(code.getUserId()));
        if (inviteCode.equals(storeCode)) {
            RedisService.removeHash(INVITE_CODE, Long.toString(code.getUserId()));
            return true;
        }
        return false;
    }

    public void save(SysUser entity) {
        sysUserRepository.save(entity);
    }

    public SysUser getById(Long id) {
        return sysUserRepository.findById(id).orElse(null);
    }
}
