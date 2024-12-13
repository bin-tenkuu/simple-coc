package com.github.bin.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.bin.model.MessageIn;
import com.github.bin.repository.RoomService;
import com.github.bin.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.io.IOException;
import java.util.Map;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer, HandshakeInterceptor, WebSocketHandler {
    public static final int MAX_TEXT_MESSAGE_SIZE = 512000;

    // region WebSocketConfigurer

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this, "/ws")
                .addInterceptors(this)
                .setAllowedOrigins("*");
    }
    // endregion

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // ws 传输数据的时候，数据过大有时候会接收不到，所以在此处设置bufferSize
        container.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);
        container.setMaxBinaryMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);
        container.setMaxSessionIdleTimeout(15 * 60000L);
        return container;
    }

    // region HandshakeInterceptor

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        return true;
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception
    ) {

    }
    // endregion

    // region WebSocketHandler

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
    }

    @Override
    public void handleMessage(
            @NotNull WebSocketSession session, @NotNull WebSocketMessage<?> message
    ) throws Exception {
        if (message instanceof TextMessage textMessage) {
            val payload = textMessage.getPayload();
            try {
                val msg = JsonUtil.toBean(payload, MessageIn.class);
                RoomService.handleMessage(session, msg);
            } catch (JsonProcessingException e) {
                session.close(new CloseStatus(4000, "消息格式错误"));
            } catch (Exception e) {
                log.warn("{} 消息处理错误: '{}'", session.getId(), payload, e);
                session.close(new CloseStatus(4000, "服务器错误:" + e.getMessage()));
            }
        }
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable e) throws Exception {
        if (e instanceof IOException) {
            log.warn("websocket IO异常: {}: {}", e.getClass(), e.getMessage());
        } else {
            log.warn("websocket 异常", e);
        }
        if (session.isOpen()) {
            session.close(new CloseStatus(4000, "服务器错误:" + e.getMessage()));
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) {
        RoomService.handleClose(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    // endregion
}
