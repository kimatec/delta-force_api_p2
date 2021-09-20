package com.revature.deltaforce.services;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.repositories.UserRepository;
import com.revature.deltaforce.util.PasswordUtils;
import com.revature.deltaforce.util.exceptions.AuthenticationException;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import com.revature.deltaforce.util.exceptions.ResourceNotFoundException;
import com.revature.deltaforce.util.exceptions.ResourcePersistenceException;
import com.revature.deltaforce.web.dtos.AppUserDTO;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.dtos.edituser.EditUserEmailDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserInfoDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserPasswordDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUsernameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordUtils passwordUtils;

    @Autowired
    public UserService(UserRepository userRepo, PasswordUtils passwordUtils) {
        this.userRepo = userRepo;
        this.passwordUtils = passwordUtils;
    }

    // Returns True if a username is taken.
    public boolean isUsernameTaken(String username) {
        return userRepo.findAppUserByUsername(username) != null;
    }

    // Returns True if an email is taken.
    public boolean isEmailTaken(String email) {
        return userRepo.findAppUserByEmail(email) != null;
    }

    /**
     * Authenticate an existing user with valid credentials.
     *
     * @param username
     * @param password
     * @return
     */
    public Principal login(String username, String password) {
        String encryptedPass = passwordUtils.generateSecurePassword(password);
        AppUser authUser = userRepo.findAppUserByUsernameAndPassword(username, encryptedPass);

        if (authUser == null)
            throw new AuthenticationException("Invalid credentials given!");

        return new Principal(authUser);
    }

    /**
     * Send new user fields to repository.
     *
     * @param newUser - new user object
     * @return Principal insertedUser - new principal object for creating a session
     */
    public AppUser registerNewUser(AppUser newUser) {
        if (userRepo.findAppUserByUsername(newUser.getUsername()) != null)
            throw new ResourcePersistenceException("Provided username is already taken!");
        if (userRepo.findAppUserByEmail(newUser.getEmail()) != null)
            throw new ResourcePersistenceException("Provided email is already taken!");

        newUser.setPassword(passwordUtils.generateSecurePassword(newUser.getPassword()));
        return userRepo.save(newUser);
    }

    // Attempts to find a user with the provided id
    public AppUserDTO findUserById(String id) {
        if (id == null || id.trim().isEmpty())
            throw new InvalidRequestException("Invalid id provided");

        return userRepo.findById(id)
                .map(AppUserDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with provided Id."));
    }

    // Adds a topic to a user's favorites.
    public Set<String> addTopic(String id, String topic) {
        AppUser authUser = userRepo.findAppUserById(id);
        HashSet<String> userFaves = authUser.getFavTopics();

        if (userFaves.contains(topic))
            throw new ResourcePersistenceException("This topic is already on the user's favorite list!");

        userFaves.add(topic);
        authUser.setFavTopics(userFaves);
        userRepo.save(authUser);
        return userFaves;
    }

    // Removes a topic from a user's favorites
    public Set<String> removeTopic(String id, String topic) {
        AppUser authUser = userRepo.findAppUserById(id);
        HashSet<String> userFaves = authUser.getFavTopics();

        if (!userFaves.contains(topic))
            throw new ResourcePersistenceException("This topic is not the user's favorite list!");

        userFaves.remove(topic);
        authUser.setFavTopics(userFaves);
        userRepo.save(authUser);
        return userFaves;
    }

    // Methods to edit a user's information
    public AppUser updateUsername(EditUsernameDTO editedUser) {
        // Check if the new username is available
        if (isUsernameTaken(editedUser.getNewUsername()))
            throw new ResourcePersistenceException("This username is already taken!");

        // Retrieve original values
        AppUser updatedUser = userRepo.findAppUserById(editedUser.getId());

        // Verify password
        String encryptedPass = passwordUtils.generateSecurePassword(editedUser.getPassword());
        if (!encryptedPass.equals(updatedUser.getPassword()))
            throw new AuthenticationException("Invalid password provided!");

        // Update username
        updatedUser.setUsername(editedUser.getNewUsername());

        // Save and return updated user
        return userRepo.save(updatedUser);
    }

    public AppUser updateUserPassword(EditUserPasswordDTO editedUser) {
        // Retrieve original values
        AppUser updatedUser = userRepo.findAppUserById(editedUser.getId());

        // Verify password
        String encryptedPass = passwordUtils.generateSecurePassword(editedUser.getPassword());
        if (!encryptedPass.equals(updatedUser.getPassword()))
            throw new AuthenticationException("Invalid password provided!");

        // Encrypt new password
        String newEncryptedPass = passwordUtils.generateSecurePassword(editedUser.getNewPassword());

        // Update password
        updatedUser.setPassword(newEncryptedPass);

        // Save and return updated user
        return userRepo.save(updatedUser);
    }

    public AppUser updateUserEmail(EditUserEmailDTO editedUser) {
        // Check if the new email is available
        if (isEmailTaken(editedUser.getNewEmail()))
            throw new ResourcePersistenceException("This email is already taken!");

        // Retrieve original values
        AppUser updatedUser = userRepo.findAppUserById(editedUser.getId());

        // Verify password
        String encryptedPass = passwordUtils.generateSecurePassword(editedUser.getPassword());
        if (!encryptedPass.equals(updatedUser.getPassword()))
            throw new AuthenticationException("Invalid password provided!");

        // Update username
        updatedUser.setEmail(editedUser.getNewEmail());

        // Save and return updated user
        return userRepo.save(updatedUser);
    }

    public AppUser updateUserInfo(EditUserInfoDTO editedUser) {
        // Retrieve original values
        AppUser updatedUser = userRepo.findAppUserById(editedUser.getId());

        // Verify password
       String encryptedPass = passwordUtils.generateSecurePassword(editedUser.getPassword());
        if(!encryptedPass.equals(updatedUser.getPassword()))
            throw new AuthenticationException("Invalid password provided!");

        // Update fields
        updatedUser.setFirstName(editedUser.getNewFirstName());
        updatedUser.setLastName(editedUser.getNewLastName());

        // Save and return updated user
        return userRepo.save(updatedUser);
    }

    // Deletes a user by their username - admin only operation.
    public void deleteUserByUsername(String username) {
        AppUser userToDelete = userRepo.findAppUserByUsername(username);
        if (userToDelete == null)
            throw new InvalidRequestException("No user found with provided username.");
        userRepo.delete(userToDelete);
    }
}
