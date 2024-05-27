package com.github.bin.entity.master;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

/**
 * @author bin
 * @since 2023/08/22
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class RoomRole {

    @ToString.Include
    @EqualsAndHashCode.Include
    private int id;

    @TableField("name")
    private String name;

    @TableField("color")
    private String color;

    public RoomRole copy(int id) {
        return new RoomRole(id, name, color);
    }

}
