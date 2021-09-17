package com.revature.deltaforce.web.dtos.edituser;


import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * DTO for packaging new user fields sent from the front end.
 * User will be expected to verify their identity with their existing password.
 **/
@Data
public class EditUserPasswordDTO extends EditUserDTO {

    // Get the ID from the Principal in the UI
    private String id;

    // Hold the new Password here
    @NotBlank(message = "Password cannot be blank!")
    private String newPassword;

    // Verify the user's existing password here
    @NotBlank(message = "Please verify your existing password.")
    private String password;
}