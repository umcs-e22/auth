package com.assigment.auth;

import com.assigment.auth.securityJwt.domain.models.ERole;
import com.assigment.auth.securityJwt.domain.models.Role;
import com.assigment.auth.securityJwt.domain.models.User;
import com.assigment.auth.securityJwt.domain.repository.RoleRepository;
import com.assigment.auth.securityJwt.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class AuthApplication {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @PostConstruct
    public void initUsers() {
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(()-> roleRepository.insert(new Role(ERole.ROLE_ADMIN)));
        roles.add(role);

        List<User> users = Stream.of(
                new User("admin","admin@admin.com", encoder.encode("admin") , roles)
        ).collect(Collectors.toList());

        if(repository.findByEmail("admin@admin.com").isEmpty()){
            repository.saveAll(users);
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
