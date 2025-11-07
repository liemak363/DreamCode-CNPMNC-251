package com.cnpmnc.DreamCode.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentUpdateRequest {
    String name;

    String code;

    Boolean isActive;

    Integer managerId;
}
