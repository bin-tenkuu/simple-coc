package com.github.bin.service;

import com.github.bin.config.datasource.DynamicRoutingDataSource;
import com.github.bin.entity.msg.HisMsg;
import com.github.bin.mapper.msg.HisMsgMapper;
import com.github.bin.model.MessageIn;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteDataSource;

import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
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

    private static HisMsgMapper hisMsgMapper;

    @Autowired
    public void setHisMsgMapper(HisMsgMapper hisMsgMapper) {
        HisMsgService.hisMsgMapper = hisMsgMapper;
    }

    public static String getDbUrl(String name) {
        return "sql/hisMsg_" + name + ".db";
    }

    public static void addDataSource(String name) {
        val url = getDbUrl(name);
        //        val dbFile = new File(url);
        //        if (!dbFile.exists()) {
        //            try {
        //                Files.copy(Path.of("sql/hisMsg.db"), dbFile.toPath(),
        //                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        //            } catch (IOException e) {
        //                throw new RuntimeException(e);
        //            }
        //        }
        val sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:" + url);
        sqLiteDataSource.setSharedCache(true);
        sqLiteDataSource.setLoadExtension(true);
        sqLiteDataSource.setCountChanges(true);
        sqLiteDataSource.setLegacyFileFormat(false);
        sqLiteDataSource.setLegacyAlterTable(false);
        DATA_SOURCE.addDataSource(name, sqLiteDataSource);
        DynamicRoutingDataSource.push(name);
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
                )
                    strict;""").update();
    }

    public static void setDataSource(String name) {
        if (!DATA_SOURCE.getDataSourceMap().containsKey(name)) {
            addDataSource(name);
        }
        DynamicRoutingDataSource.push(name);
    }

    public static void removeDataSource(String name) {
        DATA_SOURCE.removeDataSource(name);
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
            //            hisMsg = hisMsgMapper.insert(msg.getType().name(), msg.getMsg(), msg.getRole());
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
            //            hisMsg = hisMsgMapper.update(msg.getId(), msg.getMsg(), msg.getRole());
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
        return hisMsgMapper.listAll(offset, size);
    }

    public static HisMsg getById(String roomId, int id) {
        setDataSource(roomId);
        return hisMsgMapper.getById(id);
    }

    public static Long count(String roomId) {
        setDataSource(roomId);
        return hisMsgMapper.count();
    }
}
