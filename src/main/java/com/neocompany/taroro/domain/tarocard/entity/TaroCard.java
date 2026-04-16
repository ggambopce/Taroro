package com.neocompany.taroro.domain.tarocard.entity;

import java.time.Instant;
import java.util.List;

import com.neocompany.taroro.global.converter.StringListConverter;
import com.neocompany.taroro.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "taro_card")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaroCard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(nullable = false)
    private Long setId;

    @Column(nullable = false)
    private Long masterId;

    @Column(nullable = false, length = 100)
    private String cardName;

    @Column
    private Integer cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ArcanaType arcanaType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SuitType suit;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> keywords;

    @Column(columnDefinition = "TEXT")
    private String cardDescription;

    @Column(columnDefinition = "TEXT")
    private String uprightMeaning;

    @Column(columnDefinition = "TEXT")
    private String reversedMeaning;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column
    private Instant deletedAt;

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    public void update(String cardName, List<String> keywords, String cardDescription,
                       String uprightMeaning, String reversedMeaning,
                       String imageUrl, Boolean isActive) {
        if (cardName != null) this.cardName = cardName;
        if (keywords != null) this.keywords = keywords;
        if (cardDescription != null) this.cardDescription = cardDescription;
        if (uprightMeaning != null) this.uprightMeaning = uprightMeaning;
        if (reversedMeaning != null) this.reversedMeaning = reversedMeaning;
        if (imageUrl != null) this.imageUrl = imageUrl;
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
