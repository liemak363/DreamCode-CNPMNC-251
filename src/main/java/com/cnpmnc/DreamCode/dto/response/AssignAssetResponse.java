package com.cnpmnc.DreamCode.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignAssetResponse {
    Integer id;
    Integer assetId;
    Set<Integer> userIds;

    String notes;

    String approvalStatus;

    String approvalNotes;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    LocalDateTime beginTime;

    LocalDateTime endTime;
}
