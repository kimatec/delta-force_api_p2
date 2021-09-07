package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.NewsResponse;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.web.util.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

//TODO: Add auth to prevent web crawlers from pinging this and using up our key limit
@RestController
@RequestMapping("/news")
public class NewsController {

    @Value("${api.key}")
    private String apiKey;

    private final String newsServiceUrl = "https://newsapi.org/v2/";

    private final ArticleService articleService;
    private final RestTemplate restClient;

    @Autowired
    public NewsController(ArticleService articleService, RestTemplate restClient) {
        this.articleService = articleService;
        this.restClient = restClient;
    }

    // example: http://localhost:5000/news
    @GetMapping
    @Secured(allowedRoles = {})
    public List<DeltaArticle> getNews() {
        String url = newsServiceUrl + "top-headlines?country=us&category=business&apiKey=" + apiKey;
        NewsResponse newsResponse = restClient.getForObject(url, NewsResponse.class);
        return articleService.newsResponseHandler(newsResponse.getArticles());
    }

    // example: http://localhost:5000/news/q?search=tech
    @GetMapping("/q")
    @Secured(allowedRoles = {})
    public List<DeltaArticle> searchNews(@RequestParam(required = true) String search) {
        String url = newsServiceUrl + "everything?q=" + search + "&apiKey=" + apiKey;
        NewsResponse newsResponse = restClient.getForObject(url, NewsResponse.class);
        return articleService.newsResponseHandler(newsResponse.getArticles());
    }
}
