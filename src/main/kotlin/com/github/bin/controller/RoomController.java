package com.github.bin.controller;

import com.github.bin.entity.master.Room;
import com.github.bin.model.IdAndName;
import com.github.bin.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
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
public class RoomController {
    private final RoomService roomService;

    @Operation(summary = "获取房间列表")
    @GetMapping("/rooms")
    public List<IdAndName> rooms() {
        return roomService.rooms().stream()
                .map(room -> new IdAndName(room.getId(), room.getName()))
                .toList();
    }

    @Operation(summary = "获取房间信息")
    @GetMapping("/room")
    public Room getRoom(@RequestParam String id) {
        return roomService.getById(id);
    }

    @Operation(summary = "创建/更新房间")
    @PostMapping("/room")
    public boolean postRoom(@Valid @RequestBody Room room) {
        return roomService.saveOrUpdate(room);
    }

    @Operation(summary = "删除房间")
    @GetMapping("/room/del")
    public boolean deleteRoom(@RequestParam String id) {
        return roomService.removeById(id);
    }

    @Operation(summary = "导出房间聊天记录")
    @GetMapping("/room/logs")
    public ResponseEntity<Resource> getRoomLogs(@RequestParam String id, HttpServletResponse response) {
        return roomService.exportHistoryMsg(id, response);
    }

}
