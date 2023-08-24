package com.github.bin.config;

import com.github.bin.service.RoomConfig;
import com.github.bin.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author bin
 * @since 2023/08/22
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {
    private final RoomService roomService;

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
            var config = RoomService.get(roomId);
            if (config == null) {
                val room = roomService.getById(roomId);
                if (room == null) {
                    log.warn("'{}' 尝试连接 room '{}' （不存在）", remoteHost, roomId);
                    return false;
                }
                config = new RoomConfig(room);
                RoomService.set(roomId, config);
            } else {
                config.setHold(true);
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
