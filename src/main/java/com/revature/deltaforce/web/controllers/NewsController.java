package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.ExternalAPIArticle;
import com.revature.deltaforce.datasources.models.NewsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Value("${api.key}")
    private String apiKey;

    private final String newsServiceUrl = "https://newsapi.org/v2/";

    @Autowired
    private RestTemplate restClient;

    // example: http://localhost:5000/news
    @GetMapping
    public List<ExternalAPIArticle> getNews() {
        String url = newsServiceUrl + "top-headlines?country=us&category=business&apiKey=" + apiKey;
        NewsResponse newsResponse = restClient.getForObject(url, NewsResponse.class);
        return newsResponse.getArticles();
    }

    // example: http://localhost:5000/news/q?search=tech
    @GetMapping("/q")
    public List<ExternalAPIArticle> searchNews(@RequestParam(required = true) String search) {
        String url = newsServiceUrl + "everything?q=" + search + "&apiKey=" + apiKey;
        NewsResponse newsResponse = restClient.getForObject(url, NewsResponse.class);
        return newsResponse.getArticles();
    }
}
