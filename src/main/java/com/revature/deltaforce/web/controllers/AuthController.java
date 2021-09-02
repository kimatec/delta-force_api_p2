package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.services.UserService;
import com.revature.deltaforce.web.dtos.Credentials;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.util.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.awt.dnd.MouseDragGestureRecognizer;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public Principal authenticate(@RequestBody Credentials credentials, HttpServletResponse resp){
        Principal principal = userService.login(credentials.getUsername(), credentials.getPassword());
        //resp.setHeader();
        return principal;
    }
}
