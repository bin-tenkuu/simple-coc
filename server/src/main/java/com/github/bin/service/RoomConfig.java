package com.github.bin.service;

import cn.hutool.core.io.IoUtil;
import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.RoomRole;
import com.github.bin.model.Message;
import com.github.bin.util.IdWorker;
import com.github.bin.util.JsonUtil;
import com.github.bin.util.MessageUtil;
import com.github.bin.util.ThreadUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
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

    @NotNull
    private final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    @NotNull
    private final HashMap<String, RoomRole> roles = new HashMap<>();
    @Getter
    private final IdWorker idWorker;
    @Getter
    private final Room room;
    @Getter
    private volatile boolean hold;

    public RoomConfig(Room room) {
        this.room = room;
        this.hold = room != null;
        if (hold) {
            idWorker = new IdWorker(0L);
        } else {
            idWorker = null;
        }
    }

    public boolean isEnable() {
        return room != null;
    }

    public void hold() {
        if (isEnable()) {
            hold = true;
        }
    }

    public void unHold() {
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

    public void removeClient(WebSocketSession session) {
        val role = roles.remove(session.getId());
        clients.remove(session.getId());
//        if (role != null) {
//            sendSys(role.getId(), "&gt;&gt; " + role.getName() + " 离开房间");
//        }
        log.info("{} ({}) 断开连接", session.getId(), getRemoteAddr(session));
    }

    @Nullable
    public RoomRole getRole(String session) {
        return roles.get(session);
    }

    public void setRole(String session, @Nullable Integer roleId) {
        val role = room.getRoles().get(roleId);
        val oldRole = roles.put(session, role);
        if (oldRole == null) {
//            sendSys(role.getId(), "&gt;&gt; " + role.getName() + " 进入房间");
            log.info("{} room '{}'，进入房间：{}", session, getId(), role);
        } else if (oldRole != role) {
//            sendSys(role.getId(), "&gt;&gt; " + oldRole.getName() + " 角色变为 " + role.getName());
            log.info("{} room '{}'，切换角色：{} -> {}", session, getId(), oldRole, role);
        }
    }

    public void sendAll(Message msg) {
        if (isArchive()) {
            return;
        }
        val json = JsonUtil.toJson(msg);
        val textMessage = new TextMessage(json);
        for (WebSocketSession session : new ArrayList<>(clients.values())) {
            ThreadUtil.execute(() -> {
                try {
                    send(session, textMessage);
                } catch (IOException e) {
                    IoUtil.close(session);
                }
            });
        }
    }

    public void send(String id, Message msg) throws IOException {
        if (isArchive()) {
            return;
        }
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
        for (val client : new ArrayList<>(clients.values())) {
            IoUtil.close(client);
        }
        clients.clear();
        roles.clear();
    }

    public void sendAsBot(String msg) {
        if (isArchive()) {
            return;
        }
        val text = new Message.Text(null, RoomConfig.BOT_ROLE, msg);
        val hisMsg = HisMsgService.saveOrUpdate(getId(), text);
        sendAll(MessageUtil.toMessage(hisMsg));
    }

    public void sendSys(int roleId, String msg) {
        if (isArchive()) {
            return;
        }
        val sys = new Message.Sys(null, roleId, msg);
        val hisMsg = HisMsgService.saveOrUpdate(getId(), sys);
        sendAll(MessageUtil.toMessage(hisMsg));
    }

    private String getRemoteAddr(WebSocketSession session) {
        return Optional.ofNullable(session.getRemoteAddress())
                .map(InetSocketAddress::getHostName)
                .orElse("unknown");
    }

    private boolean isArchive() {
        return room.getArchive();
    }
}
