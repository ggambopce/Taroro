package com.neocompany.taroro.domain.tarocardset.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.tarocardset.entity.TaroCardSet;

public interface TaroCardSetRepository extends JpaRepository<TaroCardSet, Long> {

    Optional<TaroCardSet> findBySetIdAndDeletedFalse(Long setId);

    Slice<TaroCardSet> findAllByMasterIdAndDeletedFalse(Long masterId, Pageable pageable);

    @Query("""
            SELECT s FROM TaroCardSet s
            WHERE s.deleted = false
              AND s.isPublic = true
              AND (:keyword IS NULL
                   OR s.setName LIKE %:keyword%
                   OR s.brandName LIKE %:keyword%)
              AND (:masterId IS NULL OR s.masterId = :masterId)
              AND (:isActive IS NULL OR s.isActive = :isActive)
            ORDER BY s.createdAt DESC
            """)
    Slice<TaroCardSet> findPublicSets(
            @Param("keyword") String keyword,
            @Param("masterId") Long masterId,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
}
