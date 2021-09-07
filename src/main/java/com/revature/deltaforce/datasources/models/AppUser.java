package com.revature.deltaforce.datasources.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;

@Data
@RequiredArgsConstructor
@Document(collection = "users")
public class AppUser {
    private String id;

    @NotBlank(message = "You must have a first name")
    private String firstName;

    @NotBlank(message = "Last name can not be null or blank")
    private String lastName;

    @Email
    @NotBlank(message = "Email can not be null or blank")
    private String email;

    @NotBlank(message = "You need to have a username")
    private String username;

    @NotBlank(message = "Password can not be null or blank")
    private String password;

    private HashSet<String> favTopics = new HashSet<>();


    public AppUser(String firstName, String lastName, String email, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // For pulling user data from database
    public AppUser(String firstName, String lastName, String email, String username, String password, HashSet<String> favTopics) {
        this(firstName, lastName, email, username, password);
        this.favTopics = favTopics;
    }
}
