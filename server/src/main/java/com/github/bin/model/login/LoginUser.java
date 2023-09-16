package com.github.bin.model.login;

import com.github.bin.entity.master.SysUser;
import lombok.Getter;
import lombok.Setter;

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/9/16
 */
@Getter
@Setter
public class LoginUser {
    private String token;
    private String remoteAddr;
    private String remoteHost;
    private String userAgent;

    private Long id;
    private String username;
    private String nickname;

    public void setSysUser(SysUser user) {
        id = user.getId();
        username = user.getUsername();
        nickname = user.getNickname();
    }
}
