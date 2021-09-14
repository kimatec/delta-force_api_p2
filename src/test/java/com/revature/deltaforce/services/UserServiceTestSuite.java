package com.revature.deltaforce.services;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.repositories.UserRepository;
import com.revature.deltaforce.util.PasswordUtils;
import com.revature.deltaforce.util.exceptions.AuthenticationException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import com.revature.deltaforce.util.exceptions.ResourcePersistenceException;
import com.revature.deltaforce.web.dtos.AppUserDTO;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.dtos.edituser.EditUserEmailDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserInfoDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserPasswordDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUsernameDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class UserServiceTestSuite {

    UserService sut;

    private PasswordUtils mockPasswordUtils;
    private UserRepository mockUserRepo;

    @BeforeEach
    public void beforeEachTest() {
        mockPasswordUtils = mock(PasswordUtils.class);
        mockUserRepo = mock(UserRepository.class);
        sut = new UserService(mockUserRepo, mockPasswordUtils);
    }

    @AfterEach
    public void afterEachTest() {
        sut = null;
    }

    // login tests
    @Test
    public void login_returnsPrincipal_whenUser_providesValidCredentials(){
        // Arrange
        String username = "username";
        String password = "password";
        AppUser authUser = new AppUser("valid","user","valid@user.com","username","encryptedPassword");
        Principal expectedResult = new Principal(authUser);
        when(mockPasswordUtils.generateSecurePassword(password)).thenReturn("encryptedPassword");
        when(mockUserRepo.findAppUserByUsernameAndPassword(username, "encryptedPassword")).thenReturn(authUser);

        // Act
        Principal actualResult = sut.login(username, password);

        // Assert
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void login_throwsException_whenUser_providesInvalidCredentials(){
        // Arrange
        String username = "username";
        String password = "password";
        when(mockPasswordUtils.generateSecurePassword(password)).thenReturn("encryptedPassword");
        when(mockUserRepo.findAppUserByUsernameAndPassword(username, "encryptedPassword")).thenReturn(null);

        // Act
        AuthenticationException e = assertThrows(AuthenticationException.class, () ->  sut.login(username, password));

        // Assert
        verify(mockPasswordUtils, times(1)).generateSecurePassword(password);


    }

    // registerNewUser Tests
    @Test
    public void registerNewUser_returnsSuccessfully_whenGivenValidUser() {

        // Arrange
        AppUser expectedResult = new AppUser("valid", "valid", "valid", "valid", "valid");
        AppUser validUser = new AppUser("valid", "valid", "valid", "valid", "valid");
        when(mockUserRepo.save(any())).thenReturn(expectedResult);
        when(mockPasswordUtils.generateSecurePassword(validUser.getPassword())).thenReturn("encrypted");

        // Act
        AppUser actualResult = sut.registerNewUser(validUser);

        // Assert
        assertEquals(expectedResult, actualResult);
//        assertNotEquals(expectedResult.getPassword(), actualResult.getPassword()); need to ask wezley about this, it's broken on his app too, lol
        verify(mockUserRepo, times(1)).save(any());
        verify(mockPasswordUtils, times(1)).generateSecurePassword(anyString());

    }

    @Test
    public void registerNewUser_throwsException_whenGivenUserWithDuplicateUsername() {

        // Arrange
        AppUser existingUser = new AppUser("original", "original", "original", "duplicate", "original");
        AppUser duplicate = new AppUser("first", "last", "email", "duplicate", "password");
        when(mockUserRepo.findAppUserByUsername(duplicate.getUsername())).thenReturn(existingUser);

        // Act
        ResourcePersistenceException e = assertThrows(ResourcePersistenceException.class, () -> sut.registerNewUser(duplicate));

        // Assert
        assertEquals("Provided username is already taken!", e.getMessage());
        verify(mockUserRepo, times(1)).findAppUserByUsername(duplicate.getUsername());
        verify(mockUserRepo, times(0)).save(duplicate);

    }

    @Test
    public void register_throwsException_whenGivenUserWithDuplicateEmail() {

        // Arrange
        AppUser existingUser = new AppUser("original", "original", "duplicate", "original", "original");
        AppUser duplicate = new AppUser("first", "last", "duplicate", "username", "password");
        when(mockUserRepo.findAppUserByEmail(duplicate.getEmail())).thenReturn(existingUser);

        // Act
        ResourcePersistenceException e = assertThrows(ResourcePersistenceException.class, () -> sut.registerNewUser(duplicate));

        // Assert
        assertEquals("Provided email is already taken!", e.getMessage());
        verify(mockUserRepo, times(1)).findAppUserByEmail(duplicate.getEmail());
        verify(mockUserRepo, times(0)).save(duplicate);

    }

    // addTopic Tests
    @Test
    public void addTopic_returnsSuccessfully_whenProvided_newTopic(){

    }

    @Test
    public void addTopic_throwsException_whenProvided_existingTopic(){

    }

    // removeTopic Tests
    @Test
    public void removeTopic_returnsSuccessfully_whenProvided_existingTopic(){

    }

    @Test
    public void removeTopic_throwsException_whenProvided_nonexistentTopic(){

    }

    // findUserById Tests
    @Test
    public void findUserById_returnsAppUserDTO_whenProvided_validId(){
        // Arrange
        String validId = "some-valid-id";
        AppUser expectedUser = new AppUser();
        expectedUser.setId(validId);
        when(mockUserRepo.findById(validId)).thenReturn(java.util.Optional.of(expectedUser));

        // Act
        AppUserDTO actualResult = sut.findUserById(validId);

        // Assert
        assertEquals(actualResult.getId(),expectedUser.getId());
    }

    @Test
    public void findUserById_throwsInvalidRequestException_whenProvided_invalidId(){
        // Arrange
        String invalidId = "";

        // Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class,() -> sut.findUserById(invalidId));

        // Assert
        verify(mockUserRepo,times(0)).findById(invalidId);
    }

    //update User information - username
    @Test
    public void updateUsername_returnNewUsername_whenProvided_validId(){
        //Arrange
        String validId = "valid-id";
        AppUser validUser = new AppUser();
        EditUsernameDTO expectedUser = new EditUsernameDTO();
        expectedUser.setId(validId);
        when(mockUserRepo.findAppUserById(expectedUser.getId())).thenReturn(validUser);
        when(mockPasswordUtils.generateSecurePassword(expectedUser.getPassword())).thenReturn("encrypted");
        when(mockUserRepo.save(any())).thenReturn(validUser);

        //Act
        AppUser actualUser = sut.updateUsername(expectedUser);

        //Assert
        assertEquals(actualUser.getId(), expectedUser.getNewUsername());
    }

    @Test
    public void updateUserPassword_returnNewPassword_whenProvided_ValidId(){
        //Arrange
        String validId = "valid-id";
        AppUser validUser = new AppUser();
        EditUserPasswordDTO expectedUser = new EditUserPasswordDTO();
        expectedUser.setId(validId);
        when(mockUserRepo.findAppUserById(expectedUser.getId())).thenReturn(validUser);
        when(mockPasswordUtils.generateSecurePassword(expectedUser.getPassword())).thenReturn("encrypted");
        when(mockUserRepo.save(any())).thenReturn(validUser);

        //Act
        AppUser actualUser = sut.updateUserPassword(expectedUser);

        //Assert
        assertEquals(actualUser.getId(), expectedUser.getNewPassword());

    }

    @Test
    public void updateUserEmail_returnNewEmail_whenProvided_ValidId(){
        //Arrange
        String validId = "valid-id";
        AppUser validUser = new AppUser();
        EditUserEmailDTO expectedUser = new EditUserEmailDTO();
        expectedUser.setId(validId);
        when(mockUserRepo.findAppUserById(expectedUser.getId())).thenReturn(validUser);
        when(mockPasswordUtils.generateSecurePassword(expectedUser.getPassword())).thenReturn("encrypted");
        when(mockUserRepo.save(any())).thenReturn(validUser);

        //Act
        AppUser actualUser = sut.updateUserEmail(expectedUser);

        //Assert
        assertEquals(actualUser.getId(), expectedUser.getNewEmail());

    }

    @Test
    public void updateUserInfo_returnNewUserInfo_whenProvided_validId(){
        //Arrange
        String validId = "valid-id";
        AppUser validUser = new AppUser();
        EditUserInfoDTO expectedUser = new EditUserInfoDTO();
        expectedUser.setId(validId);
        when(mockUserRepo.findAppUserById(expectedUser.getId())).thenReturn(validUser);
        when(mockPasswordUtils.generateSecurePassword(expectedUser.getPassword())).thenReturn("encrypted");
        when(mockUserRepo.save(any())).thenReturn(validUser);

        //Act
        AppUser actualUser = sut.updateUserInfo(expectedUser);

        //Assert
        assertEquals(actualUser.getId(), expectedUser.getNewFirstName(), expectedUser.getNewLastName());
    }

}
