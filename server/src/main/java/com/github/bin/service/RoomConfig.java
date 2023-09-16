package com.github.bin.service;

import cn.hutool.core.io.IoUtil;
import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.RoomRole;
import com.github.bin.model.Message;
import com.github.bin.util.IdWorker;
import com.github.bin.util.JsonUtil;
import com.github.bin.util.MessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bin
 * @since 2023/08/22
 */
@Slf4j
public final class RoomConfig implements Closeable {
    public static final int DEFAULT_ROLE = -1;
    public static final int BOT_ROLE = -10;

    private final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    private final HashMap<String, RoomRole> roles = new HashMap<>();
    @Getter
    public final IdWorker idWorker = new IdWorker(0L);
    @Getter
    private final Room room;
    @Getter
    private volatile boolean hold;

    public RoomConfig(Room room) {
        this.room = room;
        this.hold = room != null;
    }

    public boolean isEnable() {
        return room != null;
    }

    public void hold() {
        if (isEnable()) {
            hold = true;
        }
    }

    public void unhold() {
        hold = false;
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    public String getId() {
        return room.getId();
    }

    public void addClient(WebSocketSession session) {
        hold = true;
        clients.put(session.getId(), session);
        log.info("{} ({}) 连接 room '{}' ", session.getId(), getRemoteAddr(session), getId());
    }

    @SuppressWarnings("resource")
    public void removeClient(WebSocketSession session) {
        roles.remove(session.getId());
        clients.remove(session.getId());
        log.info("{} ({}) 断开连接", session.getId(), getRemoteAddr(session));
    }

    @Nullable
    public RoomRole getRole(String session) {
        return roles.get(session);
    }

    public void setRole(String session, @Nullable Integer roleId) {
        val role = room.getRoles().get(roleId);
        roles.put(session, role);
        log.info("{} room '{}'，切换角色：{}", session, getId(), role);
    }

    public void sendAll(Message msg) {
        val json = JsonUtil.toJson(msg);
        val textMessage = new TextMessage(json);
        val iterator = clients.values().iterator();
        while (iterator.hasNext()) {
            val client = iterator.next();
            try {
                send(client, textMessage);
            } catch (IOException e) {
                iterator.remove();
                IOUtils.closeQuietly(client);
            }
        }
    }

    public void send(String id, Message msg) throws IOException {
        val json = JsonUtil.toJson(msg);
        val session = clients.get(id);
        send(session, new TextMessage(json));
    }

    private static void send(WebSocketSession session, TextMessage textMessage) throws IOException {
        if (session != null && session.isOpen()) {
            session.sendMessage(textMessage);
        }
    }

    @Override
    public void close() {
        for (val client : clients.values()) {
            IoUtil.close(client);
        }
        clients.clear();
        roles.clear();
    }

    public void sendAsBot(String msg) {
        val text = new Message.Text(null, RoomConfig.BOT_ROLE, msg);
        val hisMsg = HisMsgService.saveOrUpdate(getId(), text);
        sendAll(MessageUtil.toMessage(hisMsg));
    }

    private String getRemoteAddr(WebSocketSession session) {
        return Optional.ofNullable(session.getRemoteAddress())
                .map(InetSocketAddress::getHostName)
                .orElse("unknown");
    }
}
