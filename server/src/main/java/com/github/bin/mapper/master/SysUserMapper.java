package com.github.bin.mapper.master;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bin.entity.master.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author bin
 * @since 2023/09/12
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    SysUser findByUsername(String username);
}
