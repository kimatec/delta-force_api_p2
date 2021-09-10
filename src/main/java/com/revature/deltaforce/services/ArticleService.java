package com.revature.deltaforce.services;



import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.ExternalAPIArticle;

import com.revature.deltaforce.datasources.repositories.ArticleRepository;
import com.revature.deltaforce.datasources.repositories.UserRepository;
import com.revature.deltaforce.util.exceptions.ExternalDataSourceException;
import com.revature.deltaforce.util.exceptions.ResourceNotFoundException;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepo;
    private final ArticleRepository articleRepo;

    @Autowired
    public ArticleService(UserRepository userRepo, ArticleRepository articleRepo){
        this.userRepo = userRepo;
        this.articleRepo = articleRepo;
    }

    /**
     * Takes in a list of articles and filters them down to 10, saves all articles that are not already saved to our api database,
     * then returns the initial list of articles.
     * @param externalAPIArticles A list of articles extracted from News API
     * @return
     */
    public List<DeltaArticle> newsResponseHandler(List<ExternalAPIArticle> externalAPIArticles){

        if(externalAPIArticles.isEmpty())
        {
            throw new ExternalDataSourceException("Bad Response: No articles received");
        }
        List<DeltaArticle> requestedArticles = externalAPIArticles.stream()
                                                            .limit(10)
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

        //The list of URLs for the requested articles, so that we can re-fetch articles from our database after saving.
        List<URL> requestedUrls = requestedArticles.stream()
                .map(article -> article.getUrl())
                .collect(Collectors.toList());

        return articleRepo.findDeltaArticleByUrl(requestedUrls).stream().distinct().collect(Collectors.toList());
        }


    /**
     * Adds comment to article given by articleId, then returns the updated article
     * @param comment The comment to be added
     * @return
     */
    public DeltaArticle addComment(Comment comment){
        DeltaArticle deltaArticle = articleRepo.findArticleByUrl(comment.getUrl());
        deltaArticle.addComment(comment);
        articleRepo.save(deltaArticle);
        return deltaArticle;
    }

    /**
     * Removes comment from article given by articleId, then returns the updated article
     * @param comment The comment to be removed
     * @return
     */
    public DeltaArticle removeComment(Comment comment){
        DeltaArticle deltaArticle = articleRepo.findArticleByUrl(comment.getUrl());
        if(!deltaArticle.getComments().contains(comment))
            throw new ResourceNotFoundException("Comment not found.");
        deltaArticle.removeComment(comment);
        articleRepo.save(deltaArticle);
        return deltaArticle;
    }

    /**
     * Adds a username to the article's likes, removes username from dislikes if it is present
     * @param username The username of the user liking the article
     * @param articleId The id of the article that's being liked
     * @return
     */
    public DeltaArticle addLike(String username, String articleId){
        DeltaArticle deltaArticle = articleRepo.findArticleById(articleId);
        if(deltaArticle.getLikes().contains(username))
            deltaArticle.getLikes().remove(username);
        else
           deltaArticle.getLikes().add(username);
        deltaArticle.getDislikes().remove(username);
        return articleRepo.save(deltaArticle);
    }

    /**
     * Adds username to the article's dislikes, removes username from likes if it is present
     * @param username The username of the user disliking the article
     * @param articleId Thie id of the article that's being disliked
     * @return
     */
    public DeltaArticle addDislike(String username, String articleId){
        DeltaArticle deltaArticle = articleRepo.findArticleById(articleId);
        if(deltaArticle.getDislikes().contains(username))
            deltaArticle.getDislikes().remove(username);
        else
            deltaArticle.getDislikes().add(username);
        deltaArticle.getLikes().remove(username);
        return articleRepo.save(deltaArticle);
    }

    /**
     * Returns ten most popular articles, based primarily on the number of likes, followed by number of comments.
     * @return
     */
    public List<DeltaArticle> getPopularArticles(){
       return articleRepo.findAll().stream()
                                   .sorted()
                                   .limit(10)
                                   .collect(Collectors.toList());
    }

    /**
     * Maps each of your favorite topics to the corresponding NewsAPI URL, then returns the list of URLs as Strings. If
     * the user has no favorite topics, the returned list will have  a URL for the top headlines in the country.
     *
     * @param username The username of the user requesting the articles.
     * @return
     */
    public List<String> getFavoriteUrls(String username) {
        AppUser user = userRepo.findAppUserByUsername(username);
        if (!user.getFavTopics().isEmpty()) {
            return user.getFavTopics().stream()
                                      .map(string -> "top-headlines?country=us&category=" + string + "&apiKey=")
                                      .collect(Collectors.toList());
        } else
            return new ArrayList<>(Arrays.asList("top-headlines?country=us&apiKey="));
    }
}
