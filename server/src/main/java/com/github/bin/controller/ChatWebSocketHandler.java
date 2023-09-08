package com.github.bin.controller;

import com.github.bin.model.Message;
import com.github.bin.service.RoomConfig;
import com.github.bin.service.RoomService;
import com.github.bin.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler implements WebSocketHandler {

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        val roomConfig = getRoom(session);
        roomConfig.addClient(session);
        log.info("{} ({}) 连接 room '{}' ", session.getId(), getRemoteAddr(session), roomConfig.getId());
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
            } catch (Exception e) {
                log.warn("{} ({}) 消息格式错误: '{}'", session.getId(), getRemoteAddr(session), payload);
                session.close(new CloseStatus(4000, "消息格式错误"));
            }
        }
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) throws Exception {
        val roomConfig = getRoom(session);
        roomConfig.removeClient(session);
        if (session.isOpen()) {
            session.close(new CloseStatus(4000, "服务器错误:${exception.message}"));
        }
        log.warn("{} ({}) websocket 异常", session.getId(), getRemoteAddr(session), exception);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) {
        val roomConfig = getRoom(session);
        roomConfig.removeClient(session);
        log.info("{} ({}) 断开连接", session.getId(), getRemoteAddr(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private RoomConfig getRoom(WebSocketSession session) {
        return RoomService.get((String) session.getAttributes().get("roomId"));
    }

    private String getRemoteAddr(WebSocketSession session) {
        return Optional.ofNullable(session.getRemoteAddress())
                .map(InetSocketAddress::getHostName)
                .orElse("unknown");
    }
}
