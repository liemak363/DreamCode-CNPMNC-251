package com.cnpmnc.DreamCode.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class AssetRetireLog extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne
    @JoinColumn(name = "retired_by_id", nullable = false)
    private User retiredBy;

    @Column(nullable = false)
    private LocalDate retiredTime;

    private String reason;

    private Double salvageValue;
}
