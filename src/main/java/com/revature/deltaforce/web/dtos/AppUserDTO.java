package com.revature.deltaforce.web.dtos;

import com.revature.deltaforce.datasources.models.AppUser;
import lombok.Data;

import java.util.HashSet;

@Data
public class AppUserDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private HashSet<String> favTopics;

    public AppUserDTO(AppUser subject) {
        this.id = subject.getId();
        this.firstName = subject.getFirstName();
        this.lastName = subject.getLastName();
        this.email = subject.getEmail();
        this.username = subject.getUsername();
        this.favTopics = subject.getFavTopics();
    }
}
