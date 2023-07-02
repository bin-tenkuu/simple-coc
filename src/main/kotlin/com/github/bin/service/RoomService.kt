package com.github.bin.service

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper
import com.github.bin.aspect.RedisValue
import com.github.bin.config.handler.MsgTableName
import com.github.bin.entity.Room
import com.github.bin.mapper.HisMsgMapper
import com.github.bin.mapper.RoomMapper
import com.github.bin.model.Message
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedWriter
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
        private val hisMsgMapper: HisMsgMapper,
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
            MsgTableName.invoke(room.id!!) {
                hisMsgMapper.dropTable()
            }
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
            MsgTableName.invoke(id) {
                hisMsgMapper.initTable()
            }
        }
        return true
    }

    fun saveMsgAndSend(room: RoomConfig, msg: Message, role: String) {
        msg.role = role
        MsgTableName.invoke(room.room.id!!) {
            when (msg) {
                is Message.Text -> {
                    if (msg.id == null) {
                        msg.id = hisMsgMapper.insert("text", msg.msg, role)
                    } else {
                        hisMsgMapper.update(msg.id!!, msg.msg, role)
                    }
                }
                is Message.Pic -> {
                    if (msg.id == null) {
                        msg.id = hisMsgMapper.insert("pic", msg.msg, role)
                    } else {
                        hisMsgMapper.update(msg.id!!, msg.msg, role)
                    }
                }
                else -> return
            }
        }
        room.sendAll(msg)
    }

    fun exportHistoryMsg(id: String, response: HttpServletResponse) {
        val config = RoomService[id] ?: return
        response.reset()
        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
        response.characterEncoding = "UTF-8"
        response.setHeader("Content-Disposition", "attachment; filename=$id.zip")
        val roles = config.room.roles
        val list = MsgTableName.invoke(id) { hisMsgMapper.listAll() }
        ZipOutputStream(response.outputStream).use {
            it.setLevel(9)
            it.setComment("聊天记录")
            it.putNextEntry(ZipEntry("index.html"))
            val stream = it.bufferedWriter()
            for (msg in list) {
                val role = roles[msg.role]
                if (role != null) {
//                    for (tag in role.tags) {
//                        stream.span(tag.name)
//                    }
                } else {
                    stream.span(msg.role!!)
                }
                stream.write(":")
                when (msg.type) {
                    Message.TEXT -> stream.span(msg.msg!!)
                    Message.PIC -> stream.img(msg.msg!!)
                }
                stream.write("<br/>\n")
            }
            stream.flush()
            it.closeEntry()
            it.flush()
        }
    }

    private fun BufferedWriter.span(txt: String) {
        write("<span>")
        write(txt)
        write("</span>")
    }

    private fun BufferedWriter.img(src: String) {
        write("<img src=\"")
        write(src)
        write("\"/>")
    }

    fun handleMessage(roomConfig: RoomConfig, id: String, msg: Message) {
        val role = roomConfig.getRole(id)
        log.info("handleMessage: $role")
    }
}
