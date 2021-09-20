package com.revature.deltaforce.datasources.repositories;

import com.revature.deltaforce.datasources.models.DeltaArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<DeltaArticle, String> {
    @Query("{'url' : {$in: ?0 } }")
    List<DeltaArticle> findDeltaArticleByUrl(List<URL> urls);
    DeltaArticle findArticleByUrl(URL url);
    DeltaArticle findArticleById(String id);
    DeltaArticle deleteDeltaArticleByUrl(URL url);
    DeltaArticle findAllByUrl(ArrayList<URL> urls);
    // Gets all articles that contain a provided username
    @Query("{$or: [ { likes : ?0 }, { dislikes : ?0  },{ 'comments.username' : ?0 } ] }")
    List<DeltaArticle> findDeltaArticleByUsername(String username);
}
