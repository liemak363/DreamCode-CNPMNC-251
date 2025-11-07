// package com.cnpmnc.DreamCode.api.user;

// import com.cnpmnc.DreamCode.security.CustomUserDetails;
// import lombok.AccessLevel;
// import lombok.RequiredArgsConstructor;
// import lombok.experimental.FieldDefaults;
// import org.springframework.data.domain.Page;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/user")
// @RequiredArgsConstructor
// @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// public class UserController {
    
//     UserService userService;

//     @GetMapping("/me")
//     @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
//     public ResponseEntity<UserResponse> getMyProfile(
//         @AuthenticationPrincipal CustomUserDetails userDetails
//     ) {
//         return ResponseEntity.ok(userService.getUserProfile(userDetails.getId()));
//     }

//     @GetMapping("/my-assets")
//     @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
//     public ResponseEntity<Page<MyAssetResponse>> getMyAssets(
//         @AuthenticationPrincipal CustomUserDetails userDetails,
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "10") int size
//     ) {
//         return ResponseEntity.ok(userService.getMyAssets(userDetails.getId(), page, size));
//     }

 
//     @GetMapping("/my-assets/{assetId}")
//     @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
//     public ResponseEntity<AssetDetailResponse> getMyAssetDetail(
//         @AuthenticationPrincipal CustomUserDetails userDetails,
//         @PathVariable Integer assetId
//     ) {
//         return ResponseEntity.ok(userService.getMyAssetDetail(userDetails.getId(), assetId));
//     }


//     @GetMapping("/my-usage-history")
//     @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
//     public ResponseEntity<Page<UsageHistoryResponse>> getMyUsageHistory(
//         @AuthenticationPrincipal CustomUserDetails userDetails,
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "10") int size
//     ) {
//         return ResponseEntity.ok(userService.getMyUsageHistory(userDetails.getId(), page, size));
//     }
// }