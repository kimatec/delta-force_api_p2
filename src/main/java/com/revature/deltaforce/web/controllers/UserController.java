package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.services.ArticleService;
import com.revature.deltaforce.services.UserService;
import com.revature.deltaforce.web.dtos.AppUserDTO;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.dtos.edituser.EditUserEmailDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserInfoDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserPasswordDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUsernameDTO;
import com.revature.deltaforce.web.util.security.IsMyAccount;
import com.revature.deltaforce.web.util.security.Secured;
import com.revature.deltaforce.web.util.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ArticleService articleService;
    private final TokenGenerator tokenGenerator;

    @Autowired
    public UserController(UserService userService, ArticleService articleService, TokenGenerator tokenGenerator) {
        this.userService = userService;
        this.tokenGenerator = tokenGenerator;
        this.articleService = articleService;
    }

    // Get a user by their ID
    // ex: GET /user/aj3io4jp2d3o908df34
    @GetMapping(value = "{id}", produces = "application/json")
    public AppUserDTO getUserById(@PathVariable String id) {
        return userService.findUserById(id);
    }

    // For registering a new user
    @PostMapping(consumes = "application/json", produces = "application/json")
    public Principal registerNewUser(@RequestBody @Valid AppUser newUser, HttpServletResponse resp) {
        Principal principal = new Principal(userService.registerNewUser(newUser));
        resp.setStatus(201);
        resp.setHeader(tokenGenerator.getJwtHeader(), tokenGenerator.createToken(principal));
        return principal;
    }


    // Edit user password, returns new principal and updates JWT
    // ex: PUT user/edit/password
    @PutMapping(
            value = "/edit/password",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    @IsMyAccount
    public Principal editUserPassword(@RequestBody @Valid EditUserPasswordDTO editedUser, HttpServletResponse resp) {
        Principal principal = new Principal(userService.updateUserPassword(editedUser));
        resp.setHeader(tokenGenerator.getJwtHeader(), tokenGenerator.createToken(principal));
        return principal;
    }

    // Edit username, returns new principal and updates JWT
    // ex: PUT user/edit/username
    @PutMapping(
            value = "/edit/username",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    @IsMyAccount
    public Principal editUsername(@RequestBody @Valid EditUsernameDTO editedUser, HttpServletResponse resp) {
        AppUserDTO user = userService.findUserById(editedUser.getId());
        Principal principal = new Principal(userService.updateUsername(editedUser));
        articleService.updateUsername(user.getUsername(), editedUser.getNewUsername());
        resp.setHeader(tokenGenerator.getJwtHeader(), tokenGenerator.createToken(principal));
        return principal;
    }

    // Edit user email
    // ex: PUT user/edit/email
    @PutMapping(
            value = "/edit/email",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    @IsMyAccount
    public AppUserDTO editUserEmail(@RequestBody @Valid EditUserEmailDTO editedUser) {
        return new AppUserDTO(userService.updateUserEmail(editedUser));
    }

    // Edit user info - currently first name and last name
    // ex: PUT user/edit/userinfo
    @PutMapping(
            value = "/edit/userinfo",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    @IsMyAccount
    public AppUserDTO editUserInfo(@RequestBody @Valid EditUserInfoDTO editedUser) {
        return new AppUserDTO(userService.updateUserInfo(editedUser));
    }

    // Delete User (admin only)
    @Secured(allowedRoles = {"admin"})
    @DeleteMapping(
            value = "{username}"
    )
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserByUsername(@PathVariable String username) {
        articleService.expungeUser(username);
        userService.deleteUserByUsername(username);
    }

    // User Favorites
    // user/23i4on3ad4sd3fi3oj/faves?add=Health
    @PostMapping(
            value = "{id}/faves",
            params = {"add"},
            produces = "application/json")
    @Secured(allowedRoles = {})
    public Set<String> addToFavesById(@PathVariable("id") String id, @RequestParam("add") String topic) {
        return userService.addTopic(id, topic);
    }

    // user/23i4on3ad4sd3fi3oj/faves?remove=Sports
    @DeleteMapping(
            value = "{id}/faves",
            params = {"remove"},
            produces = "application/json")
    @Secured(allowedRoles = {})
    public Set<String> removeFromFavesById(@PathVariable("id") String id, @RequestParam("remove") String topic) {
        return userService.removeTopic(id, topic);
    }
}
