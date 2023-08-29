package com.github.bin.entity.master;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author bin
 * @since 2023/08/22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRole {
    private int id;

    @TableField("name")
    private String name;

    @TableField("color")
    private String color;

    public RoomRole copy(int id) {
        return new RoomRole(id, name, color);
    }

    @Override
    public String toString() {
        return "RoomRole(" +
                "id=" + id +
                ')';
    }
}
