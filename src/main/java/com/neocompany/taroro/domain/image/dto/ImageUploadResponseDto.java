package com.neocompany.taroro.domain.image.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ImageUploadResponseDto {

    private List<Item> items;

    @Getter
    @AllArgsConstructor
    public static class Item {
        private String originalName;
        private String key;
        private String url;
    }
}
