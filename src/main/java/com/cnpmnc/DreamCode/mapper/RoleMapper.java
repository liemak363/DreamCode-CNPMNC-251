package com.cnpmnc.DreamCode.mapper;

import com.cnpmnc.DreamCode.dto.response.RoleResponse;
import com.cnpmnc.DreamCode.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    RoleResponse toRoleResponse(Role role);
}
