package com.cnpmnc.DreamCode.mapper;

import com.cnpmnc.DreamCode.dto.response.PermissionResponse;
import com.cnpmnc.DreamCode.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    PermissionResponse toPermissionResponse(Permission permission);
}
