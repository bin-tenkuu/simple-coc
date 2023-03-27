package com.github.bin.config

import com.github.bin.util.jsonGlobal
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

/**
 * @author bin
 * @since 2023/03/27
 */
class JsonSqlType<T : Any>(
    private val kSerializer: KSerializer<T>,
) : SqlType<T>(Types.VARCHAR, "json") {
    override fun doGetResult(rs: ResultSet, index: Int): T? {
        val json = rs.getString(index)
        return if (json.isNullOrBlank()) {
            null
        } else {
            jsonGlobal.decodeFromString(kSerializer, json)
        }
    }

    override fun doSetParameter(ps: PreparedStatement, index: Int, parameter: T) {
        ps.setString(index, jsonGlobal.encodeToString(kSerializer, parameter))
    }
}

inline fun <reified T : Any> BaseTable<*>.json(name: String): Column<T> =
    registerColumn(name, JsonSqlType(serializer()))
