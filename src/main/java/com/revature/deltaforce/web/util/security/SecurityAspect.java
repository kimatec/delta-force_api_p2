package com.revature.deltaforce.web.util.security;

import com.revature.deltaforce.datasources.models.AppUser;
import com.revature.deltaforce.datasources.models.Comment;
import com.revature.deltaforce.util.exceptions.AuthenticationException;
import com.revature.deltaforce.util.exceptions.AuthorizationException;
import com.revature.deltaforce.web.dtos.Principal;
import com.revature.deltaforce.web.dtos.edituser.EditUserDTO;
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

        Principal principal = getPrincipal();

        // Allowed Roles is empty when all roles are permitted
        if (!allowedRoles.isEmpty()) {
            // if the user's role is not listed, throw exception
            if (!allowedRoles.contains(principal.getRole()))
                throw new AuthorizationException("A forbidden request was made by: " + principal.getUsername());
        }
        return pjp.proceed();
    }

    @Around("@annotation(com.revature.deltaforce.web.util.security.IsMyAccount)")
    public Object isMyAccount(ProceedingJoinPoint pjp) throws Throwable {
        Principal principal = getPrincipal();
        if (!((EditUserDTO) (pjp.getArgs()[0])).getId().equals(principal.getId()))
            throw new AuthenticationException("Invalid account edit attempt detected by: " + principal.getUsername());
        return pjp.proceed();
    }

    @Around("@annotation(com.revature.deltaforce.web.util.security.IsMyComment)")
    public Object isMyComment(ProceedingJoinPoint pjp) throws Throwable {
        if (!((Comment) pjp.getArgs()[0]).getUsername().equals(getPrincipal().getUsername()))
            throw new AuthenticationException("You can't update a comment that isn't yours!");
        return pjp.proceed();
    }

    @Around("@annotation(com.revature.deltaforce.web.util.security.IsMyLike)")
    public Object isMyLike(ProceedingJoinPoint pjp) throws Throwable {
        if (!((AppUser) pjp.getArgs()[0]).getUsername().equals(getPrincipal().getUsername()))
            throw new AuthenticationException("Invalid user");
        return pjp.proceed();
    }

    @Around("@annotation(com.revature.deltaforce.web.util.security.IsMyDislike)")
    public Object isMyDislike(ProceedingJoinPoint pjp) throws Throwable {
        if (!((AppUser) pjp.getArgs()[0]).getUsername().equals(getPrincipal().getUsername()))
            throw new AuthenticationException("Invalid user");
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

    public Principal getPrincipal() {
        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Principal principal = parseToken(req)
                .orElseThrow(() ->
                        new AuthenticationException("Request originates from an unauthenticated source.")
                );
        return principal;
    }
}
