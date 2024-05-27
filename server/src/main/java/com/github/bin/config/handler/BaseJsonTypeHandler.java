package com.github.bin.config.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.bin.util.JsonUtil;
import lombok.val;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author bin
 * @since 2023/08/22
 */
public abstract class BaseJsonTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {
    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.toJson(parameter));
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        val json = rs.getString(columnName);
        return JsonUtil.toBean(json,this);
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        val json = rs.getString(columnIndex);
        return JsonUtil.toBean(json,this);
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        val json = cs.getString(columnIndex);
        return JsonUtil.toBean(json,this);
    }
}
