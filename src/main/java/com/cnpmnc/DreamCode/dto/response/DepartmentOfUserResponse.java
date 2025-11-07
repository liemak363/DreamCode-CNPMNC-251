package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentOfUserResponse {
    Integer id;

    String name;

    Boolean isActive;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
