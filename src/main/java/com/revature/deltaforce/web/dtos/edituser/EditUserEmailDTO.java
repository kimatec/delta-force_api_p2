package com.revature.deltaforce.web.dtos.edituser;


import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO for packaging new user fields sent from the front end.
 * User will be expected to verify their identity with their existing password.
 **/
@Data
public class EditUserEmailDTO extends EditUserDTO {

    // Get the ID from the Principal in the UI
    private String id;

    // Hold the new Email here
    @Email(message = "Please provide a valid email!")
    private String newEmail;

    // Verify the user's existing password here
    @NotBlank(message = "Please verify your existing password.")
    private String password;
}

