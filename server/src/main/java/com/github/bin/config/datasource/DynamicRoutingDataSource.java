/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bin.config.datasource;

import com.github.bin.exception.CannotFindDataSourceException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.NamedThreadLocal;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.StringUtils;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 核心动态数据源组件
 *
 * @author TaoYu Kanyuxia
 * @since 1.0.0
 */
@Slf4j
public final class DynamicRoutingDataSource extends AbstractDataSource {
    /**
     * 为什么要用链表存储(准确的是栈)
     * <pre>
     * 为了支持嵌套切换，如ABC三个service都是不同的数据源
     * 其中A的某个业务要调B的方法，B的方法需要调用C的方法。一级一级调用切换，形成了链。
     * 传统的只设置当前线程的方式不能满足此业务需求，必须使用栈，后进先出。
     * </pre>
     */
    private static final ThreadLocal<Deque<String>> LOOKUP_KEY_HOLDER = new NamedThreadLocal<>("dynamic-datasource") {
        @Override
        protected Deque<String> initialValue() {
            return new ArrayDeque<>();
        }
    };

    /**
     * 获得当前线程数据源
     *
     * @return 数据源名称
     */
    public static String peek() {
        return LOOKUP_KEY_HOLDER.get().peek();
    }

    /**
     * 设置当前线程数据源
     * <p>
     * 如非必要不要手动调用，调用后确保最终清除
     * </p>
     *
     * @param ds 数据源名称
     */
    public static void push(String ds) {
        String dataSourceStr = ds == null ? "" : ds;
        LOOKUP_KEY_HOLDER.get().push(dataSourceStr);
    }

    /**
     * 所有数据库
     */
    @Getter
    private final Map<String, SQLiteDataSource> dataSourceMap = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    @Setter
    private String primary = "master";

    private SQLiteDataSource determineDataSource() {
        return getDataSource(peek());
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(null, null);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        val ds = peek();
        Connection connection = connectionMap.get(ds);
        if (connection != null) {
            return connection;
        }
        connection = new ConnectionProxy(
                determineDataSource().getConnection(username, password)
        );
        connectionMap.put(ds, connection);
        return connection;
    }

    private SQLiteDataSource determinePrimaryDataSource() {
        log.debug("dynamic-datasource switch to the primary datasource");
        SQLiteDataSource dataSource = dataSourceMap.get(primary);
        if (dataSource != null) {
            return dataSource;
        }
        throw new CannotFindDataSourceException("dynamic-datasource can not find primary datasource");
    }

    /**
     * 获取数据源
     *
     * @param ds 数据源名称
     * @return 数据源
     */
    private SQLiteDataSource getDataSource(String ds) {
        if (ds == null) {
            return determinePrimaryDataSource();
        } else if (dataSourceMap.containsKey(ds)) {
            log.debug("dynamic-datasource switch to the datasource named [{}]", ds);
            return dataSourceMap.get(ds);
        }
        throw new CannotFindDataSourceException("dynamic-datasource could not find a datasource named" + ds);
    }

    /**
     * 添加数据源
     *
     * @param ds 数据源名称
     * @param dataSource 数据源
     */
    public synchronized void addDataSource(String ds, SQLiteDataSource dataSource) {
        dataSourceMap.put(ds, dataSource);
        log.info("dynamic-datasource - add a datasource named [{}] success", ds);
    }

    /**
     * 删除数据源
     *
     * @param ds 数据源名称
     */
    public synchronized void removeDataSource(String ds) {
        if (!StringUtils.hasText(ds)) {
            throw new RuntimeException("remove parameter could not be empty");
        }
        if (primary.equals(ds)) {
            throw new RuntimeException("could not remove primary datasource");
        }
        if (dataSourceMap.containsKey(ds)) {
            dataSourceMap.remove(ds);
            log.info("dynamic-datasource - remove the database named [{}] success", ds);
        } else {
            log.warn("dynamic-datasource - could not find a database named [{}]", ds);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @NotNull T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineDataSource().isWrapperFor(iface));
    }
}
