package com.neocompany.taroro.domain.tarocard.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTaroCardRequest {
    private String cardName;
    private List<String> keywords;
    private String cardDescription;
    private String uprightMeaning;
    private String reversedMeaning;
    private String imageUrl;
    private Boolean isActive;
}
