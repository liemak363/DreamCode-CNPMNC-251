package com.cnpmnc.DreamCode.mapper;

import com.cnpmnc.DreamCode.dto.request.RetireAssetRequest;
import com.cnpmnc.DreamCode.dto.response.RetireAssetResponse;
import com.cnpmnc.DreamCode.model.AssetRetireLog;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RetireAssetMapper {

    AssetRetireLog toAssetRetireLog(RetireAssetRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    RetireAssetResponse toRetireAssetResponse(AssetRetireLog assetRetireLog);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAssetRetireLog(@MappingTarget AssetRetireLog assetRetireLog, RetireAssetRequest request);
}
