package com.cnpmnc.DreamCode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {
    @NotBlank(message = "Username is required and cannot be blank")
    private String userName;

    @NotBlank(message = "Password is required and cannot be blank")
    private String password;

    private String avatarKey;

    private Boolean isActive = true;

    private Integer departmentId;
    
    private Set<String> roles;
}
