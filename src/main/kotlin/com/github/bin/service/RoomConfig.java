package com.github.bin.service;

import cn.hutool.core.io.IoUtil;
import com.github.bin.entity.master.Room;
import com.github.bin.model.Message;
import com.github.bin.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bin
 * @since 2023/08/22
 */
@RequiredArgsConstructor
@Slf4j
public final class RoomConfig implements Closeable {
    private final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    private final HashMap<String, Long> roles = new HashMap<>();

    @Getter
    private final Room room;
    public volatile transient boolean hold = true;

    public String getId() {
        return room.getId();
    }

    public void addClient(WebSocketSession session) {
        clients.put(session.getId(), session);
    }

    public void removeClient(WebSocketSession session) {
        clients.remove(session.getId());
    }

    public long getRole(String session) {
        return roles.getOrDefault(session, -1L);
    }

    public void setRole(String session, long role) {
        roles.put(session, role);
    }

    public void sendAll(Message msg) {
        val json = JsonUtil.toJson(msg);

        val iterator = clients.values().iterator();
        while (iterator.hasNext()) {
            val client = iterator.next();
            try {
                client.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                iterator.remove();
                IOUtils.closeQuietly(client);
            }
        }
    }

    public void send(String id, Message msg) {
        val json = JsonUtil.toJson(msg);
        val session = clients.get(id);
        if (session != null) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                IOUtils.closeQuietly(clients.remove(id));
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() {
        for (val client : clients.values()) {
            IoUtil.close(client);
        }
        clients.clear();
    }
}
