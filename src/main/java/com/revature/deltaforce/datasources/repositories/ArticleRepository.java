package com.revature.deltaforce.datasources.repositories;

import com.revature.deltaforce.datasources.models.DeltaArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.net.URL;


@Repository
public interface ArticleRepository extends MongoRepository<DeltaArticle, String> {
    DeltaArticle findDeltaArticleByUrl(URL url);
    DeltaArticle deleteDeltaArticleByUrl(URL url);
}
