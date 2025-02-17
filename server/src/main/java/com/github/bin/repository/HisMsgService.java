package com.github.bin.repository;

import com.github.bin.config.datasource.DynamicRoutingDataSource;
import com.github.bin.entity.msg.HisMsg;
import com.github.bin.model.MessageIn;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
@Slf4j
public class HisMsgService {
    public static final DynamicRoutingDataSource DATA_SOURCE;
    private static final JdbcClient CLIENT;

    static {
        DATA_SOURCE = new DynamicRoutingDataSource();
        DATA_SOURCE.setPrimary("default");
        CLIENT = JdbcClient.create(DATA_SOURCE);
        addDataSource("default");
    }

    public static String getDbUrl(String roomId) {
        return "sql/hisMsg_" + roomId + ".db";
    }

    public static void addDataSource(String roomId) {
        val url = getDbUrl(roomId);
        val sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:" + url);
        sqLiteDataSource.setSharedCache(true);
        sqLiteDataSource.setLoadExtension(true);
        sqLiteDataSource.setCountChanges(true);
        sqLiteDataSource.setLegacyFileFormat(false);
        sqLiteDataSource.setLegacyAlterTable(false);
        DATA_SOURCE.addDataSource(roomId, sqLiteDataSource);
        DynamicRoutingDataSource.push(roomId);
        sql("""
                create table if not exists his_msg
                (
                    id          integer not null
                        constraint his_msg_pk
                            primary key autoincrement,
                    type        text    not null,
                    msg         text    not null,
                    role        text    not null,
                    create_time text    not null,
                    update_time text    not null
                ) strict;""").update();
    }

    public static void setDataSource(String roomId) {
        if (!DATA_SOURCE.getDataSourceMap().containsKey(roomId)) {
            addDataSource(roomId);
        }
        DynamicRoutingDataSource.push(roomId);
    }

    public static void deleteDataSource(String roomId) {
        val url = getDbUrl(roomId);
        try {
            Files.deleteIfExists(Path.of(url));
        } catch (IOException e) {
            log.warn("删除数据库失败", e);
        }
        removeDataSource(roomId);
    }

    public static void removeDataSource(String roomId) {
        DATA_SOURCE.removeDataSource(roomId);
    }

    private static JdbcClient.StatementSpec sql(@Language("SQLite") String sql) {
        return CLIENT.sql(sql);
    }

    public static HisMsg saveOrUpdate(String roomId, MessageIn.Msg msg) {
        setDataSource(roomId);
        HisMsg hisMsg;
        if (msg.getId() == null) {
            hisMsg = sql("""
                    INSERT INTO his_msg (type, msg, role, create_time, update_time)
                    VALUES (?, ?, ?, datetime(), datetime())
                    returning id, type, msg, role""")
                    .param(1, msg.getType().name())
                    .param(2, msg.getMsg())
                    .param(3, msg.getRole())
                    .query(HisMsg.class)
                    .single();
        } else {
            hisMsg = sql("""
                    UPDATE his_msg
                    SET msg = ?,
                        role = ?,
                        update_time = datetime()
                    WHERE id = ?
                    returning id, type, msg, role""")
                    .param(1, msg.getMsg())
                    .param(2, msg.getRole())
                    .param(3, msg.getId())
                    .query(HisMsg.class)
                    .single();
        }
        return hisMsg;
    }

    public static List<HisMsg> historyMsg(String roomId, Integer id, int limit) {
        setDataSource(roomId);
        JdbcClient.StatementSpec spec;
        if (id != null) {
            spec = sql("""
                    SELECT id, type, msg, role
                    FROM his_msg
                    WHERE id < ?
                    order by id desc
                    limit ?""")
                    .param(1, id)
                    .param(2, limit);
        } else {
            spec = sql("""
                    SELECT id, type, msg, role
                    FROM his_msg
                    order by id desc
                    limit ?""")
                    .param(1, limit);

        }
        return spec.query(HisMsg.class).list();
    }

    public static List<HisMsg> listAll(String roomId, long offset, long size) {
        setDataSource(roomId);
        return sql("""
                select id, type, msg, role
                from his_msg
                limit ? offset ?""")
                .param(1, size)
                .param(2, offset)
                .query(HisMsg.class)
                .list();
    }

    public static HisMsg getById(String roomId, int id) {
        setDataSource(roomId);
        return sql("""
                select id, type, msg, role
                from his_msg
                where id = ?""")
                .param(1, id)
                .query(HisMsg.class)
                .optional()
                .orElse(null);
    }

    public static long count(String roomId) {
        setDataSource(roomId);
        return sql("select count(*) from his_msg")
                .query(Long.class)
                .single();
    }
}
