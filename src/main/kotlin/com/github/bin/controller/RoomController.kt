package com.github.bin.controller

import com.github.bin.entity.Room
import com.github.bin.model.IdAndName
import com.github.bin.service.RoomService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Tag(name = "room")
@RestController
@RequestMapping("/api")
class RoomController(
        private val roomService: RoomService,
) {
    @Operation(summary = "获取房间列表")
    @GetMapping("/rooms")
    fun rooms(): List<IdAndName> {
        return roomService.rooms().map {
            IdAndName(it.id!!, it.name!!)
        }
    }

    @Operation(summary = "获取房间信息")
    @GetMapping("/room")
    fun getRoom(@RequestParam id: String): Room? {
        return roomService.getById(id)
    }

    @Operation(summary = "创建/更新房间")
    @PostMapping("/room")
    fun postRoom(@Valid @RequestBody room: Room): Boolean {
        return roomService.saveOrUpdate(room)
    }

    @Operation(summary = "删除房间")
    @GetMapping("/room/del")
    fun deleteRoom(@RequestParam id: String): Boolean {
        return roomService.removeById(id)
    }

    @Operation(summary = "导出房间聊天记录")
    @GetMapping("/room/logs")
    fun getRoomLogs(@RequestParam id: String, response: HttpServletResponse): ResponseEntity<out Resource> {
        return roomService.exportHistoryMsg(id, response)
    }

}
