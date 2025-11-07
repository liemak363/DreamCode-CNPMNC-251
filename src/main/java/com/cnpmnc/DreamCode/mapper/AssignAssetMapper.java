package com.cnpmnc.DreamCode.mapper;

import com.cnpmnc.DreamCode.dto.request.AssignAssetRequest;
import com.cnpmnc.DreamCode.dto.response.AssignAssetResponse;
import com.cnpmnc.DreamCode.model.AssetUsageLog;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface AssignAssetMapper {

    AssetUsageLog toAssetUsageLog(AssignAssetRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    AssignAssetResponse toAssignAssetResponse(AssetUsageLog assetUsageLog);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAssetUsageLog(@MappingTarget AssetUsageLog assetUsageLog, AssignAssetRequest request);
}
