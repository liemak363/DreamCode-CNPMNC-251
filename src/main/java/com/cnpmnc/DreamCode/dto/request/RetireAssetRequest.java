package com.cnpmnc.DreamCode.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RetireAssetRequest {
    Integer assetId;
    LocalDate retiredTime;
    String reason;
    Double salvageValue;
}
