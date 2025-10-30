package com.cnpmnc.DreamCode.ping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class pingController {
    @RequestMapping("/ping")
    public String ping() {
        return "pong";
    }
}
