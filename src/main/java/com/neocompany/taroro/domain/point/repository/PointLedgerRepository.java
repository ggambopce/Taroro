package com.neocompany.taroro.domain.point.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.point.entity.PointLedger;

public interface PointLedgerRepository extends JpaRepository<PointLedger, Long> {

    @Query("SELECT l FROM PointLedger l WHERE l.user.userId = :userId ORDER BY l.id DESC")
    Slice<PointLedger> findByUserIdOrderByIdDesc(@Param("userId") Long userId, Pageable pageable);
}
