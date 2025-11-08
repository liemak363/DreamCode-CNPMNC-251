package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RetireAssetResponse {
    Integer id;
    Integer assetId;
    LocalDateTime retiredTime;
    Integer retiredById;
    String reason;
    Double salvageValue;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
