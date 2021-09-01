package com.revature.deltaforce.web.dtos;

import com.revature.deltaforce.datasources.models.AppUser;
import io.jsonwebtoken.Claims;

import java.util.Objects;

public class Principal {

    private String id;
    private String username;

    public Principal() {
        super();
    }

    public Principal(AppUser subject) {
        this.id = subject.getId();
        this.username = subject.getUsername();
    }

    public Principal(Claims jwtClaims) {
        this.id = jwtClaims.getId();
        this.username = jwtClaims.getSubject();
//        this.role = jwtClaims.get("role", String.class);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Principal principal = (Principal) o;
        return Objects.equals(id, principal.id) && Objects.equals(username, principal.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "Principal{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

}

