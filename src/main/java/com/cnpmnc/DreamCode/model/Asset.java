package com.cnpmnc.DreamCode.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Asset extends BaseEntity {
    private String name;
    private String location;
    private String description;
    private List<String> imageKeys;
    private Date purchaseDate;
    private Double value;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(
            mappedBy = "asset"
    )
    private List<AssetUsageLog> usageLogs;

    @OneToMany(
            mappedBy = "asset"
    )
    private List<AssetMaintenanceLog> maintenanceLogs;

    @OneToMany(
            mappedBy = "asset"
    )
    private List<AssetAccidentLog> accidentLogs;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
