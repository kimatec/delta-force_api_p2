package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.web.util.security.IsMyComment;
import com.revature.deltaforce.web.util.security.IsMyDislike;
import com.revature.deltaforce.web.util.security.IsMyLike;
import com.revature.deltaforce.web.util.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final String newsServiceUrl = "https://newsapi.org/v2/";
    private final ArticleService articleService;
    private final RestTemplate restClient;

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    public ArticleController(ArticleService articleService, RestTemplate restClient) {
        this.articleService = articleService;
        this.restClient = restClient;
    }

    @PostMapping(
            value = "/comment",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    public DeltaArticle addComment(@RequestBody @Valid Comment comment) {
        return articleService.addComment(comment);
    }

    // Example: /article/like?id=613ba397a7763649c6fa1ed7
    @PostMapping(
            value = "/like",
            params = {"id"},
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    @IsMyLike
    public DeltaArticle likeArticle(@RequestBody AppUser username, @RequestParam("id") String articleId) {
        return articleService.addLike(username.getUsername(), articleId);
    }

    // Example: /article/dislike?id=613ba397a7763649c6fa1ed7
    @PostMapping(
            value = "/dislike",
            params = {"id"},
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    @IsMyDislike
    public DeltaArticle dislikeArticle(@RequestBody AppUser username, @RequestParam("id") String articleId) {
        return articleService.addDislike(username.getUsername(), articleId);
    }

    @DeleteMapping(value = "/comment", consumes = "application/json", produces = "application/json")
    @Secured(allowedRoles = {})
    @IsMyComment
    public DeltaArticle removeComment(@RequestBody Comment comment) {
        return articleService.removeComment(comment);
    }
}
