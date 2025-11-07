package com.cnpmnc.DreamCode.dto.request;

import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignAssetRequest {
    Integer assetId;
    Set<Integer> userIds;

    private String notes;

    private String approvalStatus = ApprovalStatus.APPROVED.toString();

    private String approvalNotes;
}
