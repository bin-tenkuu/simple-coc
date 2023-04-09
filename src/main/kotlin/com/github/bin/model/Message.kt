package com.github.bin.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.bin.entity.Role
import kotlinx.serialization.SerialName

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
    JsonSubTypes.Type(value = Message.Msgs::class, name = Message.MSGS),
    JsonSubTypes.Type(value = Message.Roles::class, name = Message.ROLES),
    failOnRepeatedNames = true
)
sealed class Message {
    companion object {
        const val DEFAULT = "default"
        const val TEXT = "text"
        const val PIC = "pic"
        const val MSGS = "msgs"
        const val ROLES = "roles"
    }

    var id: Long? = null
    var role: String = ""

    class Default : Message()

    class Text(val msg: String) : Message() {
        constructor(id: Long, msg: String, role: String) : this(msg) {
            this.id = id
            this.role = role
        }
    }

    class Pic(val msg: String) : Message() {
        constructor(id: Long, msg: String, role: String) : this(msg) {
            this.id = id
            this.role = role
        }
    }

    class Msgs(val msgs: List<Message> = ArrayList(0)) : Message()

    class Roles(val roles: MutableMap<String, Role>) : Message()
}
