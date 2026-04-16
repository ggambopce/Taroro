package com.neocompany.taroro.domain.taromaster.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTaroMasterRequest {
    private String displayName;
    private String intro;
    private String profileImageUrl;
    private List<String> specialties;
    private Integer careerYears;
    private Boolean isPublic;
}
