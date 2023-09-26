package com.github.bin.config;

import com.github.bin.controller.ChatWebSocketHandler;
import com.github.bin.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Map;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
@Slf4j
public class WebSocketConfig implements WebSocketConfigurer, HandshakeInterceptor {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(), "/ws/{roomId}")
                .addInterceptors(this)
                .setAllowedOrigins("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // ws 传输数据的时候，数据过大有时候会接收不到，所以在此处设置bufferSize
        container.setMaxTextMessageBufferSize(512000);
        container.setMaxBinaryMessageBufferSize(512000);
        container.setMaxSessionIdleTimeout(15 * 60000L);
        return container;
    }

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            val roomId = serverHttpRequest.getURI().getPath().substring(4);
            val remoteHost = serverHttpRequest.getServletRequest().getRemoteAddr();
            var config = RoomService.getSafe(roomId);
            // 外部保证 room 是有效room
            if (config == null) {
                log.warn("'{}' 尝试连接 room '{}' （不存在）", remoteHost, roomId);
                return false;
            }
            attributes.put("roomId", roomId);
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception) {

    }
}
