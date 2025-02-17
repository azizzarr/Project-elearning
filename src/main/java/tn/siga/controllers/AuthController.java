package tn.siga.controllers;

import java.util.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.siga.Interfaces.IEmailService;
import tn.siga.entities.Role;
import tn.siga.entities.User;
import tn.siga.repositories.RoleRepository;
import tn.siga.repositories.UserRepository;
import tn.siga.security.jwtUtils.JwtUtils;
import tn.siga.springjwt.payload.request.LoginRequest;
import tn.siga.springjwt.payload.request.SignupRequest;
import tn.siga.springjwt.payload.response.JwtResponse;
import tn.siga.springjwt.payload.response.MessageResponse;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        }catch (Exception e) {
            throw new Exception("bad creds");
        }
        final UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        return ResponseEntity.ok(new JwtResponse(user.getUsername(), jwtUtils.generateToken(user)));


    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles(); // Get roles from the request
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role 'ROLE_USER' is not found."));
            roles.add(userRole);

        } else {
            // Process provided roles
            strRoles.forEach(role -> {
                System.out.println("Looking for role: " + role);
                Role userRole = roleRepository.findByName(role)
                        .orElseThrow(() -> new RuntimeException("Error: Role '" + role + "' is not found."));
                roles.add(userRole);
            });
        }

        // Set roles for the user
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }



    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (jwtUtils.validateRefreshToken(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newToken = jwtUtils.generateToken(userDetails);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("token", newToken);
            }});
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        emailService.sendPasswordResetEmail(email);
        return ResponseEntity.ok("Password reset email sent");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");
        emailService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }
}

