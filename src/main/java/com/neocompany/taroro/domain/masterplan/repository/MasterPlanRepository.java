package com.neocompany.taroro.domain.masterplan.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.masterplan.entity.MasterPlan;

public interface MasterPlanRepository extends JpaRepository<MasterPlan, Long> {

    Optional<MasterPlan> findByPlanIdAndDeletedFalse(Long planId);

    @Query("""
            SELECT p FROM MasterPlan p
            WHERE p.deleted = false
              AND p.isPublic = true
              AND (:masterId IS NULL OR p.masterId = :masterId)
              AND (:isActive IS NULL OR p.isActive = :isActive)
            ORDER BY p.createdAt DESC
            """)
    Slice<MasterPlan> findPublicPlans(
            @Param("masterId") Long masterId,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    @Query("""
            SELECT p FROM MasterPlan p
            WHERE p.masterId = :masterId
              AND p.deleted = false
            ORDER BY p.createdAt DESC
            """)
    Slice<MasterPlan> findByMasterIdAndDeletedFalse(
            @Param("masterId") Long masterId,
            Pageable pageable);
}
