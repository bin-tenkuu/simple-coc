package com.github.bin.repository.master;

import com.github.bin.entity.master.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SysUserRepository extends JpaRepository<SysUser, Long>, JpaSpecificationExecutor<SysUser> {
    SysUser findByUsername(String username);

    @Modifying
    @Query("update SysUser u set u.password = :password where u.id = :id")
    void updatePasswordById(Long id, String password);
}
