package com.github.bin.service;

import com.github.bin.config.MsgDataSource;
import com.github.bin.entity.msg.HisMsg;
import com.github.bin.mapper.msg.HisMsgMapper;
import com.github.bin.model.MessageIn;
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

    private static void set(String roomId) {
        MsgDataSource.set(roomId);
    }

    public static HisMsg saveOrUpdate(String roomId, MessageIn.Msg msg) {
        set(roomId);
        HisMsg hisMsg;
        if (msg.getId() == null) {
            hisMsg = hisMsgMapper.insert(msg.getType().name(), msg.getMsg(), msg.getRole());
        } else {
            hisMsg = hisMsgMapper.update(msg.getId(), msg.getMsg(), msg.getRole());
        }
        return hisMsg;
    }

    public static List<HisMsg> historyMsg(String roomId, Long id, int limit) {
        set(roomId);
        return hisMsgMapper.historyMsg(id, limit);
    }

    public static List<HisMsg> listAll(String roomId, long offset, long size) {
        set(roomId);
        return hisMsgMapper.listAll(offset, size);
    }

    public static Long count(String roomId) {
        set(roomId);
        return hisMsgMapper.count();
    }
}
