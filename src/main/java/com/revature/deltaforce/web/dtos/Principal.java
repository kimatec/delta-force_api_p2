package com.revature.deltaforce.web.dtos;

import com.revature.deltaforce.datasources.models.AppUser;
import io.jsonwebtoken.Claims;
import lombok.Data;

@Data
public class Principal {

    private String id;
    private String username;
    private String role;

    public Principal(AppUser subject) {
        this.id = subject.getId();
        this.username = subject.getUsername();
        this.role = subject.getRole();
    }

    public Principal(Claims jwtClaims) {
        this.id = jwtClaims.getId();
        this.username = jwtClaims.getSubject();
        this.role = jwtClaims.get("role", String.class);
    }
}

