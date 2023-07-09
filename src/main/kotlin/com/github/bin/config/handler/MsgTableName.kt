package com.github.bin.config.handler

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
object MsgTableName : TableNameHandler {
    private val tableNames = ThreadLocal<String>()
    override fun dynamicTableName(sql: String, tableName: String): String {
        if (tableName == "his_msg") {
            return "his_msg_${tableNames.get()}"
        }
        return tableName
    }

    fun set(tableName: String) {
        tableNames.set(tableName)
    }

    fun remove() {
        tableNames.remove()
    }

    inline operator fun <T> invoke(tableName: String, block: () -> T): T {
        set(tableName)
        try {
            return block()
        } finally {
            remove()
        }
    }
}
