package com.github.bin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.bin.model.Message;
import com.github.bin.service.RoomConfig;
import com.github.bin.service.RoomService;
import com.github.bin.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.*;

/**
 * @author bin
 * @since 2023/08/22
 */
@Slf4j
public class ChatWebSocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        val roomConfig = getRoom(session);
        roomConfig.addClient(session);
    }

    @Override
    public void handleMessage(
            @NotNull WebSocketSession session, @NotNull WebSocketMessage<?> message
    ) throws Exception {
        if (message instanceof TextMessage textMessage) {
            val roomConfig = getRoom(session);
            val payload = textMessage.getPayload();
            try {
                val msg = JsonUtil.toBean(payload, Message.class);
                RoomService.handleMessage(roomConfig, session, msg);
            } catch (JsonProcessingException e) {
                session.close(new CloseStatus(4000, "消息格式错误"));
            } catch (Exception e) {
                log.warn("{} 消息处理错误: '{}'", session.getId(), payload, e);
                session.close(new CloseStatus(4000, "服务器错误:" + e.getMessage()));
            }
        }
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable e) throws
            Exception {
        log.warn("{} websocket 异常", session.getId(), e);
        val roomConfig = getRoom(session);
        roomConfig.removeClient(session);
        if (session.isOpen()) {
            session.close(new CloseStatus(4000, "服务器错误:" + e.getMessage()));
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) {
        val roomConfig = getRoom(session);
        roomConfig.removeClient(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private RoomConfig getRoom(WebSocketSession session) {
        return RoomService.get((String) session.getAttributes().get("roomId"));
    }

}
