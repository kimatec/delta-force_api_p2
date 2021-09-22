package com.revature.deltaforce.web.dtos.edituser;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * DTO for packaging new user fields sent from the front end.
 * User will be expected to verify their identity with their existing password.
 * Named "Info" in the event we would like to allow users to provide more information.
 **/
@Data
public class EditUserInfoDTO extends EditUserDTO {
    // Get the ID from the Principal in the UI
    private String id;

    // Hold the new Information here
    @NotBlank(message = "First name cannot be blank!")
    private String newFirstName;

    @NotBlank(message = "Last name cannot be blank!")
    private String newLastName;

    // Verify the user's existing password here
    @NotBlank(message = "Please verify your existing password.")
    private String password;
}