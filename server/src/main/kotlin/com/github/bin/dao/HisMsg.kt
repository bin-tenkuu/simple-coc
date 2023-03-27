package com.github.bin.dao

import org.ktorm.entity.Entity
import org.ktorm.schema.*

/**
 *  @Date:2023/3/13
 *  @author bin
 *  @version 1.0.0
 */
interface HisMsg : Entity<HisMsg> {
    val id: Long
    val type: String
    val msg: String
    val role: String
}

object THisMsg : Table<HisMsg>(tableName = "HisMsg", entityClass = HisMsg::class) {
    val id = long("id").bindTo { it.id }.primaryKey()
    val type = varchar("type").bindTo { it.type }
    val msg = varchar("msg").bindTo { it.msg }
    val role = varchar("role").bindTo { it.role }
}
