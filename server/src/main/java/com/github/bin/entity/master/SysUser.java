package com.github.bin.entity.master;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * @author bin
 * @since 2023/09/11
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sys_user")
public class SysUser {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "username", nullable = false, unique = true, length = 32)
    @NotBlank
    private String username;
    @Column(name = "nickname", length = 32)
    private String nickname;
    @Column(name = "password", nullable = false, length = 128)
    @NotBlank
    private String password;
    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    @LastModifiedDate
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    @Column(name = "is_enable", nullable = false)
    private Boolean isEnable;
}
