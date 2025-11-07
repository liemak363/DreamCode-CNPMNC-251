package com.cnpmnc.DreamCode.dto.request;

import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignAssetRequest {
    Integer assetId;
    Set<Integer> userIds;

    String notes;

    LocalDateTime beginTime;

    String approvalStatus = ApprovalStatus.APPROVED.toString();

    String approvalNotes;
}
