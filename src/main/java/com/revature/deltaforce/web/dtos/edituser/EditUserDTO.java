package com.revature.deltaforce.web.dtos.edituser;

import lombok.Data;

/**
 * For verifying a user's ID matches the principal in their JWT.
 * Used in SecurityAspect for the annotation @IsMyAccount
 */
@Data
public abstract class EditUserDTO {
    private String id;
}
