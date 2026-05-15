package com.neocompany.taroro.domain.taromaster.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTaroMasterRequest {
    private String displayName;
    private String intro;
    private List<String> specialties;
    private Integer careerYears;
    private Boolean isPublic;

    // ── 정산 계좌 정보 (선택) ──
    // 5개 필드 모두 입력 시 MasterSettlement 자동 생성.
    // 누락 시 등록만 진행, 정산 정보는 /api/master-auth/settlement 로 별도 등록 가능.
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String phone;
    private String email;

    public boolean hasSettlementInfo() {
        return isNotBlank(bankName)
                && isNotBlank(accountNumber)
                && isNotBlank(accountHolderName)
                && isNotBlank(phone)
                && isNotBlank(email);
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }
}
