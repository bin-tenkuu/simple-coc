package com.github.bin.mapper.msg;

import com.github.bin.entity.msg.HisMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
public interface HisMsgMapper {

    HisMsg insert(@Param("type") String type, @Param("msg") String msg, @Param("role") int role);

    HisMsg update(@Param("id") int id, @Param("msg") String msg, @Param("role") int role);

    List<HisMsg> historyMsg(@Param("id") Integer id, @Param("limit") int limit);

    List<HisMsg> listAll(@Param("offset") long offset, @Param("size") long size);

    HisMsg getById(@Param("id") int id);

    Long count();
}
