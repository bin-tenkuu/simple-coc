package com.github.bin.config.handler

import com.baomidou.mybatisplus.core.toolkit.Assert
import com.baomidou.mybatisplus.core.toolkit.StringUtils
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.ibatis.type.JdbcType
import org.apache.ibatis.type.TypeHandler
import org.postgresql.util.PGobject
import java.io.IOException
import java.sql.*

/**
 * @author bin
 * @since 2023/02/10
 */
abstract class BaseJsonTypeHandler<T> : TypeReference<T>(), TypeHandler<T?> {
    @Throws(SQLException::class)
    override fun setParameter(ps: PreparedStatement, i: Int, parameter: T?, jdbcType: JdbcType?) {
        ps.setObject(i, PGobject().apply {
            type = "json"
            value = toJson(parameter)
        }, Types.OTHER)
    }

    @Throws(SQLException::class)
    override fun getResult(rs: ResultSet, columnName: String): T? {
        val json = rs.getString(columnName)
        return fromJson(json)
    }

    @Throws(SQLException::class)
    override fun getResult(rs: ResultSet, columnIndex: Int): T? {
        val json = rs.getString(columnIndex)
        return fromJson(json)
    }

    @Throws(SQLException::class)
    override fun getResult(cs: CallableStatement, columnIndex: Int): T? {
        val json = cs.getString(columnIndex)
        return fromJson(json)
    }

    private fun fromJson(json: String?): T? {
        return if (json.isNullOrBlank()) null
        else try {
            objectMapper!!.readValue(json, this)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun toJson(obj: T?): String {
        return try {
            objectMapper!!.writeValueAsString(obj)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    companion object {
        var objectMapper: ObjectMapper? = null
            get() {
                if (null == field) {
                    field = ObjectMapper()
                }
                return field
            }
            set(objectMapper) {
                Assert.notNull(objectMapper, "ObjectMapper should not be null")
                field = objectMapper
            }
    }
}
