package com.github.bin.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.bin.entity.RoomRole

/**
 *  @Date:2023/3/11
 *  @author bin
 *  @version 1.0.0
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Message.Default::class, name = Message.DEFAULT),
    JsonSubTypes.Type(value = Message.Text::class, name = Message.TEXT),
    JsonSubTypes.Type(value = Message.Pic::class, name = Message.PIC),
    JsonSubTypes.Type(value = Message.Sys::class, name = Message.SYS),
    JsonSubTypes.Type(value = Message.Msgs::class, name = Message.MSGS),
    JsonSubTypes.Type(value = Message.Roles::class, name = Message.ROLES),
    failOnRepeatedNames = true
)
sealed interface Message {

    companion object {
        const val DEFAULT = "default"
        const val TEXT = "text"
        const val PIC = "pic"
        const val SYS = "sys"
        const val MSGS = "msgs"
        const val ROLES = "roles"
    }

    sealed interface Msg : Message {
        val type: String
        var id: Long?
        var role: Long
        var msg: String
    }

    class Default : Message {
        var id: Long? = null
        var role: Long = -1L
    }

    class Text() : Message, Msg {
        override val type: String get() = TEXT
        override var id: Long? = null
        override var role: Long = -1
        override var msg: String = ""

        constructor(id: Long, msg: String, role: Long) : this() {
            this.id = id
            this.role = role
            this.msg = msg
        }
    }

    class Pic() : Message, Msg {
        override val type: String get() = PIC
        override var id: Long? = null
        override var role: Long = -1
        override var msg: String = ""

        constructor(id: Long, msg: String, role: Long) : this() {
            this.id = id
            this.role = role
            this.msg = msg
        }
    }

    class Sys() : Message, Msg {
        override val type: String get() = SYS
        override var id: Long? = null
        override var role: Long = -1
        override var msg: String = ""

        constructor(id: Long, msg: String, role: Long) : this() {
            this.id = id
            this.role = role
            this.msg = msg
        }
    }

    class Msgs(val msgs: List<Message> = ArrayList(0)) : Message

    class Roles(val roles: MutableMap<Long, RoomRole>) : Message
}
