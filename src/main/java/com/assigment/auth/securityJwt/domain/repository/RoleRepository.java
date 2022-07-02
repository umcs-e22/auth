package com.assigment.auth.securityJwt.domain.repository;

import java.util.Optional;

import com.assigment.auth.securityJwt.domain.models.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.assigment.auth.securityJwt.domain.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);


}
