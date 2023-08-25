package com.github.bin.service;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.bin.entity.master.Room;
import com.github.bin.entity.master.RoomRole;
import com.github.bin.entity.msg.HisMsg;
import com.github.bin.mapper.master.RoomMapper;
import com.github.bin.mapper.msg.HisMsgMapper;
import com.github.bin.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
    // region static

    private static final HashMap<String, RoomConfig> ROOM_MAP = new HashMap<>();

    public static RoomConfig get(String id) {
        return ROOM_MAP.get(id);
    }

    public static RoomConfig set(String id, RoomConfig config) {
        return ROOM_MAP.put(id, config);
    }

    public static Collection<RoomConfig> values() {
        return ROOM_MAP.values();
    }
    // endregion

    private final RoomMapper roomMapper;

    public List<Room> rooms() {
        return roomMapper.selectList(null);
    }

    @Nullable
    public Room getById(String id) {
        return roomMapper.selectById(id);
    }

    public boolean removeById(String id) {
        val roomConfig = ROOM_MAP.remove(id);
        if (roomConfig != null) {
            roomConfig.close();
        }
        return SqlHelper.retBool(roomMapper.deleteById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(Room room) {
        val id = room.getId();
        val config = ROOM_MAP.get(id);
        if (config != null) {
            val old = config.getRoom();
            old.setName(room.getName());
            old.setRoles(room.getRoles());
            roomMapper.updateById(old);
            config.sendAll(new Message.Roles(old.getRoles()));
        } else {
            ROOM_MAP.put(id, new RoomConfig(room));
            roomMapper.insert(room);
            HisMsgService.accept(id, HisMsgMapper::initTable);
        }
        return true;
    }

    public <T extends Message.Msg> void saveMsgAndSend(RoomConfig room, T msg, Long role) {
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

    public ResponseEntity<Resource> exportHistoryMsg(String id) {
        val fileName = id + ".zip";
        val config = getById(id);
        if (config == null) {
            return ResponseEntity.ok(null);
        }
        val roles = config.getRoles();
        val file = new File(fileName);
        try (val it = new ZipOutputStream(new FileOutputStream(file))) {
            it.setComment("导出历史记录");
            it.setLevel(9);
            it.putNextEntry(new ZipEntry("index.html"));
            val writer = new BufferedWriter(new OutputStreamWriter(it));
            val allCount = HisMsgService.apply(id, HisMsgMapper::count);
            val size = 10L;
            var index = 0L;
            while (index >= allCount) {
                val i = index;
                val list = HisMsgService.apply(id, hisMsgMapper -> hisMsgMapper.listAll(i, size));
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
        val headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }

    public void handleMessage(RoomConfig roomConfig, String id, Message msg) {
        val role = roomConfig.getRole(id);
        if (role == null) {
            return;
        }
        log.info("handleMessage: {}", role);
        if (msg instanceof Message.Default defMsg) {
            // 更新角色，根据id获取历史消息
            roomConfig.setRole(id, defMsg.getRole());
            val list = HisMsgService.apply(roomConfig.getId(),
                    hisMsgMapper -> hisMsgMapper.historyMsg(defMsg.getId(), 20));
            for (val hisMsg : list) {
                roomConfig.send(id, toMessage(hisMsg));
            }
        } else if (msg instanceof Message.Msg message) {
            val roleId = role.getId();
            saveMsgAndSend(roomConfig, message, roleId);
            if (msg instanceof Message.Text text) {
                val b = !roleId.equals(RoomConfig.BOT_ROLE) && text.getId() == null && text.getMsg().startsWith(".");
                if (b) {
                    HisMsgService.handleBot(roomConfig, id, text.getMsg().substring(1).trim());
                }
            }
        }
    }

    private Message toMessage(HisMsg hisMsg) {
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

    private String toHtml(String type, String msg, RoomRole role) {
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
