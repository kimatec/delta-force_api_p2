package com.revature.deltaforce.datasources.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NewsResponse {

    private String status;
    private int totalResults;
    private List<ExternalAPIArticle> articles = new ArrayList<>();

}
