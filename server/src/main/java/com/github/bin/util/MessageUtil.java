package com.github.bin.util;

import com.github.bin.entity.msg.HisMsg;
import com.github.bin.model.MessageOut;

/**
 * @author bin
 * @since 2023/09/12
 */
public interface MessageUtil {

    static MessageOut.Msg toMessage(HisMsg hisMsg) {
        return new MessageOut.Msg(hisMsg.getType().name(), hisMsg.getId(), hisMsg.getRole(), hisMsg.getMsg());
    }

}
