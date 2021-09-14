package com.revature.deltaforce.services;

import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.datasources.models.DeltaArticle;
import com.revature.deltaforce.datasources.models.ExternalAPIArticle;
import com.revature.deltaforce.datasources.repositories.ArticleRepository;
import com.revature.deltaforce.datasources.repositories.UserRepository;
import com.revature.deltaforce.util.exceptions.ExternalDataSourceException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import com.revature.deltaforce.web.dtos.Source;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTestSuite {

    ArticleService sut;
    private UserRepository mockUserRepo;
    private ArticleRepository mockArticleRepo;

    @BeforeEach
    public void beforeEachTest(){
        mockUserRepo = mock(UserRepository.class);
        mockArticleRepo = mock(ArticleRepository.class);
        sut = new ArticleService(mockUserRepo, mockArticleRepo);
    }

    @AfterEach
    public void afterEachTest() { sut = null; }

    // newsResponseHandler tests //TODO: Add mockRepo to these tests
//    @Test
//    public void newsResponseHandler_returnsDeltaArticles_whenNewsAPI_respondsAsExpected() throws MalformedURLException {
//        // Arrange
//        List<ExternalAPIArticle> validRespList = new ArrayList<>(Arrays.asList(new ExternalAPIArticle()));
//        List<DeltaArticle> expectedResult = new ArrayList<>(Arrays.asList(new DeltaArticle()));
//        List<URL> urlList = new ArrayList<>(Arrays.asList(new URL("url")));
//
//        when(mockArticleRepo.findDeltaArticleByUrl(urlList)).thenReturn()
//        // Act
//        List<DeltaArticle> actualResult = sut.newsResponseHandler(validRespList);
//
//        // Assert
//        assertEquals(expectedResult,actualResult);
//    }

    @Test
    public void newsResponseHandler_throwsException_whenNoNewsIsReceived(){
        // Arrange
        List<ExternalAPIArticle> invalidRespList = new ArrayList<>();

        // Act
        ExternalDataSourceException e = assertThrows(ExternalDataSourceException.class, () -> sut.newsResponseHandler(invalidRespList));

        // Assert

    }

     //addComment Tests
    @Test
    public void addComment_returnsCommentedArticle_whenValidCommentProvided(){
        // Arrange
        DeltaArticle validArticle = new DeltaArticle();
        Comment validComment = new Comment("validId","validUsername","validComment");
        DeltaArticle expectedResult = new DeltaArticle();
        expectedResult.addComment(validComment);
        when(mockArticleRepo.findArticleById(validComment.getArticleId())).thenReturn(validArticle);
        when(mockArticleRepo.save(validArticle)).thenReturn(expectedResult);

        // Act
        DeltaArticle actualResult = sut.addComment(validComment);

        // Assert
        assertEquals(actualResult, expectedResult);
        verify(mockArticleRepo,times(1)).save(validArticle);

    }

    @Test
    public void removeComment_returnsUncommentedArticle_whenValidCommentProvided(){
        // Arrange
        DeltaArticle validArticle = new DeltaArticle();
        Comment validComment = new Comment("validId", "validUsername","validComment");
        validArticle.addComment(validComment);
        when(mockArticleRepo.findArticleById(validComment.getArticleId())).thenReturn(validArticle);
        when(mockArticleRepo.save(validArticle)).thenReturn(validArticle);

        // Act
        DeltaArticle actualResult = sut.removeComment(validComment);
        // Assert
        assertEquals(actualResult, validArticle);
        verify(mockArticleRepo,times(1)).save(validArticle);

    }

    @Test
    public void addLike_addsLike_whenArticleHasNoLikeByUser(){
        // Arrange
        DeltaArticle validArticle = new DeltaArticle();
        DeltaArticle likedArticle = new DeltaArticle();
        likedArticle.getLikes().add("username");
        when(mockArticleRepo.findArticleById("id")).thenReturn(validArticle);
        when(mockArticleRepo.save(validArticle)).thenReturn(likedArticle);

        // Act
        DeltaArticle actualResult = sut.addLike("username","id");
        //Assert
        assertEquals(actualResult, likedArticle);
    }

    @Test
    public void addDislike_addsDislike_whenArticleHasNoDislikeByUser(){
        // Arrange
        DeltaArticle validArticle = new DeltaArticle();
        DeltaArticle dislikedArticle = new DeltaArticle();
        dislikedArticle.getDislikes().add("username");
        when(mockArticleRepo.findArticleById("id")).thenReturn(validArticle);
        when(mockArticleRepo.save(validArticle)).thenReturn(dislikedArticle);

        // Act
        DeltaArticle actualResult = sut.addDislike("username","id");
        //Assert
        assertEquals(actualResult, dislikedArticle);
    }

    @Test
    public void expungeUser_removesUsers_likesDislikesAndComments(){
        // Arrange
        String username = "user";
        Comment testComment = new Comment("id",username,"test");
        DeltaArticle article = new DeltaArticle();
        article.getLikes().add(username);
        article.addComment(testComment);

        DeltaArticle article2 = new DeltaArticle();
        article2.getDislikes().add(username);
        article2.addComment(testComment);

        List<DeltaArticle> userActivity = new ArrayList<>();
        userActivity.add(article);
        userActivity.add(article2);

        when(mockArticleRepo.findDeltaArticleByUsername(username)).thenReturn(userActivity);
        when(mockArticleRepo.saveAll(userActivity)).thenReturn(userActivity);

        // Act
        sut.expungeUser(username);
        // Assert
        boolean a1HasLike = userActivity.get(0).getLikes().contains(username);
        boolean a1HasComment = userActivity.get(0).getComments().contains(testComment);
        boolean a2HasLike = userActivity.get(1).getLikes().contains(username);
        boolean a2HasComment = userActivity.get(1).getComments().contains(testComment);
        assertFalse(a1HasLike);
        assertFalse(a1HasComment);
        assertFalse(a2HasLike);
        assertFalse(a2HasComment);

    }
}
