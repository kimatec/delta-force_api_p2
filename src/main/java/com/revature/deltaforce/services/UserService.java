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

    /**
     * Authenticate an existing user with valid credentials.
     * @param username
     * @param password
     * @return
     */
    public Principal login(String username, String password){
        String encryptedPass = passwordUtils.generateSecurePassword(password);
        AppUser authUser = userRepo.findAppUserByUsernameAndPassword(username, encryptedPass);

        if(authUser == null) {
            throw new AuthenticationException("Invalid credentials given!");
        }

        return new Principal(authUser);
    }

    /**
     * Send new user fields to repository.
     *
     * @param newUser - new user object
     * @return Principal insertedUser - new principal object for creating a session
     */
    public AppUser registerNewUser(AppUser newUser) {
        if (userRepo.findAppUserByUsername(newUser.getUsername()) != null) {
            throw new ResourcePersistenceException("Provided username is already taken!");
        }
        if (userRepo.findAppUserByEmail(newUser.getEmail()) != null) {
            throw new ResourcePersistenceException("Provided email is already taken!");
        }
        newUser.setPassword(passwordUtils.generateSecurePassword(newUser.getPassword()));
        return userRepo.save(newUser);
    }

    public AppUserDTO findUserById(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        return userRepo.findById(id)
                .map(AppUserDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with provided Id."));


    }

    public Set<String> addTopic (String id, String topic){
        AppUser authUser = userRepo.findAppUserById(id);
        HashSet<String> userFaves = authUser.getFavTopics();

        if(userFaves.contains(topic))
            throw new ResourcePersistenceException("This topic is already on the user's favorite list!");

        userFaves.add(topic);
        authUser.setFavTopics(userFaves);
        userRepo.save(authUser);
        return userFaves;
    }

    public Set<String> removeTopic (String id, String topic){
        AppUser authUser = userRepo.findAppUserById(id);
        HashSet<String> userFaves = authUser.getFavTopics();

        if(!userFaves.contains(topic))
            throw new ResourcePersistenceException("This topic is not the user's favorite list!");

        userFaves.remove(topic);
        authUser.setFavTopics(userFaves);
        userRepo.save(authUser);
        return userFaves;
    }

    /**
     * Takes in an AppUser with edited values, updates values if they are changed and valid.
     * Assumes fields are populated with original data in UI layer. If an invalid/empty value is set, it will not reach this point
     * due to the @Value field in the controller
     * //TODO Create controller, include @Valid at the controller level
     * Note: Might just be able to add existing Id to newValues when it is sent in and bypass the need for principal.
     * */
    public AppUserDTO updateUserInfo(Principal user, AppUser newValues){
        // Get original values
        AppUser userToModify = userRepo.findAppUserById(user.getId());

        // If the username has changed, check that the new username is not taken
        String newUsername = newValues.getUsername();
        if(!newUsername.equals(userToModify.getUsername()))
            if(isUsernameTaken(newUsername)){
                throw new ResourcePersistenceException("This username is already taken!");
            }
        // If the Email has changed, check that the new email is not taken
        String newEmail = newValues.getEmail();
        if(!newEmail.equals(userToModify.getEmail()))
            if(isEmailTaken(newEmail)){
                throw new ResourcePersistenceException("This email is already taken!");
            }
        String newPassword = passwordUtils.generateSecurePassword(newValues.getPassword());
        newValues.setPassword(newPassword);
        newValues.setId(userToModify.getId());

        return new AppUserDTO(userRepo.save(newValues));
    }


    public boolean isUsernameTaken(String newUsername){
        return userRepo.findAppUserByUsername(newUsername) != null;
    }

    public boolean isEmailTaken(String email){
        return userRepo.findAppUserByEmail(email) != null;
    }

}
