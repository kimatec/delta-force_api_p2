package com.revature.deltaforce.services;


import com.revature.deltaforce.datasources.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
        // this.passwordUtils = passwordUtils;
    }

}
