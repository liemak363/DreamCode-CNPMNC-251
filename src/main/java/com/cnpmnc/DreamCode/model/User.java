package com.cnpmnc.DreamCode.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User extends BaseEntity {
    private String userName;
    private String password;
    private String avatarKey;

    @ManyToMany(mappedBy = "users")
    private List<AssetUsageLog> assetUsageLogs;

    @OneToMany(mappedBy = "approvedBy")
    private List<AssetUsageLog> approvedAssetUsageLogs;
    @ManyToMany
    private Set<Role> roles;
}