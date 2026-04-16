package com.neocompany.taroro.domain.taromaster.entity;

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
@Table(name = "taro_master")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaroMaster extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long masterId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String intro;

    @Column(length = 500)
    private String profileImageUrl;

    @Convert(converter = StringListConverter.class)
    @Column(name = "specialties", columnDefinition = "TEXT")
    private List<String> specialties;

    @Column
    private Integer careerYears;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MasterStatus status = MasterStatus.OFFLINE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    // ── 도메인 메서드 ─────────────────────────────────────────────────────────

    public void updateStatus(MasterStatus newStatus) {
        this.status = newStatus;
    }

    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVED;
    }

    public void reject() {
        this.approvalStatus = ApprovalStatus.REJECTED;
    }

    public void updateProfile(String displayName, String intro, String profileImageUrl,
                              List<String> specialties, Integer careerYears, Boolean isPublic) {
        if (displayName != null) this.displayName = displayName;
        if (intro != null) this.intro = intro;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
        if (specialties != null) this.specialties = specialties;
        if (careerYears != null) this.careerYears = careerYears;
        if (isPublic != null) this.isPublic = isPublic;
    }

    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isVisible(Long requesterId, boolean isAdmin) {
        if (isPublic && approvalStatus == ApprovalStatus.APPROVED) return true;
        if (isAdmin) return true;
        return this.userId.equals(requesterId);
    }
}
