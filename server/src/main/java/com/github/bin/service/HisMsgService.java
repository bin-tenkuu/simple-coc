package com.github.bin.service;

import com.github.bin.config.MsgDataSource;
import com.github.bin.mapper.msg.HisMsgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Function;

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

    public static <T> T apply(String tableName, Function<HisMsgMapper, T> block) {
        MsgDataSource.set(tableName);
        return block.apply(hisMsgMapper);
    }

    public static void accept(String tableName, Consumer<HisMsgMapper> block) {
        MsgDataSource.set(tableName);
        block.accept(hisMsgMapper);
    }

}
