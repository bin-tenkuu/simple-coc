package com.github.bin.service

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper
import com.github.bin.aspect.RedisValue
import com.github.bin.entity.msg.HisMsg
import com.github.bin.entity.master.Room
import com.github.bin.entity.master.RoomRole
import com.github.bin.mapper.master.RoomMapper
import com.github.bin.model.Message
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Service
class RoomService(
    private val baseMapper: RoomMapper,
    private val hisMsgService: HisMsgService,
) {
    companion object : HashMap<String, RoomConfig>()

    private val log = LoggerFactory.getLogger(this::class.java)

    @RedisValue(key = "Room:list")
    fun rooms(): List<Room> {
        return baseMapper.selectList(null)
    }

    fun getById(id: String): Room? {
        return baseMapper.selectById(id)
    }

    fun removeById(id: String): Boolean {
        RoomService.remove(id)?.apply {
            close()
            hisMsgService.invoke(this.id) { dropTable() }
        }
        return SqlHelper.retBool(baseMapper.deleteById(id))
    }

    @Transactional(rollbackFor = [Exception::class])
    fun saveOrUpdate(room: Room): Boolean {
        val id = room.id!!
        val config = RoomService[id]
        if (config != null) {
            val old = config.room
            old.name = room.name
            old.roles = room.roles
            baseMapper.updateById(old)
            config.sendAll(Message.Roles(old.roles))
        } else {
            RoomService[id] = RoomConfig(room)
            baseMapper.insert(room)
            hisMsgService.invoke(id) {
                initTable()
            }
        }
        return true
    }

    fun <T : Message.Msg> saveMsgAndSend(room: RoomConfig, msg: T, role: Long) {
        msg.role = role
        hisMsgService.invoke(room.id) {
            if (msg.id == null) {
                msg.id = insert(msg.type, msg.msg, role)
            } else {
                update(msg.id!!, msg.msg, role)
            }
        }
        room.sendAll(msg)
    }

    fun exportHistoryMsg(id: String, response: HttpServletResponse): ResponseEntity<Resource> {
        val fileName = "$id.zip"
        val config = getById(id) ?: return ResponseEntity.ok(null)
        val roles = config.roles
        val file = File(fileName)
        ZipOutputStream(file.outputStream()).use {
            it.setComment("导出历史记录")
            it.setLevel(9)
            it.putNextEntry(ZipEntry("index.html"))
            val writer = it.bufferedWriter()
            val allCount = hisMsgService.invoke(id) { count() }
            val size = 10L
            var index = 0L
            while (index >= allCount) {
                val list = hisMsgService.invoke(id) { listAll(index, size) }
                for (msg in list) {
                    val roleId = msg.role!!
                    val role = roles[roleId] ?: RoomRole(roleId, roleId.toString(), "black")
                    val color = role.color
                    writer.append("<div style=\"color: ").append(color).append("\">")
                    writer.append(toHtml(msg.type!!, msg.msg!!, role))
                    writer.append("</div>\n")
                    writer.flush()
                }
                index += size
            }
            it.closeEntry()
            it.flush()
        }
        val headers = HttpHeaders()
        headers.add("Content-Disposition", "attachment; filename=$fileName")
        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(FileSystemResource(file))
    }

    fun handleMessage(roomConfig: RoomConfig, id: String, msg: Message) {
        val role = roomConfig.getRole(id)
        log.info("handleMessage: $role")
        when (msg) {
            is Message.Default -> {
                // 更新角色，根据id获取历史消息
                roomConfig.setRole(id, msg.role)
                val list = hisMsgService.invoke(roomConfig.id) { historyMsg(msg.id, 20) }
                for (hisMsg in list) {
                    roomConfig.send(id, hisMsg.toMessage())
                }
            }

            is Message.Text -> {
                val b = role != -10L && msg.id == null
                saveMsgAndSend(roomConfig, msg, role)
                if (b && msg.msg.startsWith('.')) {
                    hisMsgService.handleBot(roomConfig, id, msg.msg.substring(1).trim())
                }
            }

            is Message.Msg -> saveMsgAndSend(roomConfig, msg, role)
            else -> {}
        }
    }

    private fun HisMsg.toMessage(): Message {
        val id = id!!
        val msg = msg!!
        val role = role!!
        return when (type) {
            Message.TEXT -> Message.Text(id, role, msg)
            Message.PIC -> Message.Pic(id, role, msg)
            Message.SYS -> Message.Sys(id, role, msg)
            else -> Message.Msgs()
        }
    }

    private fun StringBuilder.toHtml(type: String, msg: String, role: RoomRole) {
        val name = role.name
        val user = "<span>&lt;${name}&gt;:</span>"
        when (type) {
            Message.TEXT -> append(user).append("<span>").append(msg).append("</span>")
            Message.PIC -> append(user).append("<img alt='img' src='").append(msg).append("'/>")
            Message.SYS -> append("<i>").append(msg).append("</i>")
            else -> {}
        }
    }

    private fun toHtml(type: String, msg: String, role: RoomRole): String {
        return buildString {
            toHtml(type, msg, role)
        }
    }
}
