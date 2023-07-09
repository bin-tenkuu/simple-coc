package com.github.bin.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("room_role", resultMap = "BaseResultMap")
class RoomRole() {
    @TableId("id", type = IdType.AUTO)
    var id: Int? = null

    @TableField("name")
    var name: String? = null

    @TableField("color")
    var color: String? = null

    constructor(id: Int, name: String, color: String) : this() {
        this.id = id
        this.name = name
        this.color = color
    }
}
