package com.revature.deltaforce.services;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.ExternalAPIArticle;
import com.revature.deltaforce.datasources.models.NewsResponse;
import com.revature.deltaforce.datasources.repositories.ArticleRepository;
import com.revature.deltaforce.util.exceptions.ExternalDataSourceException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {

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

        List<DeltaArticle> deltaArticles = externalAPIArticles.stream()
                .map(DeltaArticle::new).collect(Collectors.toList());

        //TODO - check database for articles that exist
        // only add articles to the database if/when they receive likes/dislikes/comments to prevent bloat

        return deltaArticles;
    }

    public DeltaArticle addComment(Comment newComment, DeltaArticle article){

        if(newComment.getContent().trim().equals("")||newComment.getContent()==null)
            throw new InvalidRequestException("Comments cannot be empty!");

        article.addComment(newComment);

        return articleRepo.save(article);
    }
}
