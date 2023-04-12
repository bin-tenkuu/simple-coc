package com.github.bin.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper
import com.github.bin.config.handler.MsgTableName
import com.github.bin.controller.WebSocketHandler
import com.github.bin.entity.Room
import com.github.bin.mapper.HisMsgMapper
import com.github.bin.mapper.RoomMapper
import com.github.bin.model.Message
import com.github.bin.model.RoomConfig
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.InitializingBean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
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
    private val hisMsgMapper: HisMsgMapper
) : InitializingBean {
    override fun afterPropertiesSet() {
        for (room in baseMapper.selectList(Wrappers.emptyWrapper())) {
            RoomService[room.id!!] = RoomConfig(room)
        }
        WebSocketHandler.roomService = this
    }

    fun rooms(name: String?): List<Room> {
        return baseMapper.selectList(
            if (name != null) QueryWrapper<Room>().like("name", name)
            else Wrappers.emptyWrapper()
        )
    }

    fun getById(id: String): Room {
        return baseMapper.selectById(id)
    }

    fun removeById(id: String): Boolean {
        RoomService.remove(id)?.apply {
            for (client in clients) {
                client.close()
            }
            clients.clear()
            MsgTableName.invoke(room.id!!) {
                hisMsgMapper.dropTable()
            }
        }
        return SqlHelper.retBool(baseMapper.deleteById(id))
    }

    fun saveOrUpdate(room: Room): Boolean {
        val config = RoomService[room.id!!]
        val code: Int
        if (config != null) {
            val old = config.room
            old.name = room.name
            old.roles = room.roles
            code = baseMapper.updateById(old)
            config.sendAll(Message.Roles(old.roles))
        } else {
            RoomService[room.id!!] = RoomConfig(room)
            code = baseMapper.insert(room)
            MsgTableName.invoke(room.id!!) {
                hisMsgMapper.initTable()
            }
        }
        return SqlHelper.retBool(code)
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
                    for (tag in role.tags) {
                        stream.span(tag.name)
                    }
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



    companion object : HashMap<String, RoomConfig>()
}
