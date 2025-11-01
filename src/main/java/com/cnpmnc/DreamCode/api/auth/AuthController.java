package com.cnpmnc.DreamCode.api.auth;

import com.cnpmnc.DreamCode.security.JwtService;
import com.cnpmnc.DreamCode.security.CustomUserDetails;
import com.cnpmnc.DreamCode.repository.UserRepository;
import com.cnpmnc.DreamCode.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userDetails.getId());
        claims.put("roles", userRepository.findByUserName(userDetails.getUsername()).map(User::getRoles).orElse(null));
        String token = jwtService.generateToken(userDetails.getUsername(), claims);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    // Signup has been removed as requested. Use DataInitializer or admin flows to manage users.
}
