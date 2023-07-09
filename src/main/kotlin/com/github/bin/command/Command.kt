package com.github.bin.command

import com.github.bin.service.RoomConfig

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */
interface Command {
    /**
     * 消息处理
     * @param roomConfig RoomConfig
     * @param id String
     * @param msg 开头去除指令前缀，去除前后空格
     * @return 是否取消后续处理
     */
    fun invoke(roomConfig: RoomConfig, id: String, msg: String): Boolean
}
