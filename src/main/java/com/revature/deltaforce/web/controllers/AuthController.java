package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.services.UserService;
import com.revature.deltaforce.web.dtos.Credentials;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.util.security.TokenGenerator;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.awt.dnd.MouseDragGestureRecognizer;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final TokenGenerator tokenGenerator;

    @Autowired
    public AuthController(UserService userService, TokenGenerator tokenGenerator){
        this.userService = userService;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public Principal authenticate(@RequestBody Credentials credentials, HttpServletResponse resp){
        Principal principal = userService.login(credentials.getUsername(), credentials.getPassword());
        resp.setHeader(tokenGenerator.getJwtHeader(), tokenGenerator.createToken(principal));
        return principal;
    }
}
