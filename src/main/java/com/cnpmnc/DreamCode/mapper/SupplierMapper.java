package com.cnpmnc.DreamCode.mapper;

import com.cnpmnc.DreamCode.dto.request.SupplierCreationRequest;
import com.cnpmnc.DreamCode.dto.request.SupplierUpdateRequest;
import com.cnpmnc.DreamCode.dto.response.SupplierResponse;
import com.cnpmnc.DreamCode.model.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SupplierMapper {
    Supplier toSupplier(SupplierCreationRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    SupplierResponse toSupplierResponse(Supplier supplier);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSupplier(@MappingTarget Supplier supplier, SupplierUpdateRequest request);
}
