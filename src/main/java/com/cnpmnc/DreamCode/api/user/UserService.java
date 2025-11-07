// package com.cnpmnc.DreamCode.api.user;

// import com.cnpmnc.DreamCode.model.Asset;
// import com.cnpmnc.DreamCode.model.AssetUsageLog;
// import com.cnpmnc.DreamCode.model.User;
// import com.cnpmnc.DreamCode.repository.AssetRepository;
// import com.cnpmnc.DreamCode.repository.AssetUsageLogRepository;
// import com.cnpmnc.DreamCode.repository.UserRepository;
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Sort;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;

// @Service
// @RequiredArgsConstructor
// @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// public class UserService {
    
//     UserRepository userRepository;
//     AssetUsageLogRepository usageLogRepository;
//     AssetRepository assetRepository;
//     UserMapper userMapper;

//     public UserResponse getUserProfile(Integer userId) {
//         User user = userRepository.findById(userId)
//             .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
//         return userMapper.toProfileResponse(user);
//     }

//     public Page<MyAssetResponse> getMyAssets(Integer userId, int page, int size) {
//         // Tìm tất cả asset đang được user sử dụng (usage log active)
//         Page<Asset> assets = assetRepository.findActiveAssetsByUserId(
//             userId,
//             LocalDateTime.now(),
//             PageRequest.of(page, size, Sort.by("name").ascending())
//         );
        
//         return assets.map(userMapper::toMyAssetResponse);
//     }

//     public AssetDetailResponse getMyAssetDetail(Integer userId, Integer assetId) {
//         // Kiểm tra user có đang sử dụng asset này không
//         Asset asset = assetRepository.findById(assetId)
//             .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
        
//         boolean isCurrentUser = usageLogRepository.existsActiveUsageByAssetAndUser(
//             assetId, userId, LocalDateTime.now()
//         );
        
//         if (!isCurrentUser) {
//             throw new IllegalStateException("You are not currently using this asset");
//         }
        
//         return userMapper.toAssetDetailResponse(asset);
//     }

//     public Page<UsageHistoryResponse> getMyUsageHistory(Integer userId, int page, int size) {
//         Page<AssetUsageLog> history = usageLogRepository.findByUserIdOrderByBeginTimeDesc(
//             userId,
//             PageRequest.of(page, size)
//         );

//         return history.map(userMapper::toUsageHistoryResponse);
//     }
// }