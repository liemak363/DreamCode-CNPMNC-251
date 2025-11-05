package com.cnpmnc.DreamCode.api.auth;

import com.cnpmnc.DreamCode.security.JwtService;
import com.cnpmnc.DreamCode.security.CustomUserDetails;
import com.cnpmnc.DreamCode.security.TokenBlacklistService;
import com.cnpmnc.DreamCode.repository.UserRepository;
import com.cnpmnc.DreamCode.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
            UserRepository userRepository, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userDetails.getId());
        claims.put("roles", userRepository.findByUserName(userDetails.getUsername()).map(User::getRoles).orElse(null));
        String token = jwtService.generateToken(userDetails.getUsername(), claims);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", 400,
                            "error", "Bad Request",
                            "message", "Missing or invalid Authorization header"));
        }
        String token = authHeader.substring(7);
        long exp = jwtService.extractExpirationEpochMilli(token);
        tokenBlacklistService.blacklist(token, exp);
        return ResponseEntity.noContent().build();
    }

    // Signup has been removed as requested. Use DataInitializer or admin flows to
    // manage users.
}
