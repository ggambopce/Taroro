package com.neocompany.taroro.domain.taromaster.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.taromaster.entity.ApprovalStatus;
import com.neocompany.taroro.domain.taromaster.entity.MasterStatus;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;

public interface TaroMasterRepository extends JpaRepository<TaroMaster, Long> {

    Optional<TaroMaster> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("""
            SELECT m FROM TaroMaster m
            WHERE m.isPublic = true
              AND m.approvalStatus = :approvalStatus
              AND (:keyword IS NULL
                   OR m.displayName LIKE %:keyword%
                   OR m.intro LIKE %:keyword%)
              AND (:status IS NULL OR m.status = :status)
            ORDER BY m.createdAt DESC
            """)
    Slice<TaroMaster> findPublicMasters(
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("keyword") String keyword,
            @Param("status") MasterStatus status,
            Pageable pageable);
}
