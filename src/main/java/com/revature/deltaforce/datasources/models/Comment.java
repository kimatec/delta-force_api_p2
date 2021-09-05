package com.revature.deltaforce.datasources.models;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Comment {

    @NotBlank
    private String username;

    @NotBlank
    private String content;

    public Comment(String un, String comment) {
        this.username = un;
        this.content = comment;
    }

}
