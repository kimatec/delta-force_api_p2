package com.revature.deltaforce.datasources.models;

import com.revature.deltaforce.web.dtos.Source;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "articles")
@NoArgsConstructor
public class DeltaArticle {

    private String id;
    private Source source;
    private String author;
    private String title;
    private String description;
    private URL url;
    private URL urlToImage;
    private LocalDateTime publishedAt;
    private String content;
    private List<String> likes = new ArrayList<>();
    private List<String> dislikes = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    // For wrapping up the original articles
    public DeltaArticle(ExternalAPIArticle externalAPIArticle){
        this.source = externalAPIArticle.getSource();
        this.author = externalAPIArticle.getAuthor();
        this.title = externalAPIArticle.getTitle();
        this.description = externalAPIArticle.getDescription();
        this.url = externalAPIArticle.getUrl();
        this.urlToImage = externalAPIArticle.getUrlToImage();
        this.publishedAt = externalAPIArticle.getPublishedAt();
        this.content = externalAPIArticle.getContent();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}
