package com.revature.deltaforce.web.dtos;


import com.revature.deltaforce.datasources.models.AppUser;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.HashSet;

/**
 *  DTO for packaging new user fields sent from the front end.
 *  User will be expected to verify their identity with their existing password.
 **/
@Data
public class EditUserDTO {

    private String id;

    // These can all be new, existing, or null values.
    private String firstName;
    private String lastName;

    @Email
    private String email;
    private String username;
    private String password;

    // verify the user's existing password here
    private String verifyExistingPassword;

    public EditUserDTO(AppUser subject) {
        this.id = subject.getId();
        this.firstName = subject.getFirstName();
        this.lastName = subject.getLastName();
        this.email = subject.getEmail();
        this.username = subject.getUsername();
    }


}
