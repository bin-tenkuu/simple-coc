package com.github.bin.mapper

import com.github.bin.entity.msg.HisMsg
import org.apache.ibatis.annotations.Param

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
interface HisMsgMapper {
    fun initTable()
    fun dropTable()

    fun insert(@Param("type") type: String, @Param("msg") msg: String, @Param("role") role: Long): Long
    fun update(@Param("id") id: Long, @Param("msg") msg: String, @Param("role") role: Long): Int
    fun historyMsg(@Param("id") id: Long?, @Param("limit") limit: Int): List<HisMsg>
    fun listAll(@Param("offset") offset: Long, @Param("size") size: Long): List<HisMsg>
    fun count(): Long

}
