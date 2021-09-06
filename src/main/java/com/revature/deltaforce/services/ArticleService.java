package com.revature.deltaforce.services;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.ExternalAPIArticle;
import com.revature.deltaforce.datasources.models.NewsResponse;
import com.revature.deltaforce.datasources.repositories.ArticleRepository;
import com.revature.deltaforce.util.exceptions.ExternalDataSourceException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import org.assertj.core.util.diff.Delta;
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

        // TODO: There's gotta be a better way to do this, probably involving creating a query and using the
        //  $or operator with an array of URLs.
        deltaArticles.forEach(article -> {
            DeltaArticle savedArticle = articleRepo.findDeltaArticleByUrl(article.getUrl());
            if (savedArticle != null) deltaArticles.set(deltaArticles.indexOf(article), savedArticle);
        });

        return deltaArticles;
    }

    public DeltaArticle addComment(Comment newComment, DeltaArticle article){

        if(newComment.getContent().trim().equals("")||newComment.getContent()==null)
            throw new InvalidRequestException("Comments cannot be empty!");

        article.addComment(newComment);

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
