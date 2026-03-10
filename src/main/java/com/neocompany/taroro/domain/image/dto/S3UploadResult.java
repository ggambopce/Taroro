package com.neocompany.taroro.domain.image.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3UploadResult {
    private List<Item> items;

    @Getter
    @AllArgsConstructor
    public static class Item {
        private String originalName;
        private String key;
        private String url;
    }
}
