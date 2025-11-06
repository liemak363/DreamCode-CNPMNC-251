package com.cnpmnc.DreamCode.api.admin;

import com.cnpmnc.DreamCode.dto.request.UserCreationRequest;
import com.cnpmnc.DreamCode.dto.request.UserUpdateRequest;
import com.cnpmnc.DreamCode.dto.response.UserResponse;
import com.cnpmnc.DreamCode.model.Role;
import com.cnpmnc.DreamCode.model.User;
import com.cnpmnc.DreamCode.repository.RoleRepository;
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
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        for (String role : request.getRoles()) {
            roleRepository.findByName(role).ifPresent(roles::add);
        }

        user.setRoles(roles);

        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    public List<UserResponse> getUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending())).stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(Integer id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id)));
    }

    public UserResponse updateUser(Integer userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        userMapper.updateUser(user, request);
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            HashSet<Role> roles = new HashSet<>();
            for (String role : request.getRoles()) {
                roleRepository.findByName(role).ifPresent(roles::add);
            }
            user.setRoles(roles);
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }
}
