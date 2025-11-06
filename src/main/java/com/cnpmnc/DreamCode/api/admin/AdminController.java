package com.cnpmnc.DreamCode.api.admin;

import com.cnpmnc.DreamCode.dto.request.*;
import com.cnpmnc.DreamCode.dto.response.DepartmentResponse;
import com.cnpmnc.DreamCode.dto.response.SupplierResponse;
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
    public ResponseEntity<?> createUser(@RequestBody @Valid UserCreationRequest request) {
        try {
            return ResponseEntity.ok(adminService.createUser(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return adminService.getUsers(page, size);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(adminService.getUser(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody UserUpdateRequest body) {
        try {
            return ResponseEntity.ok(adminService.updateUser(id, body));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/suppliers")
    public SupplierResponse createSupplier(@RequestBody @Valid SupplierCreationRequest request) {
        return adminService.createSupplier(request);
    }

    @GetMapping("/suppliers")
    public List<SupplierResponse> getSuppliers(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        return adminService.getSuppliers(page, size);
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<?> getSupplier(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(adminService.getSupplier(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Integer id, @Valid @RequestBody SupplierUpdateRequest body) {
        try {
            return ResponseEntity.ok(adminService.updateSupplier(id, body));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Integer id) {
        try {
            adminService.deleteSupplier(id);
            return ResponseEntity.ok("Supplier deleted successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @PostMapping("/departments")
    public DepartmentResponse createDepartment(@RequestBody @Valid DepartmentCreationRequest request) {
        return adminService.createDepartment(request);
    }

    @GetMapping("/departments")
    public List<DepartmentResponse> getDepartments(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return adminService.getDepartments(page, size);
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(adminService.getDepartment(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Integer id, @Valid @RequestBody DepartmentUpdateRequest body) {
        try {
            return ResponseEntity.ok(adminService.updateDepartment(id, body));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
