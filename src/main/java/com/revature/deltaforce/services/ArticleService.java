package com.revature.deltaforce.services;



import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.ExternalAPIArticle;

import com.revature.deltaforce.datasources.repositories.ArticleRepository;
import com.revature.deltaforce.util.exceptions.ExternalDataSourceException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
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

    // For handling incoming search response
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


    public DeltaArticle addComment(CommentDTO newComment){
        DeltaArticle deltaArticle = newComment.getDeltaArticle();
        if(deltaArticle.getContent().trim().equals("")||deltaArticle.getContent()==null)
            throw new InvalidRequestException("Comments cannot be empty!");

        deltaArticle.addComment(newComment.getComment());

        return articleRepo.save(deltaArticle);
    }

    public DeltaArticle removeComment(CommentDTO commentForRemoval){
        DeltaArticle article = commentForRemoval.getDeltaArticle();
        article.removeComment(commentForRemoval.getComment());
        return articleRepo.save(article);
    }

    // adds a username to the article's likes, removes username from dislikes if it is present
    public DeltaArticle addLike(String username, DeltaArticle likedArticle){
        likedArticle.getDislikes().remove(username);
        likedArticle.getLikes().add(username);
        return articleRepo.save(likedArticle);
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
