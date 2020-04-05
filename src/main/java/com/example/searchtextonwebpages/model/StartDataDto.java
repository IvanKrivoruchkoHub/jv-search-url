package com.example.searchtextonwebpages.model;

import lombok.Data;

@Data
public class StartDataDto {
    private String startUrl;
    private String searchText;
    private Integer maxCountUrls;
    private Integer threadCount;
}
