package com.assigment.auth.securityJwt.application.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.assigment.auth.securityJwt.application.response.MessageResponse;
import com.assigment.auth.securityJwt.application.request.LoginRequest;
import com.assigment.auth.securityJwt.application.request.SignupRequest;
import com.assigment.auth.securityJwt.application.response.JwtResponse;
import com.assigment.auth.securityJwt.application.response.UserDto;
import com.assigment.auth.securityJwt.domain.authenticationFacade.IAuthenticationFacade;
import com.assigment.auth.securityJwt.domain.jwt.JwtUtils;
import com.assigment.auth.securityJwt.domain.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assigment.auth.securityJwt.domain.models.ERole;
import com.assigment.auth.securityJwt.domain.models.Role;
import com.assigment.auth.securityJwt.domain.models.User;
import com.assigment.auth.securityJwt.domain.repository.RoleRepository;
import com.assigment.auth.securityJwt.domain.repository.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private IAuthenticationFacade authenticationFacade;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	private static Logger log = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
												 userDetails.getUserUUID(),
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/check")
	public ResponseEntity<?> checkUser() {
		UserDetailsImpl userDetails = authenticationFacade.getUserDetailsImpl();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return ResponseEntity.ok(new UserDto(userDetails.getUserUUID(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()));

		Set<Role> roles = new HashSet<>();
		//By default user get only user role.
		Role role = roleRepository.findByName(ERole.ROLE_BASIC_USER)
				.orElseGet(()-> roleRepository.insert(new Role(ERole.ROLE_BASIC_USER)));
		roles.add(role);

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
