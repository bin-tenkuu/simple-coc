package com.github.bin.service;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.bin.command.Command;
import com.github.bin.config.MsgDataSource;
import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.RoomRole;
import com.github.bin.entity.msg.HisMsg;
import com.github.bin.mapper.master.RoomMapper;
import com.github.bin.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
@RequiredArgsConstructor
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

    public static RoomConfig set(String id, RoomConfig config) {
        return ROOM_MAP.put(id, config);
    }

    public static Collection<RoomConfig> values() {
        return ROOM_MAP.values();
    }

    // endregion

    private static RoomMapper roomMapper;

    @Autowired
    public void setRoomMapper(RoomMapper roomMapper) {
        RoomService.roomMapper = roomMapper;
    }

    private static List<Command> commands;

    @Autowired
    public void setCommands(List<Command> commands) {
        RoomService.commands = commands;
    }

    public static List<Room> rooms() {
        return roomMapper.selectList(null);
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

    public static boolean saveOrUpdate(Room room) {
        val id = room.getId();
        val config = ROOM_MAP.get(id);
        if (config != null) {
            val old = config.getRoom();
            old.setName(room.getName());
            old.setRoles(room.getRoles());
            roomMapper.updateById(old);
            config.sendAll(new Message.RoomMessage(old));
        } else {
            ROOM_MAP.put(id, new RoomConfig(room));
            roomMapper.insert(room);
            MsgDataSource.addDataSource(id);
        }
        return true;
    }

    public static <T extends Message.Msg> void saveMsgAndSend(RoomConfig room, T msg, int role) {
        msg.setRole(role);
        HisMsgService.accept(room.getId(), hisMsgMapper -> {
            if (msg.getId() == null) {
                msg.setId(hisMsgMapper.insert(msg.getType(), msg.getMsg(), role));
            } else {
                hisMsgMapper.update(msg.getId(), msg.getMsg(), role);
            }
        });
        room.sendAll(msg);
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
        } else {
            //noinspection ResultOfMethodCallIgnored
            file.setLastModified(System.currentTimeMillis());
        }
        val headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=room_" + id + ".zip");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }

    public static void handleMessage(RoomConfig roomConfig, WebSocketSession session, Message msg) {
        val id = session.getId();
        if (msg instanceof Message.Default defMsg) {
            // 更新角色，根据id获取历史消息
            val roomRole = roomConfig.setRole(id, defMsg.getRole());
            log.info("{} 进入 room '{}'，角色：{}", id, roomConfig.getId(), roomRole);
            val list = HisMsgService.historyMsg(roomConfig.getId(), defMsg.getId(), 20);
            try {
                roomConfig.send(id, new Message.RoomMessage(roomConfig.getRoom()));
                for (val hisMsg : list) {
                    roomConfig.send(id, toMessage(hisMsg));
                }
            } catch (IOException e) {
                IOUtils.closeQuietly(session);
            }
        } else if (msg instanceof Message.Msg message) {
            RoomRole role = roomConfig.getRole(id);
            if (role == null) {
                val roomRole = roomConfig.getRoom().getRoles().get(RoomConfig.DEFAULT_ROLE);
                if (roomRole == null) {
                    return;
                }
                role = roomRole.copy(message.getRole());
                roomConfig.getRoom().addRole(role);
                roomMapper.updateById(roomConfig.getRoom());
                roomConfig.sendAll(new Message.RoomMessage(roomConfig.getRoom()));
                return;
            }
            val roleId = role.getId();
            val b = message instanceof Message.Text
                    && roleId != RoomConfig.BOT_ROLE
                    && message.getId() == null
                    && message.getMsg().startsWith(".");
            saveMsgAndSend(roomConfig, message, roleId);
            if (b) {
                handleBot(roomConfig, id, message.getMsg().substring(1).trim());
            }
        }
    }

    private static void handleBot(RoomConfig roomConfig, String id, String msg) {
        try {
            for (val command : commands) {
                if (command.invoke(roomConfig, id, msg)) {
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("bot 处理异常", e);
            roomConfig.sendAsBot("【ERROR】bot 处理异常，请联系管理员");
        }
    }

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

    private static Message toMessage(HisMsg hisMsg) {
        val id = hisMsg.getId();
        val msg = hisMsg.getMsg();
        val role = hisMsg.getRole();
        return switch (hisMsg.getType()) {
            case Message.TEXT -> new Message.Text(id, role, msg);
            case Message.PIC -> new Message.Pic(id, role, msg);
            case Message.SYS -> new Message.Sys(id, role, msg);
            default -> new Message.Msgs();
        };
    }

    private static String toHtml(String type, String msg, RoomRole role) {
        StringBuilder sb = new StringBuilder();
        val name = role.getName();
        val user = "<span>&lt;" + name + "&gt;:</span>";
        switch (type) {
            case Message.TEXT -> sb.append(user).append("<span>").append(msg).append("</span>");
            case Message.PIC -> sb.append(user).append("<img alt='img' src='").append(msg).append("'/>");
            case Message.SYS -> sb.append("<i>").append(msg).append("</i>");
        }
        return sb.toString();
    }

}
