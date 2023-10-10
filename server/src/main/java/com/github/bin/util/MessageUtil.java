package com.github.bin.util;

import com.github.bin.entity.msg.HisMsg;
import com.github.bin.model.Message;
import lombok.val;

/**
 * @author bin
 * @since 2023/09/12
 */
public interface MessageUtil {

    static Message toMessage(HisMsg hisMsg) {
        val id = hisMsg.getId();
        val msg = hisMsg.getMsg();
        val role = hisMsg.getRole();
        return hisMsg.getType().create(id, role, msg);
    }

}
