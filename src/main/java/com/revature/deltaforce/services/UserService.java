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


    public AppUserDTO findUserById(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        return userRepo.findById(id)
                .map(AppUserDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("No user found with provided Id."));


    }
}
