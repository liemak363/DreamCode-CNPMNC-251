package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentResponse {
    Integer id;

    String name;

    String code;

    Boolean isActive;

    UserResponse manager;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
