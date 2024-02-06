package com.github.bin.controller;

import com.github.bin.entity.master.Room;
import com.github.bin.model.IdAndName;
import com.github.bin.model.ResultModel;
import com.github.bin.model.login.LoginUser;
import com.github.bin.service.HisMsgService;
import com.github.bin.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
@Tag(name = "room")
@RestController
@RequestMapping("/api/room")
@AllArgsConstructor
@Slf4j
public class RoomController {

    @Operation(summary = "获取房间列表")
    @GetMapping("/list")
    public ResultModel<List<IdAndName>> rooms() {
        val list = RoomService.rooms();
        return ResultModel.success(list);
    }

    @Operation(summary = "获取房间信息")
    @GetMapping("/info")
    public ResultModel<Room> getRoom(@RequestParam String id) {
        val room = RoomService.getById(id);
        if (room == null) {
            return ResultModel.fail("房间不存在");
        }
        val roomUserId = room.getUserId();
        val userId = LoginUser.getUserId();
        val copy = new Room(room);
        copy.setEnable(roomUserId.equals(userId) || Room.ALL_USER.equals(roomUserId));
        return ResultModel.success(copy);
    }

    @Operation(summary = "创建/更新房间")
    @PostMapping("/info")
    public ResultModel<?> postRoom(@Valid @RequestBody Room room) {
        val userId = LoginUser.getUserId();
        val lastRoom = RoomService.getById(room.getId());
        if (lastRoom != null) {
            val lastRoomUserId = lastRoom.getUserId();
            if (!Room.ALL_USER.equals(lastRoomUserId)) {
                if (!userId.equals(lastRoomUserId)) {
                    return ResultModel.fail("无权限修改");
                }
            }
        }
        RoomService.saveOrUpdate(room);
        return ResultModel.success();
    }

    @Operation(summary = "删除房间")
    @GetMapping("/del")
    public ResultModel<?> deleteRoom(@RequestParam String id) {
        RoomService.removeById(id);
        return ResultModel.success();
    }

    @Operation(summary = "导出房间聊天记录")
    @GetMapping("/logs")
    public ResponseEntity<Resource> getRoomLogs(@RequestParam String id) {
        return RoomService.exportHistoryMsg(id);
    }

    @Operation(summary = "导出房间聊天记录原始数据")
    @GetMapping("/logs/db")
    public ResponseEntity<Resource> getRoomLogsDb(@RequestParam String id) {
        val dbUrl = HisMsgService.getDbUrl(id);
        val headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=hisMsg_" + id + ".db");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(dbUrl));
    }

}
