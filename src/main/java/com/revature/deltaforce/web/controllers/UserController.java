package com.revature.deltaforce.web.controllers;

import com.revature.deltaforce.services.UserService;
import com.revature.deltaforce.web.dtos.AppUserDTO;
import com.revature.deltaforce.web.util.security.Secured;
import com.revature.deltaforce.web.util.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "{id}", produces = "application/json")
    public AppUserDTO getUserById(@PathVariable String id) {
        return userService.findUserById(id);
    }

    // Not sure if I like the way I implemented these, will probably make a UserFavesDTO later instead - cody
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
