package com.revature.deltaforce.web.dtos;

import com.revature.deltaforce.datasources.models.DeltaArticle;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeltaArticlesDTO {
    private List<DeltaArticle> articles = new ArrayList<>();
}
