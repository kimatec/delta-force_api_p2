package com.revature.deltaforce.services;



import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.ExternalAPIArticle;

import com.revature.deltaforce.datasources.repositories.ArticleRepository;
import com.revature.deltaforce.util.exceptions.ExternalDataSourceException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import com.revature.deltaforce.util.exceptions.ResourceNotFoundException;
import com.revature.deltaforce.web.dtos.CommentDTO;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ArticleRepository articleRepo;

    @Autowired
    public ArticleService(ArticleRepository articleRepo){
        this.articleRepo = articleRepo;
    }

//    Takes in a list of articles, saves all articles that are not already saved to our api database,
//    then returns the initial list of articles.

    public List<DeltaArticle> newsResponseHandler(List<ExternalAPIArticle> externalAPIArticles){

        if(externalAPIArticles.isEmpty())
        {
            throw new ExternalDataSourceException("Bad Response: No articles received");
        }
        List<DeltaArticle> requestedArticles = externalAPIArticles.stream()
                                                            .map(DeltaArticle::new)
                                                            .collect(Collectors.toList());

        List<URL> deltaArticleUrls= externalAPIArticles.stream()
                                                        .map(DeltaArticle::new)
                                                        .map(article -> article.getUrl())
                                                        .collect(Collectors.toList());
        List<DeltaArticle> existingArticles = articleRepo.findDeltaArticleByUrl(deltaArticleUrls);
        logger.error("NUMBER OF EXISTING ARTICLES: " + existingArticles.size());
        List<DeltaArticle> filteredArticles = requestedArticles.stream()
                                                            .filter(article -> !existingArticles.contains(article))
                                                            .collect(Collectors.toList());
        logger.error("NUMBER OF FILTERED ARTICLES: " + filteredArticles.size());
        articleRepo.saveAll(filteredArticles);

        return requestedArticles;
        }


    //Finds article by ID, adds comment to article, saves the article, then returns the updated article
    public DeltaArticle addComment(Comment comment, String articleId){
        DeltaArticle deltaArticle = articleRepo.findArticleById(articleId);
        if(deltaArticle.getContent().trim().equals("")||deltaArticle.getContent()==null)
            throw new InvalidRequestException("Comments cannot be empty!");

        deltaArticle.addComment(comment);
        articleRepo.save(deltaArticle);
        return deltaArticle;
    }

    public DeltaArticle removeComment(Comment comment, String articleId){
        DeltaArticle deltaArticle = articleRepo.findArticleById(articleId);
        if(!deltaArticle.getComments().contains(comment))
            throw new ResourceNotFoundException("Comment not found.");
        deltaArticle.removeComment(comment);
        articleRepo.save(deltaArticle);
        return deltaArticle;
    }

    // adds a username to the article's likes, removes username from dislikes if it is present
    public DeltaArticle addLike(String username, String articleId){
        DeltaArticle deltaArticle = articleRepo.findArticleById(articleId);
        if(deltaArticle.getLikes().contains(username))
            deltaArticle.getLikes().remove(username);
        else
           deltaArticle.getLikes().add(username);
        deltaArticle.getDislikes().remove(username);
        return articleRepo.save(deltaArticle);
    }

    // adds username to the article's dislikes, removes username from likes if it is present
    public DeltaArticle addDislike(String username, DeltaArticle likedArticle){
        likedArticle.getLikes().remove(username);
        likedArticle.getDislikes().add(username);
        return articleRepo.save(likedArticle);
    }

    public DeltaArticle removeLike(String username, DeltaArticle likedArticle){
        likedArticle.getLikes().remove(username);
        return articleRepo.save(likedArticle);
    }

    public DeltaArticle removeDislike(String username, DeltaArticle likedArticle){
        likedArticle.getDislikes().remove(username);
        return articleRepo.save(likedArticle);
    }

}
