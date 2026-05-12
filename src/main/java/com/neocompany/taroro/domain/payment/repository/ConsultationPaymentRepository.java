package com.neocompany.taroro.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.payment.entity.ConsultationPayment;

public interface ConsultationPaymentRepository extends JpaRepository<ConsultationPayment, Long> {

    @Query("SELECT p FROM ConsultationPayment p WHERE p.room.id = :roomId")
    Optional<ConsultationPayment> findByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT (COUNT(p) > 0) FROM ConsultationPayment p WHERE p.room.id = :roomId")
    boolean existsByRoomId(@Param("roomId") Long roomId);

    Slice<ConsultationPayment> findByMasterIdOrderByIdDesc(Long masterId, Pageable pageable);

    Slice<ConsultationPayment> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
