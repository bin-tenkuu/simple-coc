package com.github.bin.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

@TableName("his_msg", resultMap = "BaseResultMap")
class HisMsg {
    @TableId("id", type = IdType.AUTO)
    var id: Long? = null
    var type: String? = null
    var msg: String? = null
    var role: Int? = null
}
