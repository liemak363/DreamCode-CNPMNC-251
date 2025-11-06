package com.cnpmnc.DreamCode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private String password;

    private String avatarKey;

    private Boolean isActive;

    private Set<String> roles;
}
