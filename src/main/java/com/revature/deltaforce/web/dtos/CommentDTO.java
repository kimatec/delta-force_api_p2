package com.revature.deltaforce.web.dtos;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
public class CommentDTO {
    private Comment comment;
    private DeltaArticle deltaArticle;
}
