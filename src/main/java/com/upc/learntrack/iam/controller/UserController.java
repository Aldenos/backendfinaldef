package com.upc.learntrack.iam.controller;

import com.upc.learntrack.iam.dto.UserDto;
import com.upc.learntrack.iam.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.findByEmail(principal.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(Principal principal, @Valid @RequestBody UserDto dto) {
        UserDto currentUser = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(userService.update(currentUser.getId(), dto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(Principal principal) {
        UserDto currentUser = userService.findByEmail(principal.getName());
        userService.delete(currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Valid @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}