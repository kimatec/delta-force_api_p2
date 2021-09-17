package com.revature.deltaforce.web.util.security;

import com.revature.deltaforce.web.dtos.Principal;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenGenerator {

    private final JwtConfig jwtConfig;

    @Autowired
    public TokenGenerator(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String createToken(Principal subject) {
        long now = System.currentTimeMillis();

        JwtBuilder tokenBuilder = Jwts.builder()
                .setId(subject.getId())
                .setSubject(subject.getUsername())
                .claim("role", subject.getRole()) // specify the role of the user for whom this token is for
                .setIssuer("revature")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration()))
                .signWith(jwtConfig.getSigAlg(), jwtConfig.getSigningKey());

        return jwtConfig.getPrefix() + tokenBuilder.compact();
    }
    public String getJwtHeader() {
        return jwtConfig.getHeader();
    }

    public JwtConfig getJwtConfig() {
        return jwtConfig;
    }
}
