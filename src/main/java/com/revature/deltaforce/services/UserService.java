package com.revature.deltaforce.services;


import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.repositories.UserRepository;
import com.revature.deltaforce.util.PasswordUtils;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import com.revature.deltaforce.web.dtos.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new InvalidRequestException("Invalid credentials given!");
        }

        return new Principal(authUser);
    }

    /**
     * Send new user fields to repository.
     * TODO: add validation
     * @param newUser
     * @return
     */
    public Principal registerNewUser(AppUser newUser) {
        newUser.setPassword(passwordUtils.generateSecurePassword(newUser.getPassword()));
        AppUser insertedUser = userRepo.save(newUser);
        return new Principal(insertedUser);
    }
}
