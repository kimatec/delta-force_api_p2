package com.revature.deltaforce.web.util.security;

import com.revature.deltaforce.util.exceptions.AuthenticationException;
import com.revature.deltaforce.web.dtos.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
public class SecurityAspect {

    private final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);
    private final JwtConfig jwtConfig;

    @Autowired
    public SecurityAspect(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Around("@annotation(com.revature.deltaforce.web.util.security.Secured)")
    public Object secureEndpoint(ProceedingJoinPoint pjp) throws Throwable {

        List<String> allowedRoles = Arrays.asList(
                ((MethodSignature) pjp.getSignature())
                                      .getMethod()
                                      .getAnnotation(Secured.class)
                                      .allowedRoles()
        );


        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        // TODO: Find out why this is an UndeclaredThrowableException when key is null or invalid

        Principal principal = parseToken(req)
                .orElseThrow(() ->
                new AuthenticationException("Request originates from an unauthenticated source.")
        );

        // Allowed Roles is empty when all roles are permitted
        if (!allowedRoles.isEmpty()) {
            // TODO: Implement user/admin roles.
            // if the user's role is not listed, throw exception
            //if(!allowedRoles.contains(principal.getRole()))
            throw new AuthenticationException("A forbidden request was made by: " + principal.getUsername());
        }

        return pjp.proceed();
    }

    public Optional<Principal> parseToken(HttpServletRequest req) {
        try {
            String header = req.getHeader(jwtConfig.getHeader());

            if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
                logger.warn("Request originates from an unauthenticated source.");
                return Optional.empty();
            }

            String token = header.replaceAll(jwtConfig.getPrefix(), "");

            Claims jwtClaims = Jwts.parser()
                                   .setSigningKey(jwtConfig.getSigningKey())
                                   .parseClaimsJws(token)
                                   .getBody();

            return Optional.of(new Principal(jwtClaims));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }


}
