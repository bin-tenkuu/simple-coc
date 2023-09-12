package com.github.bin.service;

import com.github.bin.config.MsgDataSource;
import com.github.bin.entity.msg.HisMsg;
import com.github.bin.mapper.msg.HisMsgMapper;
import com.github.bin.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
@Slf4j
public class HisMsgService {
    private static HisMsgMapper hisMsgMapper;

    @Autowired
    public void setHisMsgMapper(HisMsgMapper hisMsgMapper) {
        HisMsgService.hisMsgMapper = hisMsgMapper;
    }

    private static void set(String tableName) {
        MsgDataSource.set(tableName);
    }

    public static HisMsg saveOrUpdate(String tableName, Message.Msg msg) {
        set(tableName);
        HisMsg hisMsg;
        if (msg.getId() == null) {
            hisMsg = hisMsgMapper.insert(msg.getType(), msg.getMsg(), msg.getRole());
        } else {
            hisMsg = hisMsgMapper.update(msg.getId(), msg.getMsg(), msg.getRole());
        }
        return hisMsg;
    }

    public static List<HisMsg> historyMsg(String tableName, Long id, int limit) {
        set(tableName);
        return hisMsgMapper.historyMsg(id, limit);
    }

    public static List<HisMsg> listAll(String tableName, long offset, long size) {
        set(tableName);
        return hisMsgMapper.listAll(offset, size);
    }

    public static Long count(String tableName) {
        set(tableName);
        return hisMsgMapper.count();
    }
}
