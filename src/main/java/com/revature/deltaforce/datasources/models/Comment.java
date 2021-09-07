package com.revature.deltaforce.datasources.models;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class Comment {

    @NotBlank
    private String username;

    @NotBlank
    private String content;

    private Instant timePosted;

    public Comment(String un, String comment) {
        this.username = un;
        this.content = comment;
        this.timePosted = Instant.now();
    }

}
