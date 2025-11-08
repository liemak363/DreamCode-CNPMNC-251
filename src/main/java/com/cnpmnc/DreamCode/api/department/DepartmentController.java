package com.cnpmnc.DreamCode.api.department;

import com.cnpmnc.DreamCode.dto.response.*;
import com.cnpmnc.DreamCode.model.*;
import com.cnpmnc.DreamCode.model.enumType.ApprovalStatus;
import com.cnpmnc.DreamCode.repository.*;
import com.cnpmnc.DreamCode.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AssetRequestRepository requestRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/health")
    public String health() {
        return "department-ok";
    }

    private User getUser(Authentication auth) {
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
        return userRepository.findById(ud.getId()).orElseThrow();
    }

    private List<Department> getManagedDepartments(User user) {
        List<Department> depts = departmentRepository.findByManager(user);
        if (depts.isEmpty()) {
            throw new IllegalArgumentException("You are not a manager of any department");
        }
        return depts;
    }

    private boolean isManagingDepartment(User user, Integer departmentId) {
        return departmentRepository.existsByIdAndManagerId(departmentId, user.getId());
    }

    @GetMapping("/assets")
    public ResponseEntity<?> getAllManagedAssets(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User user = getUser(auth);
            List<Department> depts = getManagedDepartments(user);
            
            List<Integer> deptIds = depts.stream()
                .map(Department::getId)
                .toList();
            
            Page<Asset> assets = assetRepository.findByDepartmentIdIn(
                deptIds, 
                PageRequest.of(page, size, org.springframework.data.domain.Sort.by("department.name", "name").ascending())
            );
            
            Page<AssetResponse> response = assets.map(DepartmentMapper::toAssetSimpleResponse);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    @PostMapping("/requests")
    public ResponseEntity<?> createRequest(
            Authentication auth,
            @RequestBody Map<String, Object> body) {
        try {
            User user = getUser(auth);

            if (body.get("departmentId") == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "departmentId is required"));
            }

            Integer departmentId = (Integer) body.get("departmentId");

            if (!isManagingDepartment(user, departmentId)) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "You are not managing this department"));
            }

            Department dept = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Department not found"));

            String type = (String) body.get("type");
            if (type == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "type is required"));
            }

            AssetRequest req = new AssetRequest();
            req.setType(type);
            req.setStatus(ApprovalStatus.PENDING);
            req.setRequester(user);
            req.setDepartment(dept);
            req.setReason((String) body.get("reason"));
            req.setPriority((Integer) body.getOrDefault("priority", 2));

            if (body.get("assetId") != null) {
                Integer assetId = (Integer) body.get("assetId");
                Asset asset = assetRepository.findById(assetId).orElse(null);
                if (asset != null && !asset.getDepartment().getId().equals(dept.getId())) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Asset does not belong to this department"));
                }
                req.setAsset(asset);
            }

            req = requestRepository.save(req);

            return ResponseEntity.ok(DepartmentMapper.toAssetRequestResponse(req));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getMyRequests(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User user = getUser(auth);
            Page<AssetRequest> requests = requestRepository.findByRequesterId(
                    user.getId(), PageRequest.of(page, size));
            Page<AssetRequestResponse> response = requests.map(DepartmentMapper::toAssetRequestResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user-requests")
    public ResponseEntity<?> getUserRequestsLegacy(
            Authentication auth,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User user = getUser(auth);
            List<Department> depts = getManagedDepartments(user);

            Integer targetDeptId = departmentId != null ? departmentId : depts.get(0).getId();

            if (!isManagingDepartment(user, targetDeptId)) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "You are not managing this department"));
            }

            Page<AssetRequest> requests = requestRepository.findByDepartmentIdAndStatus(
                    targetDeptId, ApprovalStatus.PENDING, PageRequest.of(page, size));

            Page<AssetRequestResponse> response = requests.map(DepartmentMapper::toAssetRequestResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user-requests/{id}/approve")
    public ResponseEntity<?> approveUserRequest(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            User user = getUser(auth);

            AssetRequest req = requestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Request not found"));

            if (!isManagingDepartment(user, req.getDepartment().getId())) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Not your managed department's request"));
            }

            req.setStatus(ApprovalStatus.APPROVED);
            if (body != null) {
                req.setNotes(body.get("notes"));
            }

            req = requestRepository.save(req);

            return ResponseEntity.ok(DepartmentMapper.toAssetRequestResponse(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/user-requests/{id}/reject")
    public ResponseEntity<?> rejectUserRequest(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            User user = getUser(auth);

            AssetRequest req = requestRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Request not found"));

            if (!isManagingDepartment(user, req.getDepartment().getId())) {
                return ResponseEntity.status(403).body(Map.of(
                        "error", "Not your managed department's request"));
            }

            req.setStatus(ApprovalStatus.REJECTED);
            req.setNotes(body.get("notes"));

            req = requestRepository.save(req);

            return ResponseEntity.ok(DepartmentMapper.toAssetRequestResponse(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}