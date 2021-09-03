package com.revature.deltaforce.datasources.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;

@Data
@RequiredArgsConstructor
@Document(collection = "users")
public class AppUser {
    private String id;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @Email
    private String email;

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private HashSet<String> favTopics;

    public AppUser(String id, String firstName, String lastName, String email, String username, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.favTopics = new HashSet<>();
    }

    // For pulling user data from database
    public AppUser(String firstName, String lastName, String email, String username, String password, HashSet<String> favTopics) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.favTopics = favTopics;
    }
}
