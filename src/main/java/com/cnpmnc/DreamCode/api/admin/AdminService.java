package com.cnpmnc.DreamCode.api.admin;

import com.cnpmnc.DreamCode.constant.PredefinedRole;
import com.cnpmnc.DreamCode.dto.request.*;
import com.cnpmnc.DreamCode.dto.response.DepartmentResponse;
import com.cnpmnc.DreamCode.dto.response.SupplierResponse;
import com.cnpmnc.DreamCode.dto.response.UserResponse;
import com.cnpmnc.DreamCode.mapper.DepartmentMapper;
import com.cnpmnc.DreamCode.mapper.SupplierMapper;
import com.cnpmnc.DreamCode.mapper.UserMapper;
import com.cnpmnc.DreamCode.model.Department;
import com.cnpmnc.DreamCode.model.Role;
import com.cnpmnc.DreamCode.model.Supplier;
import com.cnpmnc.DreamCode.model.User;
import com.cnpmnc.DreamCode.repository.DepartmentRepository;
import com.cnpmnc.DreamCode.repository.RoleRepository;
import com.cnpmnc.DreamCode.repository.SupplierRepository;
import com.cnpmnc.DreamCode.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    DepartmentRepository departmentRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    SupplierMapper supplierMapper;
    DepartmentMapper departmentMapper;
    SupplierRepository supplierRepository;

    public UserResponse createUser(UserCreationRequest request) {

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Validate that DEPT_MANAGER role is not in the request
        if (request.getRoles().contains(PredefinedRole.DEPT_MANAGER_ROLE)) {
            throw new IllegalArgumentException("Cannot manually assign DEPT_MANAGER role. This role is automatically assigned when a user becomes a department manager.");
        }

        HashSet<Role> roles = new HashSet<>();
        for (String role : request.getRoles()) {
            roleRepository.findByName(role).ifPresent(roles::add);
        }


        user.setRoles(roles);

        // Check if the department exists
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department with ID " + request.getDepartmentId() + " does not exist."));
            user.setDepartment(department);
        }

        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending())).stream().map(userMapper::toUserResponse).toList();
    }

    public List<UserResponse> getActiveUsers(int page, int size) {
        return userRepository.findAllByIsActiveTrue(PageRequest.of(page, size, Sort.by("id").descending())).stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(Integer id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id)));
    }

    public UserResponse updateUser(Integer userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        userMapper.updateUser(user, request);
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Department with ID " + request.getDepartmentId() + " does not exist."));
            user.setDepartment(department);
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            // Validate that DEPT_MANAGER role is not in the request
            if (request.getRoles().contains(PredefinedRole.DEPT_MANAGER_ROLE)) {
                throw new IllegalArgumentException("Cannot manually assign DEPT_MANAGER role. This role is automatically assigned when a user becomes a department manager.");
            }

            HashSet<Role> roles = new HashSet<>();
            for (String role : request.getRoles()) {
                roleRepository.findByName(role).ifPresent(roles::add);
            }
            user.setRoles(roles);
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public SupplierResponse createSupplier(SupplierCreationRequest request) {
        Supplier supplier = supplierMapper.toSupplier(request);

        supplier = supplierRepository.save(supplier);

        return supplierMapper.toSupplierResponse(supplier);
    }

    public List<SupplierResponse> getSuppliers(int page, int size) {
        return supplierRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending())).stream().map(supplierMapper::toSupplierResponse).toList();
    }

    public List<SupplierResponse> getActiveSuppliers(int page, int size) {
        return supplierRepository.findAllByIsActiveTrue(PageRequest.of(page, size, Sort.by("id").descending())).stream().map(supplierMapper::toSupplierResponse).toList();
    }

    public SupplierResponse getSupplier(Integer id) {
        return supplierMapper.toSupplierResponse(supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id)));
    }

    public SupplierResponse updateSupplier(Integer supplierId, SupplierUpdateRequest request) {
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + supplierId));

        supplierMapper.updateSupplier(supplier, request);

        return supplierMapper.toSupplierResponse(supplierRepository.save(supplier));
    }

    public void deleteSupplier(Integer supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + supplierId));

        // Check if supplier has any associated assets using efficient count query
        long assetCount = supplierRepository.countAssetsBySupplierId(supplierId);
        if (assetCount > 0) {
            throw new IllegalStateException("Cannot delete supplier. There are " + assetCount + " asset(s) associated with this supplier.");
        }

        supplierRepository.delete(supplier);
        log.info("Supplier with id {} has been deleted successfully", supplierId);
    }

    public DepartmentResponse createDepartment(DepartmentCreationRequest request) {
        Department department = departmentMapper.toDepartment(request);

        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getManagerId()));

        // Check if manager has DEPT_MANAGER role, if not, add it
        boolean hasDeptManagerRole = manager.getRoles().stream()
                .anyMatch(role -> role.getName().equals(PredefinedRole.DEPT_MANAGER_ROLE));

        if (!hasDeptManagerRole) {
            Role deptManagerRole = roleRepository.findByName(PredefinedRole.DEPT_MANAGER_ROLE)
                    .orElseThrow(() -> new IllegalArgumentException(PredefinedRole.DEPT_MANAGER_ROLE + " role not found"));
            manager.getRoles().add(deptManagerRole);
            userRepository.save(manager);
        }

        department.setManager(manager);
        department = departmentRepository.save(department);

        return departmentMapper.toDepartmentResponse(department);
    }

    public List<DepartmentResponse> getDepartments(int page, int size) {
        return departmentRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending())).stream().map(departmentMapper::toDepartmentResponse).toList();
    }

    public List<DepartmentResponse> getActiveDepartments(int page, int size) {
        return departmentRepository.findAllByIsActiveTrue(PageRequest.of(page, size, Sort.by("id").descending())).stream().map(departmentMapper::toDepartmentResponse).toList();
    }

    public DepartmentResponse getDepartment(Integer id) {
        return departmentMapper.toDepartmentResponse(departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + id)));
    }

    public DepartmentResponse updateDepartment(Integer departmentId, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + departmentId));

        departmentMapper.updateDepartment(department, request);
        if (request.getManagerId() != null) {
            // Get old manager before updating
            User oldManager = department.getManager();

            // Get new manager
            User newManager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getManagerId()));

            // Remove DEPT_MANAGER role from old manager if they exist and are different from new manager
            if (oldManager != null && !oldManager.getId().equals(newManager.getId())) {
                oldManager.getRoles().removeIf(role -> role.getName().equals(PredefinedRole.DEPT_MANAGER_ROLE));
                userRepository.save(oldManager);
            }

            // Check if new manager has DEPT_MANAGER role, if not, add it
            boolean hasDeptManagerRole = newManager.getRoles().stream()
                    .anyMatch(role -> role.getName().equals(PredefinedRole.DEPT_MANAGER_ROLE));

            if (!hasDeptManagerRole) {
                Role deptManagerRole = roleRepository.findByName(PredefinedRole.DEPT_MANAGER_ROLE)
                        .orElseThrow(() -> new IllegalArgumentException(PredefinedRole.DEPT_MANAGER_ROLE + " role not found"));
                newManager.getRoles().add(deptManagerRole);
                userRepository.save(newManager);
            }

            department.setManager(newManager);
        }
        return departmentMapper.toDepartmentResponse(departmentRepository.save(department));
    }
}
