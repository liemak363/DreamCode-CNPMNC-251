package com.cnpmnc.DreamCode.api.assets;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets")
public class AssetsController {
    @GetMapping("/health")
    public String health() { return "assets-ok"; }
}
