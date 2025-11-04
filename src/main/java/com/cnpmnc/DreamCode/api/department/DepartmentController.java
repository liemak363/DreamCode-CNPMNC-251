package com.cnpmnc.DreamCode.api.department;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    @GetMapping("/health")
    public String health() {
        return "department-ok";
    }
}
