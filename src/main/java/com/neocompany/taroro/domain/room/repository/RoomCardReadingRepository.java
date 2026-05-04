package com.neocompany.taroro.domain.room.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.room.entity.RoomCardReading;

public interface RoomCardReadingRepository extends JpaRepository<RoomCardReading, Long> {

    List<RoomCardReading> findByRoomIdOrderByPosition(Long roomId);

    Optional<RoomCardReading> findByRoomIdAndPosition(Long roomId, Integer position);

    @Modifying
    @Query("DELETE FROM RoomCardReading r WHERE r.roomId = :roomId")
    void deleteAllByRoomId(@Param("roomId") Long roomId);
}
