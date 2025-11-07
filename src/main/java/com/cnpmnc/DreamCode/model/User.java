package com.cnpmnc.DreamCode.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Column(name = "user_name", unique = true)
    private String userName;

    private String password;

    private String avatarKey;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(mappedBy = "manager")
    private Department managedDepartment;

    @ManyToMany(mappedBy = "users")
    private List<AssetUsageLog> assetUsageLogs;

    @OneToMany(mappedBy = "approvedBy")
    private List<AssetUsageLog> approvedAssetUsageLogs;

    @OneToMany(mappedBy = "revokedBy")
    private List<AssetRevokeLog> revokedAssetLogs;

    @OneToMany(mappedBy = "retiredBy")
    private List<AssetRetireLog> retiredAssetLogs;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Set<Role> roles;
}