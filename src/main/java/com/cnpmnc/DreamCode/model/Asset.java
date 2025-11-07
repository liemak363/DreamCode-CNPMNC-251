package com.cnpmnc.DreamCode.model;

import com.cnpmnc.DreamCode.model.converter.StringListConverter;
import com.cnpmnc.DreamCode.model.enumType.AssetStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Asset extends BaseEntity {
    private String name;
    private String location;
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;
    
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> imageKeys;
    
    private Date purchaseDate;
    private Double value;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "asset")
    private List<AssetUsageLog> usageLogs;

    @OneToMany(mappedBy = "asset")
    private List<AssetMaintenanceLog> maintenanceLogs;

    @OneToMany(mappedBy = "asset")
    private List<AssetAccidentLog> accidentLogs;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
}
