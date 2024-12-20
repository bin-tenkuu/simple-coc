package com.github.bin.service;

import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.RoomRole;
import com.github.bin.enums.ElPosition;
import com.github.bin.enums.ElType;
import com.github.bin.enums.MsgType;
import com.github.bin.model.MessageIn;
import com.github.bin.model.MessageOut;
import com.github.bin.repository.HisMsgService;
import com.github.bin.util.IdWorker;
import com.github.bin.util.JsonUtil;
import com.github.bin.util.MessageUtil;
import com.github.bin.util.ThreadUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author bin
 * @since 2023/08/22
 */
@Slf4j
public final class RoomConfig implements Closeable {
    public static final int DEFAULT_ROLE = -1;
    public static final int BOT_ROLE = -10;

    public final CocService cocService = new CocService();
    private final HashMap<String, SessionWrapper> sessions = new HashMap<>();
    private final IdWorker idWorker = new IdWorker(0L);
    @Getter
    private final Room room;
    @Getter
    private volatile boolean hold;
    /**
     * 顶部消息,随意修改且不保存，不参与其他操作
     */
    public String topMessage;

    public RoomConfig(Room room) {
        this.room = room;
        this.hold = room != null;
    }

    // region client

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
        return sessions.isEmpty();
    }

    public int size() {
        return sessions.size();
    }

    public String getRoomId() {
        return room.getId();
    }

    @Nullable
    public RoomRole getRole(String id) {
        val wrapper = sessions.get(id);
        return wrapper == null ? null : wrapper.role;
    }

    public void addClient(String id, WebSocketSession session, Integer roleId) {
        hold = true;
        val role = room.getRoles().get(roleId);
        val wrapper = new SessionWrapper(idWorker.nextId(), session, role);
        val last = sessions.put(id, wrapper);
        if (role != null) {
            if (last == null) {
                val message = MessageOut.ElNotification.of(
                        String.format("%s 进入房间", role.getName()),
                        ElType.I, ElPosition.TR
                );
                sendAll(message);
                log.info("{} ({}) room '{}'，进入角色：{}",
                        wrapper.id, getRemoteAddr(session), getRoomId(), role);
            } else if (!Objects.equals(role, last.role)) {
                log.info("{} ({}) room '{}'，切换角色：{} -> {}",
                        wrapper.id, getRemoteAddr(session), getRoomId(), role, last.role);
            }
        }
    }

    public void removeClient(WebSocketSession session) {
        val wrapper = sessions.remove(session.getId());
        if (wrapper == null) {
            return;
        }
        val role = wrapper.role;
        if (role != null) {
            val message = MessageOut.ElNotification.of(
                    String.format("%s 离开房间", role.getName()),
                    ElType.W, ElPosition.TR
            );
            sendAll(message);
            log.info("{} ({}) 断开连接", wrapper.id, getRemoteAddr(session));
        }
    }

    // endregion
    // region send

    public void sendAll(MessageOut msg) {
        val textMessage = toMessage(msg);
        for (val wrapper : new ArrayList<>(sessions.values())) {
            val session = wrapper.session;
            ThreadUtil.execute(() -> {
                try {
                    send(session, textMessage);
                } catch (IOException e) {
                    IOUtils.closeQuietly(session);
                }
            });
        }
    }

    public void send(String id, MessageOut msg) throws IOException {
        val textMessage = toMessage(msg);
        val wrapper = sessions.get(id);
        if (wrapper != null) {
            send(wrapper.session, textMessage);
        }
    }

    private static TextMessage toMessage(MessageOut msg) {
        val json = JsonUtil.toJson(msg);
        return new TextMessage(json);
    }

    public static void send(WebSocketSession session, MessageOut msg) throws IOException {
        val textMessage = toMessage(msg);
        session.sendMessage(textMessage);
    }

    private static void send(WebSocketSession session, TextMessage textMessage) throws IOException {
        if (session != null && session.isOpen()) {
            session.sendMessage(textMessage);
        }
    }

    public void sendAsBot(String msg) {
        sendAsBot(MsgType.text, msg);
    }

    public void sendAsBot(MsgType type, String msg) {
        if (getRoom().getArchive()) {
            return;
        }
        val text = new MessageIn.Msg(type, null, BOT_ROLE, msg);
        val hisMsg = HisMsgService.saveOrUpdate(getRoomId(), text);
        sendAll(MessageUtil.toMessage(hisMsg));
    }

    // endregion

    @Override
    public void close() {
        for (val wrapper : new ArrayList<>(sessions.values())) {
            IOUtils.closeQuietly(wrapper.session);
        }
        HisMsgService.removeDataSource(getRoomId());
        sessions.clear();
    }

    private static String getRemoteAddr(WebSocketSession session) {
        InetSocketAddress remoteAddress = session.getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getHostName() : "unknown";
    }

    private record SessionWrapper(long id, WebSocketSession session, RoomRole role) {
    }
}
