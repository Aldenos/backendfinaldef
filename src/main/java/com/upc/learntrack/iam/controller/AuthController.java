package com.upc.learntrack.iam.controller;

import com.upc.learntrack.iam.dto.LoginRequestDto;
import com.upc.learntrack.iam.dto.RegisterRequestDto;
import com.upc.learntrack.iam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        String token = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDto request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh() {
        return ResponseEntity.ok(Map.of("message", "Refresh endpoint pendiente de implementación real con BD"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada en backend. (El frontend debe eliminar el token)"));
    }
}