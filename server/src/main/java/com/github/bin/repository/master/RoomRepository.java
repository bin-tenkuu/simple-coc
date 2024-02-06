package com.github.bin.repository.master;

import com.github.bin.entity.master.Room;
import com.github.bin.model.IdAndName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String>, JpaSpecificationExecutor<Room> {
    @Query("select new com.github.bin.model.IdAndName(r.id, r.name) from Room r")
    List<IdAndName> listIdAndName();
}
