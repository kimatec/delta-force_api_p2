package com.revature.deltaforce.services;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.ExternalAPIArticle;
import com.revature.deltaforce.datasources.repositories.ArticleRepository;
import com.revature.deltaforce.util.exceptions.ExternalDataSourceException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import com.revature.deltaforce.web.dtos.Source;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTestSuite {

    ArticleService sut;

    private ArticleRepository mockArticleRepo;

    @BeforeEach
    public void beforeEachTest(){
        mockArticleRepo = mock(ArticleRepository.class);
        sut = new ArticleService(mockArticleRepo);
    }

    @AfterEach
    public void afterEachTest() { sut = null; }

    // newsResponseHandler tests //TODO: Add mockRepo to these tests
    @Test
    public void newsResponseHandler_returnsDeltaArticles_whenNewsAPI_respondsAsExpected(){
        // Arrange
        List<ExternalAPIArticle> validRespList = new ArrayList<>();
        validRespList.add(new ExternalAPIArticle());

        List<DeltaArticle> expectedResult = new ArrayList<>();
        expectedResult.add(new DeltaArticle());

        // Act
        List<DeltaArticle> actualResult = sut.newsResponseHandler(validRespList);

        // Assert
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void newsResponseHandler_throwsException_whenNoNewsIsReceived(){
        // Arrange
        List<ExternalAPIArticle> invalidRespList = new ArrayList<>();

        // Act
        ExternalDataSourceException e = assertThrows(ExternalDataSourceException.class, () -> sut.newsResponseHandler(invalidRespList));

        // Assert

    }

    @Test
    public void addComment_returnsCommentedArticle_whenValidCommentProvided(){
        // Arrange
        DeltaArticle validArticle = new DeltaArticle();
        Comment validComment = new Comment("validUsername","validComment");
        DeltaArticle expectedResult = new DeltaArticle();
        expectedResult.addComment(validComment);
        when(mockArticleRepo.save(validArticle)).thenReturn(expectedResult);

        // Act
        DeltaArticle actualResult = sut.addComment(validComment,validArticle);

        // Assert
        assertEquals(actualResult, expectedResult);
        verify(mockArticleRepo,times(1)).save(validArticle);

    }

    @Test
    public void addComment_throwsException_whenInvalidCommentProvided(){
        // Arrange
        DeltaArticle validArticle = new DeltaArticle();
        Comment invalidComment = new Comment("username","");

        // Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> sut.addComment(invalidComment,validArticle));

        // Assert
        verify(mockArticleRepo,times(0)).save(validArticle);

    }

}
