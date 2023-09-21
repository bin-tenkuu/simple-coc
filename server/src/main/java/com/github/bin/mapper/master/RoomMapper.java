package com.github.bin.mapper.master;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.bin.entity.master.Room;
import com.github.bin.model.IdAndName;

import java.util.List;

/**
 * @author bin
 * @since 2023/08/22
 */
public interface RoomMapper extends BaseMapper<Room> {
    List<IdAndName> listIdAndName();
}
