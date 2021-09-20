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
import java.util.stream.Collectors;

@Data
@Document(collection = "articles")
@NoArgsConstructor
public class DeltaArticle implements Comparable<DeltaArticle> {

    private String id;
    private Source source;
    private String author;
    private String title;
    private String description;
    private URL url;
    private String urlToImage;
    private LocalDateTime publishedAt;
    private String content;
    private List<String> likes = new ArrayList<>();
    private List<String> dislikes = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    // For wrapping up the original articles
    public DeltaArticle(ExternalAPIArticle externalAPIArticle) {
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

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    public void updateComments(String oldUsername, String newUsername) {
        comments.stream()
                .filter(filter -> filter.getUsername().equals(oldUsername))
                .forEach(update -> update.setUsername(newUsername));
    }

    public void removeComments(String username) {
        comments.removeIf(comment -> comment.getUsername().equals(username));
    }

    public void updateLikes(String oldUsername, String newUsername) {
        List<String> updatedLikes = likes.stream().map(username -> username.replaceAll(oldUsername, newUsername))
                .collect(Collectors.toList());
        this.setLikes(updatedLikes);
    }

    public void updateDislikes(String oldUsername, String newUsername) {
        List<String> updatedDislikes = dislikes.stream().map(username -> username.replaceAll(oldUsername, newUsername))
                .collect(Collectors.toList());
        this.setDislikes(updatedDislikes);
    }

    @Override
    public int compareTo(DeltaArticle deltaArticle) {
        return Comparator.comparing(DeltaArticle::getLikes, (a1, a2) -> Integer.compare(a2.size(), a1.size()))
                .thenComparing(DeltaArticle::getComments, (c1, c2) -> Integer.compare(c2.size(), c1.size()))
                .thenComparing(DeltaArticle::getDislikes, (e1, e2) -> Integer.compare(e2.size(), e1.size()))
                .thenComparing(DeltaArticle::getPublishedAt, (d1, d2) -> {
                    if (d1.isAfter(d2))
                        return 1;
                    else
                        return -1;
                })
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
