package com.github.bin.service;

import cn.hutool.core.io.IoUtil;
import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.RoomRole;
import com.github.bin.model.Message;
import com.github.bin.util.JsonUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bin
 * @since 2023/08/22
 */
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
public final class RoomConfig implements Closeable {
    public static final Long BOT_ROLE = -10L;
    private final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    private final HashMap<String, RoomRole> roles = new HashMap<>();
    private final Room room;
    private volatile transient boolean hold = true;

    public String getId() {
        return room.getId();
    }

    public void addClient(WebSocketSession session) {
        hold = true;
        clients.put(session.getId(), session);
    }

    public void removeClient(WebSocketSession session) {
        roles.remove(session.getId());
        clients.remove(session.getId());
    }

    @Nullable
    public RoomRole getRole(String session) {
        return roles.get(session);
    }

    public void setRole(String session, @Nullable Long roleId) {
        roles.put(session, room.getRoles().get(roleId));
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
        roles.clear();
    }

    private void sendAsBot(Message.Msg msg) {
        msg.setRole(-10);
        HisMsgService.accept(getId(),
                hisMsgMapper -> msg.setId(hisMsgMapper.insert(msg.getType(), msg.getMsg(), BOT_ROLE)));
        sendAll(msg);
    }

    public void sendAsBot(String msg) {
        val text = new Message.Text();
        text.setMsg(msg);
        sendAsBot(text);
    }

}
