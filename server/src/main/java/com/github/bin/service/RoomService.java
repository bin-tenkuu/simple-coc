package com.github.bin.service;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.bin.config.MsgDataSource;
import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.RoomRole;
import com.github.bin.enums.MsgType;
import com.github.bin.mapper.master.RoomMapper;
import com.github.bin.model.IdAndName;
import com.github.bin.model.MessageIn;
import com.github.bin.model.MessageOut;
import com.github.bin.model.login.LoginUser;
import com.github.bin.util.MessageUtil;
import com.github.bin.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.compress.utils.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
@Slf4j
public class RoomService {
    // region ROOM_MAP

    private static final HashMap<String, RoomConfig> ROOM_MAP = new HashMap<>();

    public static RoomConfig get(String id) {
        var roomConfig = ROOM_MAP.get(id);
        if (roomConfig == null) {
            roomConfig = new RoomConfig(roomMapper.selectById(id));
            ROOM_MAP.put(id, roomConfig);
        } else {
            roomConfig.hold();
        }
        return roomConfig;
    }

    public static RoomConfig getSafe(String id) {
        var roomConfig = get(id);
        if (roomConfig.isEnable()) {
            return roomConfig;
        } else {
            return null;
        }
    }

    public static Collection<RoomConfig> values() {
        return ROOM_MAP.values();
    }

    // endregion
    // region FILE_MAP

    public static final HashMap<File, Long> FILE_MAP = new HashMap<>();

    private static void addFile(File file) {
        val min10 = 10 * 60 * 1000L;
        FILE_MAP.put(file, System.currentTimeMillis() + min10);
        log.info("添加文件: {}", file.getName());
    }

    public static Iterator<Map.Entry<File, Long>> fileIter() {
        return FILE_MAP.entrySet().iterator();
    }

    // endregion
    private static RoomMapper roomMapper;

    @Autowired
    public void setRoomMapper(RoomMapper roomMapper) {
        RoomService.roomMapper = roomMapper;
    }

    public static List<IdAndName> rooms() {
        return roomMapper.listIdAndName();
    }

    @Nullable
    public static Room getById(String id) {
        var roomConfig = get(id);
        return roomConfig.getRoom();
    }

    public static boolean removeById(String id) {
        val roomConfig = ROOM_MAP.remove(id);
        if (roomConfig != null) {
            roomConfig.close();
        }
        return SqlHelper.retBool(roomMapper.deleteById(id));
    }

    public static void saveOrUpdate(Room room) {
        val id = room.getId();
        val config = get(id);
        val old = config.getRoom();
        if (old != null) {
            old.setId(room.getId());
            old.setName(room.getName());
            old.setRoles(room.getRoles());
            old.setArchive(room.getArchive());
            roomMapper.updateById(old);
            config.sendAll(new MessageOut.RoomMessage(old));
        } else {
            room.setUserId(LoginUser.getUserId());
            ROOM_MAP.put(id, new RoomConfig(room));
            roomMapper.insert(room);
            MsgDataSource.addDataSource(id);
        }
    }

