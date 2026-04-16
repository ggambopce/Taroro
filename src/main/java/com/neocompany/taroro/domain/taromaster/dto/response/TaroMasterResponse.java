package com.neocompany.taroro.domain.taromaster.dto.response;

import java.time.Instant;
import java.util.List;

import com.neocompany.taroro.domain.taromaster.entity.ApprovalStatus;
import com.neocompany.taroro.domain.taromaster.entity.MasterStatus;
import com.neocompany.taroro.domain.taromaster.entity.TaroMaster;

import lombok.Getter;

@Getter
public class TaroMasterResponse {

    private final Long masterId;
    private final Long userId;
    private final String displayName;
    private final String intro;
    private final String profileImageUrl;
    private final List<String> specialties;
    private final Integer careerYears;
    private final MasterStatus status;
    private final ApprovalStatus approvalStatus;
    private final boolean isPublic;
    private final Instant createdAt;
    private final Instant updatedAt;

    public TaroMasterResponse(TaroMaster master) {
        this.masterId = master.getMasterId();
        this.userId = master.getUserId();
        this.displayName = master.getDisplayName();
        this.intro = master.getIntro();
        this.profileImageUrl = master.getProfileImageUrl();
        this.specialties = master.getSpecialties();
        this.careerYears = master.getCareerYears();
        this.status = master.getStatus();
        this.approvalStatus = master.getApprovalStatus();
        this.isPublic = master.isPublic();
        this.createdAt = master.getCreatedAt();
        this.updatedAt = master.getUpdatedAt();
    }
}
