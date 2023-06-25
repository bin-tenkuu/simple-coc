package com.github.bin.controller

import com.github.bin.entity.Room
import com.github.bin.model.IdAndName
import com.github.bin.service.RoomService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
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
    @GetMapping("/rooms")
    fun rooms(
        @RequestParam name: String?
    ): List<IdAndName> {
        return roomService.rooms(name).map {
            IdAndName(it.id!!, it.name!!)
        }
    }

    @GetMapping("/room")
    fun getRoom(@RequestParam id: String): Room? {
        return roomService.getById(id)
    }

    @PostMapping("/room")
    fun postRoom(@Valid @RequestBody room: Room): Boolean {
        return roomService.saveOrUpdate(room)
    }

    @GetMapping("/room/del")
    fun deleteRoom(@RequestParam id: String): Boolean {
        return roomService.removeById(id)
    }

    @GetMapping("/room/logs")
    fun getRoomLogs(@RequestParam id: String, response: HttpServletResponse) {
        roomService.exportHistoryMsg(id, response)
    }

}
