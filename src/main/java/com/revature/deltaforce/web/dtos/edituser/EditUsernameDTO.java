package com.revature.deltaforce.web.dtos.edituser;


import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * DTO for packaging new user fields sent from the front end.
 * User will be expected to verify their identity with their existing password.
 **/
@Data
public class EditUsernameDTO extends EditUserDTO {

    // Get the ID from the Principal in the UI
    private String id;

    // Hold the new username here
    @NotBlank(message = "Username cannot be blank!")
    private String newUsername;

    // Verify the user's existing password here
    @NotBlank(message = "Please verify your existing password.")
    private String password;
}