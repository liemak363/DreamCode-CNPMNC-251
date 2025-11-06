package com.cnpmnc.DreamCode.api.admin;

import com.cnpmnc.DreamCode.dto.request.DepartmentCreationRequest;
import com.cnpmnc.DreamCode.dto.request.DepartmentUpdateRequest;
import com.cnpmnc.DreamCode.dto.response.DepartmentResponse;
import com.cnpmnc.DreamCode.model.Department;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DepartmentMapper {
    Department toDepartment(DepartmentCreationRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    DepartmentResponse toDepartmentResponse(Department Department);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDepartment(@MappingTarget Department Department, DepartmentUpdateRequest request);
}
