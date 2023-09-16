package com.github.bin.entity.master;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bin
 * @since 2023/09/11
 */
@Getter
@Setter
@NoArgsConstructor
@TableName(value = "sys_user", resultMap = "BaseResultMap")
public class SysUser implements UserDetails {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("user_name")
    @NotBlank
    private String username;
    @TableField("nick_name")
    private String nickname;
    @TableField("password")
    @NotBlank
    private String password;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("is_enable")
    private Boolean isEnable;

    @TableField(exist = false)
    private List<GrantedAuthority> authorities = new ArrayList<>();

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return isEnable;
    }

}
