package com.assigment.auth.securityJwt.domain.authenticationFacade;

import com.assigment.auth.exceptions.NotFoundAuthenticationExecution;
import com.assigment.auth.securityJwt.domain.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public UserDetailsImpl getUserDetailsImpl() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails;
        if (principal instanceof UserDetails) {
            userDetails = ((UserDetailsImpl)principal);
        } else {
            throw new NotFoundAuthenticationExecution();
        }
        return userDetails;
    }
}