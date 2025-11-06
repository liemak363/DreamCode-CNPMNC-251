package com.cnpmnc.DreamCode.api.admin;

import com.cnpmnc.DreamCode.dto.request.UserCreationRequest;
import com.cnpmnc.DreamCode.dto.request.UserUpdateRequest;
import com.cnpmnc.DreamCode.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminController {

    AdminService adminService;

    @PostMapping("/users")
    UserResponse createUser(@RequestBody @Valid UserCreationRequest request) {
        return adminService.createUser(request);
    }

    @GetMapping("/users")
    public List<UserResponse> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return adminService.getUsers(page, size);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> get(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(adminService.getUser(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest body) {
        try {
            return ResponseEntity.ok(adminService.updateUser(id, body));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
