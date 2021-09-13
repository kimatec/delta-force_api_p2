package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.NewsResponse;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.web.util.security.Secured;
import org.assertj.core.util.diff.Delta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    // example: http://localhost:5000/news/popular
    @GetMapping("/popular")
    @Secured(allowedRoles = {})
    public List<DeltaArticle> popularArticles(){return articleService.getPopularArticles();}

    // Fetches 10 articles from each of your favorite topics, shuffles them, then returns 10 articles.
    @GetMapping("/dashboard")
    public List<DeltaArticle> favTopics(@RequestBody AppUser username){
        List<String> favTopicUrls = articleService.getFavoriteUrls(username.getUsername());
        List<DeltaArticle> favArticles = favTopicUrls.stream()
                            .map(string -> newsServiceUrl+string+apiKey)
                            .map(url -> restClient.getForObject(url, NewsResponse.class))
                            .map(response -> articleService.newsResponseHandler(response.getArticles()))
                            .flatMap(list -> list.stream())
                            .collect(Collectors.toList());
        if(!favTopicUrls.contains("top-headlines?country=us&apiKey="))
            Collections.shuffle(favArticles);
        return favArticles.subList(0,9);
    }

}
