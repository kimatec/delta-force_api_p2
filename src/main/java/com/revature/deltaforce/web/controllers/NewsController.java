package com.revature.deltaforce.web.controllers;


import com.revature.deltaforce.datasources.models.ExternalAPIArticle;
import com.revature.deltaforce.datasources.models.NewsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Value("${api.key}")
    private String apiKey;

    private final String articleServiceUrl = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=";

    @Autowired
    private RestTemplate restClient;

    @GetMapping
    public List<ExternalAPIArticle> getNews() {
        NewsResponse newsResponse = restClient.getForObject(articleServiceUrl + apiKey, NewsResponse.class);
        return newsResponse.getArticles();
    }
}