    public static ResponseEntity<Resource> exportHistoryMsg(String id) {
        val fileName = "logs/room/" + id + ".zip";
        val file = new File(fileName);
        if (!file.isFile()) {
            val config = getById(id);
            if (config == null) {
                return ResponseEntity.ok(null);
            }
            createRoomLog(file, id, config.getRoles());
        }
        addFile(file);
        val headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=room_" + id + ".zip");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }

    // region handleMessage

    public static void handleMessage(WebSocketSession session, MessageIn msg) {
        switch (msg) {
            case MessageIn.Default defMsg -> handleMessage(session, defMsg);
            case MessageIn.Msg message -> handleMessage(session, message);
            default -> {
            }
        }
    }

    private static void handleMessage(WebSocketSession session, MessageIn.Default defMsg) {
        val lastRoomConfig = getRoom(session);
        if (lastRoomConfig != null) {
            lastRoomConfig.removeClient(session);
        }
        val roomConfig = get(defMsg.getRoomId());
        if (!roomConfig.isEnable()) {
            IOUtils.closeQuietly(session);
            return;
        }
        val id = session.getId();
        setRoomId(session, roomConfig.getRoomId());
        // 更新角色，根据id获取历史消息
        roomConfig.addClient(id, session, defMsg.getRole());
        val list = HisMsgService.historyMsg(roomConfig.getRoomId(), defMsg.getId(), 20);
        ThreadUtil.execute(roomConfig, config -> {
            try {
                config.send(id, new MessageOut.RoomMessage(config.getRoom()));
                for (val hisMsg : list) {
                    config.send(id, MessageUtil.toMessage(hisMsg));
                }
            } catch (IOException e) {
                IOUtils.closeQuietly(session);
            }
        });
    }

    private static void handleMessage(WebSocketSession session, MessageIn.Msg message) {
        RoomConfig roomConfig = getRoom(session);
        if (roomConfig == null) {
            IOUtils.closeQuietly(session);
            return;
        }
        if (roomConfig.isArchive()) {
            return;
        }
        val id = session.getId();
        RoomRole role = roomConfig.getRole(id);
        if (role == null) {
            val roomRole = roomConfig.getRoom().getRoles().get(RoomConfig.DEFAULT_ROLE);
            if (roomRole == null) {
                return;
            }
            role = roomRole.copy(message.getRole());
            roomConfig.getRoom().addRole(role);
            roomMapper.updateById(roomConfig.getRoom());
            roomConfig.sendAll(new MessageOut.RoomMessage(roomConfig.getRoom()));
            return;
        }
        val roleId = role.getId();
        val b = message.getType() == MsgType.text
                && roleId != RoomConfig.BOT_ROLE
                && message.getId() == null
                && message.getMsg().startsWith(".");
        message.setRole(roleId);
        val hisMsg = HisMsgService.saveOrUpdate(roomConfig.getRoomId(), message);
        roomConfig.sendAll(MessageUtil.toMessage(hisMsg));
        if (b) {
            CommandServer.handleBot(roomConfig, id, message.getMsg().substring(1).trim());
        }
    }

    public static void handleClose(WebSocketSession session) {
        val roomConfig = getRoom(session);
        if (roomConfig != null) {
            roomConfig.removeClient(session);
        }
    }

    private static void setRoomId(WebSocketSession session, String roomId) {
        session.getAttributes().put("roomId", roomId);
    }

    @Nullable
    private static RoomConfig getRoom(WebSocketSession session) {
        val roomId = (String) session.getAttributes().get("roomId");
        return getSafe(roomId);

    }

    // endregion
    private static void createRoomLog(File file, String id, Map<Integer, RoomRole> roles) {
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdir();
        try (val it = new ZipOutputStream(new FileOutputStream(file))) {
            it.setComment("导出历史记录");
            it.setLevel(9);
            it.putNextEntry(new ZipEntry("index.html"));
            val writer = new BufferedWriter(new OutputStreamWriter(it));
            val allCount = HisMsgService.count(id);
            val size = 10L;
            var index = 0L;
            while (index < allCount) {
                val list = HisMsgService.listAll(id, index, size);
                for (val msg : list) {
                    val roleId = msg.getRole();
                    val role = roles.getOrDefault(roleId,
                            new RoomRole(roleId, roleId.toString(), "black"));
                    val color = role.getColor();
                    writer.append("<div style=\"color: ").append(color).append("\">");
                    writer.append(toHtml(msg.getType(), msg.getMsg(), role));
                    writer.append("</div>\n");
                    writer.flush();
                }
                index += size;
            }
            it.closeEntry();
            it.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHtml(MsgType type, String msg, RoomRole role) {
        StringBuilder sb = new StringBuilder();
        val name = role.getName();
        val user = "<span>&lt;" + name + "&gt;:</span>";
        switch (type) {
            case text -> sb.append(user).append("<span>").append(msg).append("</span>");
            case pic -> sb.append(user).append("<img alt='img' src='").append(msg).append("'/>");
            case sys -> sb.append("<i>").append(msg).append("</i>");
        }
        return sb.toString();
    }

}
