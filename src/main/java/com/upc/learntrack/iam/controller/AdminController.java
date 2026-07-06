package com.upc.learntrack.iam.controller;

import com.upc.learntrack.iam.dto.PendingUserDto;
import com.upc.learntrack.iam.dto.VerifyCodeRequestDto;
import com.upc.learntrack.iam.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<PendingUserDto>> getPendingUsers() {
        return ResponseEntity.ok(adminService.getPendingUsers());
    }

    @PostMapping("/approve/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> approveUser(@PathVariable Long userId) {
        adminService.approveUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reject/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> rejectUser(@PathVariable Long userId) {
        adminService.rejectUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Void> verifyCode(@Valid @RequestBody VerifyCodeRequestDto request) {
        adminService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }
}