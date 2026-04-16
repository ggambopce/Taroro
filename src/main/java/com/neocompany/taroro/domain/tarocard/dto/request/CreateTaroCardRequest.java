package com.neocompany.taroro.domain.tarocard.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTaroCardRequest {
    private Long setId;
    private String cardName;
    private Integer cardNumber;
    private String arcanaType;
    private String suit;
    private List<String> keywords;
    private String cardDescription;
    private String uprightMeaning;
    private String reversedMeaning;
    private String imageUrl;
    private Boolean isActive;
}
