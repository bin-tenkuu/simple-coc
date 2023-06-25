package com.github.bin.config.handler

import com.fasterxml.jackson.core.type.TypeReference
import com.github.bin.util.JsonUtil.toBean
import com.github.bin.util.JsonUtil.toJson
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.TypeHandler
import java.sql.*

/**
 * @author bin
 * @since 2023/02/10
 */
abstract class BaseJsonTypeHandler<T : Any> : TypeReference<T>(), TypeHandler<T?> {
    @Throws(SQLException::class)
    override fun setParameter(ps: PreparedStatement, i: Int, parameter: T?, jdbcType: JdbcType?) {
        ps.setString(i, parameter?.toJson())
    }

    @Throws(SQLException::class)
    override fun getResult(rs: ResultSet, columnName: String): T? {
        val json = rs.getString(columnName)
        return json?.toBean(this)
    }

    @Throws(SQLException::class)
    override fun getResult(rs: ResultSet, columnIndex: Int): T? {
        val json = rs.getString(columnIndex)
        return json?.toBean(this)
    }

    @Throws(SQLException::class)
    override fun getResult(cs: CallableStatement, columnIndex: Int): T? {
        val json = cs.getString(columnIndex)
        return json?.toBean(this)
    }
}
