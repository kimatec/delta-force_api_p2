package com.revature.deltaforce.datasources.models;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Objects;

@Data
public class Comment {
    private String articleId;

    @NotBlank
    private String username;

    @NotBlank(message = "Content cannot be blank.")
    private String content;

    private Instant timePosted;

    public Comment() {
        this.timePosted = Instant.now();
    }

    public Comment(String articleId, String username, String content) {
        this.articleId = articleId;
        this.username = username;
        this.content = content;
        this.timePosted = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(username, comment.username) && Objects.equals(content, comment.content) && Objects.equals(articleId, comment.articleId);
    }
}
