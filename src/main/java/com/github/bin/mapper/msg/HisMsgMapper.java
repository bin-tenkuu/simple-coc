package com.github.bin.mapper.msg;

import com.github.bin.entity.msg.HisMsg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
public interface HisMsgMapper {
    void initTable();

    void dropTable();

    long insert(@Param("type") String type, @Param("msg") String msg, @Param("role") long role);

    int update(@Param("id") long id, @Param("msg") String msg, @Param("role") long role);

    List<HisMsg> historyMsg(@Param("id") Long id, @Param("limit") int limit);

    List<HisMsg> listAll(@Param("offset") long offset, @Param("size") long size);

    Long count();
}
