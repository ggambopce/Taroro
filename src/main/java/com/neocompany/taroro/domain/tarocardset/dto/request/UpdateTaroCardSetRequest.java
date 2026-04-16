package com.neocompany.taroro.domain.tarocardset.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTaroCardSetRequest {
    private String setName;
    private String setDescription;
    private String coverImageUrl;
    private Boolean isPublic;
    private Boolean isActive;
}
