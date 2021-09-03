package com.revature.deltaforce.datasources.models;

import com.revature.deltaforce.web.dtos.Source;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;

@Data
public class ExternalAPIArticle {

    private Source source;
    private String author;
    private String title;
    private String description;
    private URL url;
    private URL urlToImage;
    private LocalDateTime publishedAt;
    private String content;

}
