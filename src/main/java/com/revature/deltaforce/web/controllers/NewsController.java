package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.NewsResponse;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.web.util.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/news")
public class NewsController {

    private static final String newsServiceUrl = "https://newsapi.org/v2/";
    private final ArticleService articleService;
    private final RestTemplate restClient;

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    public NewsController(ArticleService articleService, RestTemplate restClient) {
        this.articleService = articleService;
        this.restClient = restClient;
    }

    // example: http://localhost:5000/news/category/business
    @GetMapping(value = "category/{category}", produces = "application/json")
    @Secured(allowedRoles = {})
    public List<DeltaArticle> getNews(@PathVariable String category) {
        String url;
        if (category.equals("top"))
            url = newsServiceUrl + "top-headlines?country=us&apiKey=" + apiKey;
        else
            url = newsServiceUrl + "top-headlines?country=us&category=" + category + "&apiKey=" + apiKey;
        NewsResponse newsResponse = restClient.getForObject(url, NewsResponse.class);
        return articleService.newsResponseHandler(newsResponse.getArticles()).stream().sorted().collect(Collectors.toList());
    }

    // example: http://localhost:5000/news/q?search=tech
    @GetMapping("/q")
    @Secured(allowedRoles = {})
    public List<DeltaArticle> searchNews(@RequestParam(required = true) String search) {
        String url = newsServiceUrl + "everything?q=" + search + "&apiKey=" + apiKey;
        NewsResponse newsResponse = restClient.getForObject(url, NewsResponse.class);
        return articleService.newsResponseHandler(newsResponse.getArticles()).stream().sorted().collect(Collectors.toList());
    }

    // example: http://localhost:5000/news/popular
    @GetMapping("/popular")
    @Secured(allowedRoles = {})
    public List<DeltaArticle> popularArticles() {
        return articleService.getPopularArticles().stream().sorted().collect(Collectors.toList());
    }

    // Fetches 10 articles from each of your favorite topics, shuffles them, then returns 10 articles.
    @GetMapping("/dashboard")
    public List<DeltaArticle> favTopics(@RequestBody AppUser username) {
        List<String> favTopicUrls = articleService.getFavoriteUrls(username.getUsername());

        //If user has favTopics, get articles for each category and return a single list of all the Delta Articles. Otherwise,
        //get top headlines.
        List<DeltaArticle> favArticles = favTopicUrls.stream()
                .map(string -> newsServiceUrl + string + apiKey)
                .map(url -> restClient.getForObject(url, NewsResponse.class).getArticles())
                .map(articleService::newsResponseHandler)
                .flatMap(list -> list.stream())
                .sorted()
                .collect(Collectors.toList());

        //If user has favorite topics, shuffle the headlines so the feed will show articles from different categories.
        if (!favTopicUrls.contains("top-headlines?country=us&apiKey="))
            Collections.shuffle(favArticles);
        return favArticles.subList(0, 9);
    }
}
