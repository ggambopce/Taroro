package com.neocompany.taroro.domain.room.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.room.entity.Room;
import com.neocompany.taroro.domain.room.entity.RoomStatus;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE (r.userId = :userId OR r.masterId = :userId) ORDER BY r.createdAt DESC")
    Slice<Room> findMyRooms(@Param("userId") Long userId, Pageable pageable);

    Slice<Room> findByStatusOrderByCreatedAtDesc(RoomStatus status, Pageable pageable);

    long countByStatus(RoomStatus status);
}
