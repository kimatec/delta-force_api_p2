package com.revature.deltaforce.datasources.models;

import com.revature.deltaforce.web.dtos.Source;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeltaArticle {

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

}
