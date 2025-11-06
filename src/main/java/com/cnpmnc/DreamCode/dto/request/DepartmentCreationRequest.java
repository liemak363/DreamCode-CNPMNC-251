package com.cnpmnc.DreamCode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepartmentCreationRequest {
    @NotBlank(message = "Department name is required and cannot be blank")
    String name;

    Boolean isActive = true;

    Integer managerId;
}
