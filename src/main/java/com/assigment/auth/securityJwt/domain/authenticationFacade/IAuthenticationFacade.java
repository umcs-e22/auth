package com.assigment.auth.securityJwt.domain.authenticationFacade;

import com.assigment.auth.securityJwt.domain.services.UserDetailsImpl;

public interface IAuthenticationFacade {
    UserDetailsImpl getUserDetailsImpl();
}
