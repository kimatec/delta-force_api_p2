package com.revature.deltaforce.datasources.models;

import com.revature.deltaforce.web.dtos.Source;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Data
@Document(collection = "articles")
@NoArgsConstructor
public class DeltaArticle implements Comparable<DeltaArticle>{

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

    public void removeComment(Comment comment) { this.comments.remove(comment);}


    @Override
    public int compareTo(DeltaArticle deltaArticle) {
        return Comparator.comparing(DeltaArticle::getLikes, (a1,a2) -> Integer.compare(a2.size(), a1.size()))
                .thenComparing(DeltaArticle::getComments, (c1,c2) -> Integer.compare(c2.size(), c1.size()))
                .compare(this, deltaArticle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeltaArticle that = (DeltaArticle) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
