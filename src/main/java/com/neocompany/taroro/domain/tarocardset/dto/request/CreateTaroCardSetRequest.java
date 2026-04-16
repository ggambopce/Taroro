package com.neocompany.taroro.domain.tarocardset.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTaroCardSetRequest {
    private String setName;
    private String setDescription;
    private String brandName;
    private String publisherName;
    private String coverImageUrl;
    private Integer cardCount;
    private Boolean isPublic;
    private Boolean isActive;
}
