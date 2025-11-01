package com.cnpmnc.DreamCode.api.reports;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {
    @GetMapping("/health")
    public String health() { return "reports-ok"; }
}
