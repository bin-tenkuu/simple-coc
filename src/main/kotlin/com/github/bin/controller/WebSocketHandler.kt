package com.github.bin.controller

import com.github.bin.config.Global
import com.github.bin.config.handler.MsgTableName
import com.github.bin.model.Message
import com.github.bin.model.RoomConfig
import com.github.bin.service.BotService
import com.github.bin.service.HisMsgService
import com.github.bin.service.RoomService
import jakarta.websocket.*
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.*

@Component
@Slf4j
@ServerEndpoint("/ws/{roomId}")
class WebSocketHandler {
    private lateinit var session: Session
    private lateinit var roomId: String
    private lateinit var roomConfig: RoomConfig
    private var role = ""

    companion object {
        private val log: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)

        @JvmStatic
        lateinit var msgService: HisMsgService

        @JvmStatic
        lateinit var roomService: RoomService

        @JvmStatic
        lateinit var botService: BotService
    }

    @OnOpen
    fun onOpen(session: Session, @PathParam("roomId") roomId: String) {
        val roomConfig = RoomService[roomId]
        this.session = session
        this.roomId = roomId
        if (roomConfig == null) {
            session.close()
            return
        }
        this.roomConfig = roomConfig
        roomConfig.clients += session
        session.asyncRemote.sendText(Global.toJson(Message.Roles(roomConfig.room.roles)))
    }

    @OnClose
    fun onClose() {
        if (::roomConfig.isInitialized) {
            roomConfig.clients -= session
        }
    }

    @OnError
    fun onError(throwable: Throwable) {
        session.close(CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, throwable.message))
        log.error("WebSocket error", throwable)
    }

    @OnMessage
    fun onMessage(message: String) {
        if (!::roomConfig.isInitialized) {
            return
        }
        when (val msg = Global.fromJson<Message>(message)) {
            is Message.Text -> {
                roomService.saveMsgAndSend(roomConfig, msg, role)
                botService.handler(roomConfig, msg.msg, role)
            }
            is Message.Pic -> roomService.saveMsgAndSend(roomConfig, msg, role)
            is Message.Default -> {
                role = msg.role
                if (role !in roomConfig.room.roles) {
                    session.close(CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, "角色不存在"))
                    return
                }
                val list = MsgTableName.invoke(roomConfig.room.id!!) {
                    msgService.historyMsg(msg.id)
                }.map {
                    when (it.type) {
                        Message.TEXT -> Message.Text(it.id!!, it.msg!!, it.role!!)
                        Message.PIC -> Message.Pic(it.id!!, it.msg!!, it.role!!)
                        else -> Message.Msgs()
                    }
                }
                session.asyncRemote.sendText(Global.toJson(Message.Msgs(list)))
            }
            is Message.Msgs -> return
            is Message.Roles -> return
        }
    }

}
