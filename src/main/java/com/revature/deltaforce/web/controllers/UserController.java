package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.services.UserService;
import com.revature.deltaforce.web.dtos.AppUserDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserEmailDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserInfoDTO;
import com.revature.deltaforce.web.dtos.edituser.EditUserPasswordDTO;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.dtos.edituser.EditUserUsernameDTO;
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

    UserService userService;
    TokenGenerator tokenGenerator;

    @Autowired
    public UserController(UserService userService, TokenGenerator tokenGenerator) {
        this.userService = userService;
        this.tokenGenerator = tokenGenerator;
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
        resp.setHeader(tokenGenerator.getJwtHeader(), tokenGenerator.createToken(principal));
        return principal;
    }


    // Edit user password using EditUserPasswordDTO
    // ex: PUT /edit/password
    @PutMapping(
            value="/edit/password",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    public AppUserDTO editUserPassword(@RequestBody @Valid EditUserPasswordDTO editedUser){
        return null;
    }

    // Edit user
    // ex: PUT /edit/username
    @PutMapping(
            value="/edit/username",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    public AppUserDTO editUserUsername(@RequestBody @Valid EditUserUsernameDTO editedUser){
        return null;
    }

    // Edit user
    // ex: PUT /edit/email
    @PutMapping(
            value="/edit/email",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    public AppUserDTO editUserEmail(@RequestBody @Valid EditUserEmailDTO editedUser){
        return null;
    }

    // Edit user
    // ex: PUT /edit/names
    @PutMapping(
            value="/edit/names",
            consumes = "application/json",
            produces = "application/json")
    @Secured(allowedRoles = {})
    public AppUserDTO editUserInfo(@RequestBody @Valid EditUserInfoDTO editedUser){
        return null;
    }



    // Delete User (admin only)
    @Secured(allowedRoles = {"admin"})
    @DeleteMapping(
            value = "{username}"
    )
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUserByUsername(@PathVariable String username){ userService.deleteUserByUsername(username);}


    // User Favorites

    // user/23i4on3ad4sd3fi3oj/faves?add=eggs
    @PostMapping(
            value = "{id}/faves",
            params = {"add"},
            produces = "application/json")
    @Secured(allowedRoles={})
    public Set<String> addToFavesById(@PathVariable("id") String id, @RequestParam("add") String topic){
        return userService.addTopic(id, topic);
    }

    // user/23i4on3ad4sd3fi3oj/faves?remove=lost%20socks
    @DeleteMapping(
            value = "{id}/faves",
            params = {"remove"},
            produces = "application/json")
    @Secured(allowedRoles={})
    public Set<String> removeFromFavesById(@PathVariable("id") String id, @RequestParam("add") String topic){
        return userService.removeTopic(id, topic);
    }

}
