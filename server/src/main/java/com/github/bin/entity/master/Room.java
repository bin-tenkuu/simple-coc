package com.github.bin.entity.master;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.bin.util.JsonUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bin
 * @since 2023/08/22
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "room",
        indexes = {
                @Index(name = "uk_room_id", columnList = "id", unique = true),
                @Index(name = "idx_room_user_id", columnList = "user_id")
        }
)
public class Room {
    @NotBlank
    @NotNull
    @Pattern(regexp = "^\\w+$", message = "房间ID只能包含字母、数字、下划线")
    @Id
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "roles", nullable = false, columnDefinition = "text")
    @Convert(converter = JpaConverterJson.class)
    private Map<Integer, RoomRole> roles = new HashMap<>();

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;
    public static final Long ALL_USER = 0L;

    @Column(name = "archive", nullable = false, insertable = false)
    private Boolean archive;

    @LastModifiedDate
    @Column(name = "update_date", nullable = false, insertable = false)
    private LocalDate updateDate;

    @Transient
    private Boolean enable;

    public void addRole(RoomRole role) {
        roles.put(role.getId(), role);
    }

    public Room(Room room) {
        this.id = room.id;
        this.name = room.name;
        this.roles = room.roles;
        this.userId = room.userId;
        this.archive = room.archive;
        this.updateDate = room.updateDate;
        this.enable = room.enable;
    }

    public static final class JpaConverterJson implements AttributeConverter<Map<Integer, RoomRole>, String> {

        @Override
        public String convertToDatabaseColumn(Map<Integer, RoomRole> meta) {
            return JsonUtil.toJson(meta);
        }

        @Override
        public Map<Integer, RoomRole> convertToEntityAttribute(String dbData) {
            return JsonUtil.toBean(dbData, new TypeReference<>() {
            });
        }

    }
}

