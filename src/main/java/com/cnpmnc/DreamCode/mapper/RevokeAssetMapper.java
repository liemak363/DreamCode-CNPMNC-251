package com.cnpmnc.DreamCode.mapper;

import com.cnpmnc.DreamCode.dto.request.RevokeAssetRequest;
import com.cnpmnc.DreamCode.dto.response.RevokeAssetResponse;
import com.cnpmnc.DreamCode.model.AssetRevokeLog;
import org.mapstruct.*;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RevokeAssetMapper {

    AssetRevokeLog toAssetRevokeLog(RevokeAssetRequest request);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    RevokeAssetResponse toRevokeAssetResponse(AssetRevokeLog assetRevokeLog);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAssetRevokeLog(@MappingTarget AssetRevokeLog assetRevokeLog, RevokeAssetRequest request);
}
