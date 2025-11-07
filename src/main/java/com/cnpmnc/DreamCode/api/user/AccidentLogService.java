// package com.cnpmnc.DreamCode.api.user;

// import com.cnpmnc.DreamCode.dto.request.AccidentReportRequest;
// import com.cnpmnc.DreamCode.model.Asset;
// import com.cnpmnc.DreamCode.model.AssetAccidentLog;
// import com.cnpmnc.DreamCode.model.User;
// import com.cnpmnc.DreamCode.model.enumType.AccidentStatus;
// import com.cnpmnc.DreamCode.repository.AssetAccidentLogRepository;
// import com.cnpmnc.DreamCode.repository.AssetRepository;
// import com.cnpmnc.DreamCode.repository.AssetUsageLogRepository;
// import org.springframework.stereotype.Service;

// @Service
// public class AccidentLogService {
    
//     private final AssetAccidentLogRepository accidentLogRepository;
//     private final AssetRepository assetRepository;
//     private final AssetUsageLogRepository usageLogRepository;
    
//     public AssetAccidentLog createAccidentReport(
//         Integer assetId, 
//         AccidentReportRequest request,
//         User currentUser // Từ SecurityContext
//     ) {
//         // 1. Validate asset tồn tại
//         Asset asset = assetRepository.findById(assetId)
//             .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        
//         // 2. Kiểm tra user có quyền báo cáo không (đang sử dụng asset)
//         boolean isCurrentUser = usageLogRepository.existsActiveUsageByAssetAndUser(
//             assetId, 
//             currentUser.getId(), 
//             LocalDateTime.now()
//         );
        
//         if (!isCurrentUser) {
//             throw new IllegalStateException("You are not currently using this asset");
//         }
        
//         // 3. Tạo accident log với snapshot
//         AssetAccidentLog log = new AssetAccidentLog();
//         log.setAsset(asset);
//         log.setTitle(request.getTitle());
//         log.setDescription(request.getDescription());
//         log.setImageKeys(request.getImageKeys());
//         log.setStatus(AccidentStatus.PENDING);
        
//         // === SNAPSHOT DATA ===
//         log.setReporterUserId(currentUser.getId());
//         log.setReporterUserName(currentUser.getUserName());
        
//         if (currentUser.getDepartment() != null) {
//             log.setReporterDepartmentName(currentUser.getDepartment().getName());
//         }
        
//         return accidentLogRepository.save(log);
//     }
// }
