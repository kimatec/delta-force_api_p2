package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.util.exceptions.AuthorizationException;
import com.revature.deltaforce.web.dtos.CommentDTO;
import com.revature.deltaforce.web.util.security.Secured;
import com.revature.deltaforce.web.util.security.SecurityAspect;
import com.revature.deltaforce.web.util.security.UserVerified;
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

    @PostMapping(value = "/comment", consumes = "application/json", produces = "application/json")
    @Secured(allowedRoles = {})
    public DeltaArticle addComment(@RequestBody CommentDTO commentDTO){ return articleService.addComment(commentDTO);}

//    @DeleteMapping(value = "/comment", consumes = "application/json", produces = "application/json")
//    public String removeComment(@RequestBody CommentDTO commentDTO) {
//
//        if(!commentDTO.getComment().getUsername().equals(username))
//            throw new AuthorizationException("You can't delete a comment you didn't write!");
//        articleService.removeComment(commentDTO);
//        return "Comment removed successfully.";
//    }


}
