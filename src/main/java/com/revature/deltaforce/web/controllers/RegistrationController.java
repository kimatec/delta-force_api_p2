package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.services.UserService;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.util.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * The RegistrationController allows new users to register a new user account with the application.
 */
@RestController
@RequestMapping("/register")
public class RegistrationController {

    UserService userService;
    private final TokenGenerator tokenGenerator;


    @Autowired
    public RegistrationController(UserService userService, TokenGenerator tokenGenerator) {
        this.userService = userService;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Principal registerNewUser(@RequestBody @Valid AppUser newUser, HttpServletResponse resp) {
        Principal principal = new Principal(userService.registerNewUser(newUser));
        resp.setHeader(tokenGenerator.getJwtHeader(), tokenGenerator.createToken(principal));
        return principal;
    }

}
