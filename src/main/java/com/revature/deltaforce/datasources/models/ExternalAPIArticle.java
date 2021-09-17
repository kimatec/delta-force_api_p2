package com.revature.deltaforce.datasources.models;

import com.revature.deltaforce.web.dtos.Source;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;

@Data
public class ExternalAPIArticle implements Comparable<ExternalAPIArticle> {

    private Source source;
    private String author;
    private String title;
    private String description;
    private URL url;
    private String urlToImage;
    private LocalDateTime publishedAt;
    private String content;

    @Override
    public int compareTo(ExternalAPIArticle article) {
        return Comparator.comparing(ExternalAPIArticle::getPublishedAt, (a1, a2) -> {
                    if (a1.isAfter(a2))
                        return 1;
                    else
                        return -1;
                })
                .compare(this, article);
    }
}
