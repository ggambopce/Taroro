package com.neocompany.taroro.domain.tarocardset.entity;

import java.time.Instant;

import com.neocompany.taroro.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taro_card_set")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaroCardSet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long setId;

    @Column(nullable = false)
    private Long masterId;

    @Column(nullable = false, length = 100)
    private String setName;

    @Column(columnDefinition = "TEXT")
    private String setDescription;

    @Column(length = 100)
    private String brandName;

    @Column(length = 100)
    private String publisherName;

    @Column(length = 500)
    private String coverImageUrl;

    @Column
    private Integer cardCount;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column
    private Instant deletedAt;

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    public void update(String setName, String setDescription, String coverImageUrl,
                       Boolean isPublic, Boolean isActive) {
        if (setName != null) this.setName = setName;
        if (setDescription != null) this.setDescription = setDescription;
        if (coverImageUrl != null) this.coverImageUrl = coverImageUrl;
        if (isPublic != null) this.isPublic = isPublic;
        if (isActive != null) this.isActive = isActive;
    }

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = Instant.now();
    }

    public boolean isOwnedBy(Long masterId) {
        return this.masterId.equals(masterId);
    }
}
