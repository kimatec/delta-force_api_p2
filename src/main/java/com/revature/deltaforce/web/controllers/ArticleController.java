package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.util.exceptions.AuthorizationException;
import com.revature.deltaforce.web.dtos.CommentDTO;
import com.revature.deltaforce.web.util.security.Secured;
import com.revature.deltaforce.web.util.security.SecurityAspect;
import com.revature.deltaforce.web.util.security.UserVerified;
import org.assertj.core.util.diff.Delta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.validation.Valid;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Value("${api.key}")
    private String apiKey;
    private final String newsServiceUrl = "https://newsapi.org/v2/";
    private final ArticleService articleService;
    private final RestTemplate restClient;

    @Autowired
    public ArticleController(ArticleService articleService, RestTemplate restClient){
        this.articleService = articleService;
        this.restClient = restClient;
    }

    @PostMapping(
            value = "/comment",
            params = {"id"},
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    public DeltaArticle addComment(@RequestBody Comment comment, @RequestParam("id") String articleId){ return articleService.addComment(comment, articleId);}


    @DeleteMapping(value = "/comment", consumes = "application/json", produces = "application/json")
    @Secured(allowedRoles = {})
    public DeltaArticle removeComment(@RequestBody Comment comment, @RequestParam("id") String articleId) {return articleService.removeComment(comment, articleId);}


}
