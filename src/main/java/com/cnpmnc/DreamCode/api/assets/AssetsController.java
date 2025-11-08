package com.cnpmnc.DreamCode.api.assets;

import com.cnpmnc.DreamCode.dto.request.*;
import com.cnpmnc.DreamCode.dto.response.AssetResponse;
import com.cnpmnc.DreamCode.dto.response.AssetUsageLogResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetsController {

    private final AssetService assetService;

    // Health check
    @GetMapping("/health")
    public String health() {
        return "assets-ok";
    }

    // ========== TỪ NHÁNH THANG: CRUD Tài sản ==========

    // 1. Tạo tài sản mới
    @PostMapping
    public ResponseEntity<?> createAsset(@Valid @RequestBody AssetCreationRequest request) {
        try {
            AssetResponse created = assetService.createAsset(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // 2. Danh sách tra cứu tài sản (có filter)
    @GetMapping
    public Page<AssetResponse> searchAssets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return assetService.searchAssets(name, departmentId, categoryId, page, size);
    }

    // 3. Lấy chi tiết tài sản
    @GetMapping("/{id}")
    public ResponseEntity<?> getAsset(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(assetService.getAsset(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // 4. Cập nhật tài sản
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable Integer id,
                                         @Valid @RequestBody AssetUpdateRequest request) {
        try {
            return ResponseEntity.ok(assetService.updateAsset(id, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // 5. Xóa tài sản
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Integer id) {
        try {
            assetService.deleteAsset(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // 6. Lịch sử sử dụng tài sản
    @GetMapping("/{id}/usage-logs")
    public ResponseEntity<?> getAssetUsageLogs(@PathVariable Integer id,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        try {
            Page<AssetUsageLogResponse> logs = assetService.getAssetUsageLogs(id, page, size);
            return ResponseEntity.ok(logs);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    // ========== TỪ NHÁNH MAIN: Assign & Revoke ==========

    // 7. Phân bổ tài sản cho người dùng
    @PostMapping("/assign")
    public ResponseEntity<?> assignAsset(@RequestBody @Valid AssignAssetRequest request) {
        try {
            return ResponseEntity.ok(assetService.assignAsset(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    // 8. Thu hồi tài sản
    @PostMapping("/revoke")
    public ResponseEntity<?> revokeAsset(@RequestBody @Valid RevokeAssetRequest request) {
        try {
            return ResponseEntity.ok(assetService.revokeAsset(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    //9. Thanh lý tài sản
    @PostMapping("/retire")
    public ResponseEntity<?> retireAsset(@RequestBody @Valid RetireAssetRequest request) {
        try {
            return ResponseEntity.ok(assetService.retireAsset(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
