package com.revature.deltaforce.services;


import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.repositories.UserRepository;
import com.revature.deltaforce.util.PasswordUtils;
import com.revature.deltaforce.util.exceptions.InvalidRequestException;
import com.revature.deltaforce.web.dtos.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordUtils passwordUtils;

    @Autowired
    public UserService(UserRepository userRepo, PasswordUtils passwordUtils) {
        this.userRepo = userRepo;
        this.passwordUtils = passwordUtils;
    }

    public Principal login(String username, String password){

        if(username == null || username.trim().equals("") || password == null || password.trim().equals("")){
            throw new InvalidRequestException("Invalid user credentials provided!");
        }

        String encryptedPass = passwordUtils.generateSecurePassword(password);
        AppUser authUser = userRepo.findUserByCredentials(username, encryptedPass);

        if(authUser == null){
            throw new InvalidRequestException("Invalid credentials given!");
        }

        return new Principal(authUser);
    }
}
