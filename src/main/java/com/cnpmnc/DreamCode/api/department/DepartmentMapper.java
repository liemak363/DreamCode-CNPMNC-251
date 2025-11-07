package com.cnpmnc.DreamCode.api.department;

import com.cnpmnc.DreamCode.dto.response.*;
import com.cnpmnc.DreamCode.model.*;

public class DepartmentMapper {
    
    public static AssetResponse toAssetSimpleResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .name(asset.getName())
                .location(asset.getLocation())
                .description(asset.getDescription())
                .purchaseDate(asset.getPurchaseDate())
                .value(asset.getValue())
                .status(asset.getStatus())
                .categoryId(asset.getCategory() != null ? asset.getCategory().getId() : null)
                .categoryName(asset.getCategory() != null ? asset.getCategory().getName() : null)
                .supplierId(asset.getSupplier() != null ? asset.getSupplier().getId() : null)
                .supplierName(asset.getSupplier() != null ? asset.getSupplier().getName() : null)
                .build();
    }
    
    public static AssetRequestResponse toAssetRequestResponse(AssetRequest request) {
        return AssetRequestResponse.builder()
                .id(request.getId())
                .type(request.getType())
                .status(request.getStatus().name())
                .reason(request.getReason())
                .priority(request.getPriority())
                .notes(request.getNotes())
                .requesterId(request.getRequester() != null ? request.getRequester().getId() : null)
                .requesterName(request.getRequester() != null ? request.getRequester().getUserName() : null)
                .departmentId(request.getDepartment() != null ? request.getDepartment().getId() : null)
                .departmentName(request.getDepartment() != null ? request.getDepartment().getName() : null)
                .assetId(request.getAsset() != null ? request.getAsset().getId() : null)
                .assetName(request.getAsset() != null ? request.getAsset().getName() : null)
                .createdAt(request.getCreatedAt())
                .build();
    }
}