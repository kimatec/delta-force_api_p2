package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.web.dtos.CommentDTO;
import com.revature.deltaforce.web.util.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping(value = "/comment", consumes = "application/json", produces = "application/json")
    @Secured(allowedRoles = {})
    public DeltaArticle addComment(@RequestBody CommentDTO commentDTO){ return articleService.addComment(commentDTO);}



}
