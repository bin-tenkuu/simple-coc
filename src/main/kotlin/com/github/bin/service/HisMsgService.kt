package com.github.bin.service

import com.github.bin.controller.WebSocketHandler
import com.github.bin.entity.HisMsg
import com.github.bin.mapper.HisMsgMapper
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Service
class HisMsgService(
        private val baseMapper: HisMsgMapper
) : InitializingBean {
    override fun afterPropertiesSet() {
        WebSocketHandler.msgService = this
    }

    fun historyMsg(id: Long?): List<HisMsg> {
        return baseMapper.historyMsg(id)
    }

}
