package com.cnpmnc.DreamCode.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>(); // token -> epochMilli expiry

    public void blacklist(String token, long expiresAtEpochMilli) {
        blacklist.put(token, expiresAtEpochMilli);
    }

    public boolean isBlacklisted(String token) {
        Long exp = blacklist.get(token);
        if (exp == null)
            return false;
        if (exp < Instant.now().toEpochMilli()) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
