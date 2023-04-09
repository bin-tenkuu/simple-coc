package com.github.bin.config.handler

import com.github.bin.entity.Role
import org.springframework.stereotype.Component

/**
 *  @Date:2023/4/9
 *  @author bin
 *  @version 1.0.0
 */
@Component
class RoomHandler : BaseJsonTypeHandler<Map<String, Role>>()
