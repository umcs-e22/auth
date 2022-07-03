package com.assigment.auth.securityJwt.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private String userUUID;
    private String username;
    private String email;
    private List<String> roles;
}
