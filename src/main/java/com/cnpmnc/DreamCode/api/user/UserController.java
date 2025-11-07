package com.cnpmnc.DreamCode.api.user;

import com.cnpmnc.DreamCode.dto.request.AccidentReportRequest;
import com.cnpmnc.DreamCode.dto.response.AccidentLogResponse;
import com.cnpmnc.DreamCode.dto.response.AssetResponse;
import com.cnpmnc.DreamCode.dto.response.UserResponse;
import com.cnpmnc.DreamCode.security.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    
    UserService userService;

    /**
     * Lấy thông tin profile của user hiện tại
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
    public ResponseEntity<UserResponse> getMyProfile(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getUserProfile(userDetails.getId()));
    }

    /**
     * Lấy danh sách assets đang được user sử dụng
     */
    @GetMapping("/my-assets")
    @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
    public ResponseEntity<Page<AssetResponse>> getMyAssets(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getMyAssets(userDetails.getId(), page, size));
    }

    /**
     * Lấy chi tiết 1 asset mà user đang sử dụng
     */
    @GetMapping("/my-assets/{assetId}")
    @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
    public ResponseEntity<?> getMyAssetDetail(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Integer assetId
    ) {
        try {
            return ResponseEntity.ok(userService.getMyAssetDetail(userDetails.getId(), assetId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Báo cáo sự cố asset
     * POST /api/user/accidents
     * Body: {
     *   "assetId": 1,
     *   "title": "Màn hình bị vỡ",
     *   "description": "Màn hình laptop bị vỡ góc dưới bên phải",
     *   "imageKeys": ["img1.jpg", "img2.jpg"]
     * }
     */
    @PostMapping("/accidents")
    @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
    public ResponseEntity<?> reportAccident(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody AccidentReportRequest request
    ) {
        try {
            AccidentLogResponse response = userService.createAccidentReport(
                userDetails.getId(), 
                request
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy danh sách accident logs của user
     * GET /api/user/accidents?page=0&size=10
     */
    @GetMapping("/accidents")
    @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
    public ResponseEntity<Page<AccidentLogResponse>> getMyAccidents(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getMyAccidentLogs(userDetails.getId(), page, size));
    }

    /**
     * Lấy chi tiết 1 accident log
     */
    @GetMapping("/accidents/{accidentId}")
    @PreAuthorize("hasAnyRole('USER', 'DEPT_MANAGER', 'ASSET_MANAGER', 'ADMIN')")
    public ResponseEntity<?> getAccidentDetail(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Integer accidentId
    ) {
        try {
            return ResponseEntity.ok(userService.getAccidentDetail(userDetails.getId(), accidentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}