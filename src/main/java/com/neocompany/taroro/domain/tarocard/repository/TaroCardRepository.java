package com.neocompany.taroro.domain.tarocard.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neocompany.taroro.domain.tarocard.entity.TaroCard;

public interface TaroCardRepository extends JpaRepository<TaroCard, Long> {

    Optional<TaroCard> findByCardIdAndDeletedFalse(Long cardId);

    @Query("""
            SELECT c FROM TaroCard c
            WHERE c.setId = :setId
              AND c.deleted = false
              AND (:keyword IS NULL OR c.cardName LIKE %:keyword%)
              AND (:isActive IS NULL OR c.isActive = :isActive)
            ORDER BY c.cardNumber ASC
            """)
    Slice<TaroCard> findBySet(
            @Param("setId") Long setId,
            @Param("keyword") String keyword,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
}
