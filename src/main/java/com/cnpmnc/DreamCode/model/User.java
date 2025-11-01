package com.cnpmnc.DreamCode.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import com.cnpmnc.DreamCode.model.converter.StringListConverter;

@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Column(name = "user_name", unique = true)
    private String userName;

    private String password;

    // Store roles as comma-separated string in a single VARCHAR column
    @Convert(converter = StringListConverter.class)
    @Column(name = "roles")
    private List<String> roles;
    private String avatarKey;

    @ManyToMany(mappedBy = "users")
    private List<AssetUsageLog> assetUsageLogs;

    @OneToMany(mappedBy = "approvedBy")
    private List<AssetUsageLog> approvedAssetUsageLogs;
}
