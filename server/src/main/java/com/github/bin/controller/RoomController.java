package com.github.bin.controller;

import com.github.bin.config.MsgDataSource;
import com.github.bin.entity.master.Room;
import com.github.bin.model.IdAndName;
import com.github.bin.model.ResultModel;
import com.github.bin.model.login.LoginUser;
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
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class RoomController {

    @Operation(summary = "获取房间列表")
    @GetMapping("/rooms")
    public ResultModel<List<IdAndName>> rooms() {
        val list = RoomService.rooms();
        return ResultModel.success(list);
    }

    @Operation(summary = "获取房间信息")
    @GetMapping("/room")
    public ResultModel<Room> getRoom(@RequestParam String id) {
        val room = RoomService.getById(id);
        if (room == null) {
            return ResultModel.fail("房间不存在");
        }
        val roomUserId = room.getUserId();
        val userId = LoginUser.getUserId();
        if (Room.ALL_USER.equals(roomUserId) || userId.equals(roomUserId)) {
            return ResultModel.success(room);
        }
        return ResultModel.fail("房间不存在");
    }

    @Operation(summary = "创建/更新房间")
    @PostMapping("/room")
    public ResultModel<?> postRoom(@Valid @RequestBody Room room) {
        RoomService.saveOrUpdate(room);
        return ResultModel.success();
    }

    @Operation(summary = "删除房间")
    @GetMapping("/room/del")
    public ResultModel<?> deleteRoom(@RequestParam String id) {
        if (RoomService.removeById(id)) {
            return ResultModel.success();
        } else {
            return ResultModel.fail("删除失败");
        }
    }

    @Operation(summary = "导出房间聊天记录")
    @GetMapping("/room/logs")
    public ResponseEntity<Resource> getRoomLogs(@RequestParam String id) {
        return RoomService.exportHistoryMsg(id);
    }

    @Operation(summary = "导出房间聊天记录原始数据")
    @GetMapping("/room/logs/db")
    public ResponseEntity<Resource> getRoomLogsDb(@RequestParam String id) {
        val dbUrl = MsgDataSource.getDbUrl(id);
        val headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=hisMsg_" + id + ".db");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(dbUrl));
    }

}
