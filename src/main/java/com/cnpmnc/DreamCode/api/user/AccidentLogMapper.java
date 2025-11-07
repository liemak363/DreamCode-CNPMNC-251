// package com.cnpmnc.DreamCode.api.user;

// import com.cnpmnc.DreamCode.dto.response.AccidentLogResponse;
// import com.cnpmnc.DreamCode.model.AssetAccidentLog;
// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;


// @Mapper(componentModel = "spring")
// public interface AccidentLogMapper {
    
//     @Mapping(source = "asset.id", target = "assetId")
//     @Mapping(source = "asset.name", target = "assetName")
//     @Mapping(source = "asset.location", target = "assetLocation")
//     AccidentLogResponse toResponse(AssetAccidentLog log);
// }