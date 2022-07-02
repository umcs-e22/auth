package com.assigment.auth.securityJwt.domain.authenticationFacade;

import com.assigment.auth.securityJwt.domain.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
    Authentication getAuthentication();

    UserDetailsImpl getUserDetailsImpl();
}
