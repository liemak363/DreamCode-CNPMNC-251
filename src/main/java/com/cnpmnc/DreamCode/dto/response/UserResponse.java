package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Integer id;

    String userName;

    String avatarKey;

    Boolean isActive;

    String departmentName;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Set<RoleResponse> roles;
}
